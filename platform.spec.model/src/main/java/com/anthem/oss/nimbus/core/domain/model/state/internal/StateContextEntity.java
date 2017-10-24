/**
 * 
 */
package com.anthem.oss.nimbus.core.domain.model.state.internal;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.anthem.oss.nimbus.core.domain.definition.Domain;
import com.anthem.oss.nimbus.core.domain.model.config.ParamValue;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Soham Chakravarti
 *
 */
@Domain("#")
@Getter @Setter
public class StateContextEntity {

	public enum MessageType {
		INFO,
		WARNING,
		DANGER,
		SUCCESS;
	}
		
	private String msgText;
	private MessageType msgType; 
	
	private Boolean visible = true;
	private Boolean enabled = true;
	
	private Boolean active = true;
	
	private List<ParamValue> values;
	
	public List<ParamValue> getValues() {
		return values;
		//return Collections.emptyList();
	}
	public void setValues(List<ParamValue> v) {
		if(CollectionUtils.isEmpty(v))
			return;
		
		
		v.stream().forEach(pv->{
			Optional.ofNullable(pv.getCode()).map(String::valueOf).map(s->s.intern())
				.ifPresent(pv::setCode);
			
			Optional.ofNullable(pv.getDesc()).map(String::valueOf).map(s->s.intern())
			.ifPresent(pv::setDesc);
		
			Optional.ofNullable(pv.getLabel()).map(String::valueOf).map(s->s.intern())
			.ifPresent(pv::setLabel);
		});
	}
}
