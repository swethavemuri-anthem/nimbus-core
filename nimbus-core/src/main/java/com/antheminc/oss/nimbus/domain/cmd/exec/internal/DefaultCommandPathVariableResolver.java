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
package com.antheminc.oss.nimbus.domain.cmd.exec.internal;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.antheminc.oss.nimbus.context.BeanResolverStrategy;
import com.antheminc.oss.nimbus.domain.cmd.CommandElement.Type;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandPathVariableResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.ParamPathExpressionParser;
import com.antheminc.oss.nimbus.domain.defn.Constants;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.antheminc.oss.nimbus.domain.session.UserEndpointSession;
import com.antheminc.oss.nimbus.entity.client.user.ClientUser;

/**
 * @author Soham Chakravarti
 *
 */
public class DefaultCommandPathVariableResolver implements CommandPathVariableResolver {

	private final CommandMessageConverter converter;
	
	public DefaultCommandPathVariableResolver(BeanResolverStrategy beanResolver) {
		this.converter = beanResolver.get(CommandMessageConverter.class);
	}
	
	
	@Override
	public String resolve(Param<?> param, String urlToResolve) {
		Map<Integer, String> entries = ParamPathExpressionParser.parse(urlToResolve);
		if(MapUtils.isEmpty(entries))
			return urlToResolve;
		
		String out = urlToResolve;
		for(Integer i : entries.keySet()) {
			String key = entries.get(i);
			
			// look for relative path to passed in param's parent model
			String pathToResolve = ParamPathExpressionParser.stripPrefixSuffix(key);
			
			String val = map(param, pathToResolve);
			
			out = StringUtils.replace(out, key, val, 1);
		}
		
		return out;
	}
	
	protected String map(Param<?> param, String pathToResolve) {
		// handle recursive
		if(ParamPathExpressionParser.containsPrefixSuffix(pathToResolve)) {
			String recursedPath = resolve(param, pathToResolve);
			pathToResolve = recursedPath; 
		}
			
		
		if(StringUtils.startsWithIgnoreCase(pathToResolve, Constants.MARKER_SESSION_SELF.code))
			return mapSelf(param, pathToResolve);
		
		if(StringUtils.startsWithIgnoreCase(pathToResolve, Constants.MARKER_COMMAND_PARAM_CURRENT_SELF.code))
			return StringUtils.removeStart(param.getPath(), param.getRootDomain().getPath());
		
		if(StringUtils.startsWithIgnoreCase(pathToResolve, Constants.MARKER_REF_ID.code))
			return param.getRootExecution().getRootCommand().getRefId(Type.DomainAlias);

		if(StringUtils.startsWithIgnoreCase(pathToResolve, Constants.MARKER_ELEM_ID.code)) 
			return mapColElem(param, pathToResolve);
		
		return mapQuad(param, pathToResolve);
	}
	
	//TODO bean path evaluation to get value
	protected String mapSelf(Param<?> param, String pathToResolve) {
		if(StringUtils.endsWith(pathToResolve, "loginId"))
			return Optional.ofNullable(UserEndpointSession.getStaticLoggedInUser()).orElseGet(() -> new ClientUser()).getLoginId();
		if(StringUtils.endsWith(pathToResolve, "id"))
			return Optional.ofNullable(UserEndpointSession.getStaticLoggedInUser()).orElseGet(() -> new ClientUser()).getId();
		
		return param.getRootExecution().getRootCommand().getElement(Type.ClientAlias).get().getAlias();
	}
	
	protected String mapQuad(Param<?> param, String pathToResolve) {
		if(StringUtils.startsWith(pathToResolve, "json(")) {
			String paramPath = StringUtils.substringBetween(pathToResolve, "json(", ")");
			Param<?> p = param.findParamByPath(paramPath) != null? param.findParamByPath(paramPath): param.getParentModel().findParamByPath(paramPath);
			
			Object state = p.getLeafState();
			String json = converter.convert(state);
			return json;
		} else {
			Param<?> p = param.findParamByPath(pathToResolve) != null? param.findParamByPath(pathToResolve): param.getParentModel().findParamByPath(pathToResolve);
			return String.valueOf(p.getState());
		}
	}

	protected String mapColElem(Param<?> commandParam, String pathToResolve) {
		// check if command param is colElem
		if(commandParam.isCollectionElem())
			return commandParam.findIfCollectionElem().getElemId();
		
		// otherwise, if mapped, check if mapsTo param is colElem
		if(commandParam.isMapped())
			return mapColElem(commandParam.findIfMapped().getMapsTo(), pathToResolve);
		
		// throw ex ..or.. blank??
		return "";
	}
}
