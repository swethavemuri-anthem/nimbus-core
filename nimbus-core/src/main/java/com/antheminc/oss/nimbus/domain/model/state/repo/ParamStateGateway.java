/**
 *  Copyright 2016-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.antheminc.oss.nimbus.domain.model.state.repo;

import java.lang.reflect.Method;

import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;

/**
 * @author Soham Chakravarti
 *
 */
public interface ParamStateGateway extends ParamStateRepository {

	public <T> T getValue(Method readMethod, Object target);	
	public <T> void setValue(Method writeMethod, Object target, T value);
	public <T> T instantiate(Class<T> clazz);

	default <M> M _instantiateOrGet(Param<M> param) {
		M existing = _getRaw(param);
		return (existing==null) ? _instantiateAndSet(param) : existing;
	}
	public <M> M _instantiateAndSet(Param<M> param);
	
	public <P> P _getRaw(Param<P> param);
	public <P> void _setRaw(Param<P> param, P newState);
}
