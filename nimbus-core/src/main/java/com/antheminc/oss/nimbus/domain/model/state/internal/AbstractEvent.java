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
package com.antheminc.oss.nimbus.domain.model.state.internal;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Soham Chakravarti
 *
 */
@Getter @Setter @ToString
public abstract class AbstractEvent<T extends Serializable, P> {

	private String type;
	
	private T id;
	
	private P payload;

	
	public enum SuppressMode {

		ECHO
	}
	
	public enum PersistenceMode {

		ATOMIC,
		BATCH
	}
  
	
    public AbstractEvent() {}
	
	public AbstractEvent(String type, T id, P payload) {
		this.type = type;
		this.id = id;
		this.payload = payload;
	}
	
}
