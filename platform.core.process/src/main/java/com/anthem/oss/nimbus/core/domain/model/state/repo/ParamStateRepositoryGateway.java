/**
 * 
 */
package com.anthem.oss.nimbus.core.domain.model.state.repo;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.anthem.oss.nimbus.core.BeanResolverStrategy;
import com.anthem.oss.nimbus.core.InvalidArgumentException;
import com.anthem.oss.nimbus.core.UnsupportedScenarioException;
import com.anthem.oss.nimbus.core.domain.command.Action;
import com.anthem.oss.nimbus.core.domain.definition.Converters.ParamConverter;
import com.anthem.oss.nimbus.core.domain.definition.Repo.Cache;
import com.anthem.oss.nimbus.core.domain.model.config.ParamConfig.MappedParamConfig;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.ListModel;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.ListParam;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.MappedParam;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.MappedTransientParam;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.Model;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.Param;
import com.anthem.oss.nimbus.core.domain.model.state.InvalidStateException;
import com.anthem.oss.nimbus.core.domain.model.state.StateType;
import com.anthem.oss.nimbus.core.util.JustLogit;
import com.anthem.oss.nimbus.core.utils.JavaBeanHandler;

import lombok.Getter;

/**
 * @author Soham Chakravarti
 *
 */

@Getter
public class ParamStateRepositoryGateway implements ParamStateGateway {

	private JustLogit logit = new JustLogit(getClass());
	
	JavaBeanHandler javaBeanHandler;
	
	private ParamStateRepository local;
	
	//@Autowired	@Qualifier("default.param.state.rep_session")
	private ParamStateRepository session;
	
	private ParamStateRepository detachedStateRepository;

	public ParamStateRepositoryGateway(JavaBeanHandler javaBeanHandler, ParamStateRepository local, BeanResolverStrategy beanResolver) {
		this.javaBeanHandler = javaBeanHandler;
		this.local = local;
		this.detachedStateRepository = beanResolver.get(ParamStateRepository.class, "param.state.rep_detached");
		
	}
	
	/*
	@Autowired	@Qualifier("default.param.state.rep_db")
	private ParamStateRepository db;
	*/
	private ParamStateRepository defaultRepStrategy = new ParamStateRepository() {
		
		/**
		 * Local is always kept, but follows behind cache if configured.
		 * 
		 * 1. If cache=true, then retrieve state from cache AND set to local before returning if local state is different
		 * 2. If cache=false, then 
		 */
		@Override
		public <P> P _get(Param<P> param) {
			if(isCacheable()) {
				P cachedState = session._get(param);
				P localState  = local._get(param);
				if(_equals(cachedState, localState) != null) {
					local._set(param, cachedState);
					localState = cachedState;
				}
				return localState;
			} else {
				P localState  = local._get(param);
				return localState;
			}
		}
		
		@Override
		public <P> Action _set(Param<P> param, P newState) {
			P currState = _get(param);
			if(_equals(newState, currState) == null) return null;
			
			if(isCacheable()) {
				session._set(param, newState);
			}
			//_updateParatmStateTree(param, newState);
			return local._set(param, newState);
		}
		
		public boolean isCacheable() {
			return false;
		}
		
//		public boolean isPersistable() {
//			return true;
//		}
		
		@Override
		public String toString() {
			return "Default Strategy";
		}
	};
	
	@Override
	public <T> T instantiate(Class<T> clazz) {
		return javaBeanHandler.instantiate(clazz);
	}
	
	@Override
	public <T> T getValue(Method readMethod, Object target) {
		return javaBeanHandler.getValue(readMethod, target);
	}
	
	@Override
	public <T> void setValue(Method readMethod, Object target, T value) {
		javaBeanHandler.setValue(readMethod, target, value);
	}
	
	@Override
	public <M> M _instantiateAndSet(Param<M> param) {
		return _instantiateAndSet(defaultRepStrategy, param);
	}
	
	public <M> M _instantiateAndSet(ParamStateRepository currRep, Param<M> param) {
		M newState = instantiate(param.getConfig().getReferredClass());
		_setRaw(param, newState);
		return newState;
	}
	
	@Override
	public <P> P _getRaw(Param<P> param) {
		return _getRaw(defaultRepStrategy, param);
	}
	public <P> P _getRaw(ParamStateRepository currRep, Param<P> param) {
		return currRep._get(param);
	}
	
	@Override
	public <P> void _setRaw(Param<P> param, P newState) {
		_setRaw(defaultRepStrategy, param, newState);
	}
	public <P> void _setRaw(ParamStateRepository currRep, Param<P> param, P newState) {
		currRep._set(param, newState);
	}
	
	/***
	 * 1. get value from rep - could be a composite repository hiding all the write-through cache, 1/2nd/3rd level details
	 */
	@Override
	public <P> P _get(Param<P> param) {
		logit.trace(()->"_get of param: "+param);
		
		Object state = _get(defaultRepStrategy, param);
		if(CollectionUtils.isNotEmpty(param.getConfig().getConverters())) {
			for(ParamConverter converter: param.getConfig().getConverters()) {
				state = converter.serialize(state);
			}
		}
		return (P)state;
	}
	
	public <P> P _get(ParamStateRepository currRep, Param<P> param) {
		if(!param.isMapped())
			return currRep._get(param);
		
		// mapped

		MappedParamConfig<P, ?> mappedParamConfig = param.getConfig().findIfMapped();
		if(mappedParamConfig.isDetachedWithAutoLoad()) {
			
			return mappedParamConfig.getPath().detachedState().cacheState() == Cache.rep_none 
							|| currRep._get(param) == null ? detachedStateRepository._get(param) : currRep._get(param);
		}

		MappedParam<P, ?> mappedParam = param.findIfMapped();
		Param<P> mapsToParam = (Param<P>)mappedParam.getMapsTo();
		
//		if(param.isLeaf())
//			return currRep._get(mapsToParam);
//
//		
//		return currRep._get(param);
		
		if(!mappedParam.requiresConversion()) {
			return currRep._get(mapsToParam);
		}
		
		return currRep._get(param);
	}
	
	@Override
	public <P> Action _set(Param<P> param, P newState) {
		logit.trace(()->"_set of param: "+param+" with entityState: "+newState);
		
		return _set(defaultRepStrategy, param, newState);
	}
	
	public <P> Action _set(ParamStateRepository currRep, Param<P> param, P newState) {
		// unmapped core/view - nested/leaf: set to param
		if(!param.isMapped()) {
			if(newState==null) {
				return currRep._set(param, null);
			}
			
			if(param.isLeaf()) {
				return currRep._set(param, newState);	
				
			} else if(param.isCollection()) {
				ListParam<P> listParam = (ListParam<P>)param.findIfCollection();
				//reset collection
				//_instantiateAndSet(currRep, param);
				listParam.getType().getModel().instantiateAndSet();

				if(!(newState instanceof Collection))
					throw new InvalidArgumentException("Collection param with path: "+param.getPath()+" must have argument of type "+Collection.class);
				
				
				Collection<P> state = (Collection<P>)newState;
				// add element parameters
				Optional.ofNullable(state)
					.ifPresent(list->
						list.stream()
							.forEach(listParam::add)
					);
				
				return Action._new;
				
			} else // scenario: when model is mapped to a type, but param is not -- needs to refer to its root param 
			//if(param.getConfig().getType().findIfNested().getModel().isMapped())
			{
				return _setNestedModel(currRep, param, newState);
				
			} 
//			else {
//				// set nested state as is from passed in domain state
//				return currRep._set(param, newState);
//			}
		}
		
		// set to current param
		//currRep._set(param, newState);
		
		MappedParam<P, ?> mappedParam = param.findIfMapped();
		Param<P> mapsToParam = (Param<P>)mappedParam.getMapsTo();
		
		if(mappedParam.isTransient()) {
			MappedTransientParam<P, ?> mappedTransient = mappedParam.findIfTransient();
			
			// handle unassigned scenario
			if(!mappedTransient.isAssinged()) 
				throw new InvalidStateException("MappedTransientParam must be assigned prior to setting state. "
						+ "Is config missing that needs to assign or re-assign for transientParam: "+mappedTransient);
			
			
			// handle assigned..
				
			Param<?> mapsTo = mappedTransient.getMapsTo();
			
			// handle collection element: add only, edit would be handled automatically if already assigned to an existing colElem
			if(mapsTo.isCollectionElem() && !mapsTo.findIfCollectionElem().getParentModel().contains(mapsTo)) {
				
				// get mapsTo core and add to collection
				ListModel mapsToElemParentModel = mapsTo.findIfCollectionElem().getParentModel();
				mapsToElemParentModel.add(mapsTo.findIfCollectionElem());
			}

			// set to view
			if(mappedTransient.isNested())
				return _setNestedModel(currRep, mappedParam, newState);
			

			
		} /*else if(!param.findIfMapped().requiresConversion() && !param.isCollection()) {
			Object parentModel = param.getParentModel().instantiateOrGet();//ensure mappedFrom model is instantiated
			
			if(CollectionUtils.isNotEmpty(param.getConfig().getConverters())) {
				Collections.reverse(param.getConfig().getConverters());
				Object output = newState;
				for(ParamConverter converter: param.getConfig().getConverters()) {
					output = converter.deserialize(output);
				}
				newState = (P)output;
			}
			return mapsToParam.setState(newState);
			
		}*/ else if(param.isCollection()) {
			if(newState==null) {
				param.getParentModel().instantiateOrGet();//ensure mappedFrom model is instantiated
				return mapsToParam.setState(null);
			}
			

			ListParam<P> mappedListParam = (ListParam<P>)param.findIfCollection();
			
			// reset collection
			//_instantiateAndSet(currRep, param);
			mappedListParam.getType().getModel().instantiateAndSet();

			if(!(newState instanceof Collection))
				throw new InvalidArgumentException("Collection param with path: "+param.getPath()+" must have argument of type "+Collection.class);
			
			Collection<P> newColState = (Collection<P>)newState;
			// add element parameters
			Optional.ofNullable(newColState)
				.ifPresent(list->
					list.stream()
						.forEach(mappedListParam::add)
				);
			return Action._new;
			
		} else if(param.isNested()) {
			// mapped nested: ..handling..  <TypeStateAndConfig.Nested<P>>
			return _setNestedModel(currRep, param, newState);
			
		} else if(param.isLeaf()) {
			Object parentModel = param.getParentModel().instantiateOrGet();//ensure mappedFrom model is instantiated
			
			if(CollectionUtils.isNotEmpty(param.getConfig().getConverters())) {
				Collections.reverse(param.getConfig().getConverters());
				Object output = newState;
				for(ParamConverter converter: param.getConfig().getConverters()) {
					output = converter.deserialize(output);
				}
				newState = (P)output;
			}
			return mapsToParam.setState(newState);
			
		} 
		
		throw new UnsupportedScenarioException("Found param: "+param+" with type that is currently not supported. "
				+ "Follows the order: a)Transient b)Collection c)Nested d)Leaf");
	
	}
	
	protected <P> Action _setNestedModel(ParamStateRepository currRep, Param<P> param, P newState) {
		// if param is mapped && requiresNoConversion, then use mapsToParam
		if(param.isMapped() && !param.findIfMapped().requiresConversion()) {
			Param<P> mapsToParam = (Param<P>)param.findIfMapped().getMapsTo();
			return _setNestedModel(currRep, mapsToParam, newState);
		}
		
		// ensure model is instantiated
		param.findIfNested().instantiateOrGet();
		
//		if(param.isMapped())
//			param.findIfMapped().getMapsTo().findIfNested().instantiateOrGet();
		
		StateType.Nested<P> nestedType = param.getType().findIfNested(); 
		Model<P> nestedModel = nestedType.getModel();
		if(nestedModel.templateParams().isNullOrEmpty()) return null;
		
		// iterate child params
		
		for(Param<? extends Object> childParam : nestedModel.getParams()) {
			
			//PropertyDescriptor pd = (childParam.isMapped() ? childParam.findIfMapped().getMapsTo() : childParam).getPropertyDescriptor();
			PropertyDescriptor pd = childParam.getPropertyDescriptor();
			Object childParamState = javaBeanHandler.getValue(pd, newState);
			_set(currRep, (Param<Object>)childParam, childParamState);
		}
		
		//TODO detect change
		return Action._replace;
	}
	
	public static <P> Action _equals(P newState, P currState) {
		//00
		if(newState==null && currState==null) return null;
		
		//01
		if(newState==null && currState!=null) return Action._delete;
		
		//10
		if(newState!=null && currState==null) return Action._new;
		
		//11
		boolean isEqual = currState.equals(newState);
		return isEqual ? null : Action._update;
	}
	
}
