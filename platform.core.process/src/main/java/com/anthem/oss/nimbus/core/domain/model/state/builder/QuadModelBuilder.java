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
package com.anthem.oss.nimbus.core.domain.model.state.builder;

import com.anthem.oss.nimbus.core.domain.command.Command;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.Param;
import com.anthem.oss.nimbus.core.domain.model.state.QuadModel;
import com.anthem.oss.nimbus.core.domain.model.state.internal.ExecutionEntity;

/**
 * @author Soham Chakravarti
 *
 */
public interface QuadModelBuilder {

	default <V, C> QuadModel<V, C> build(Command cmd) {
		ExecutionEntity<V, C> eState = new ExecutionEntity<>();
		return build(cmd, eState);
	}
	
	public <V, C> QuadModel<V, C> build(Command cmd, V viewState, Param<C> coreParam);
	
	public <V, C> QuadModel<V, C> build(Command cmd, ExecutionEntity<V, C> eState);
	
}
