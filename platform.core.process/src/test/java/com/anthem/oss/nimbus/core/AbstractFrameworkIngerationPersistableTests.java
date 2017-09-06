/**
 * 
 */
package com.anthem.oss.nimbus.core;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.springframework.mock.web.MockHttpServletRequest;

import com.anthem.oss.nimbus.core.domain.command.Action;

import test.com.anthem.nimbus.platform.utils.ExtractResponseOutputUtils;
import test.com.anthem.nimbus.platform.utils.MockHttpRequestBuilder;

/**
 * @author Soham Chakravarti
 *
 */
public abstract class AbstractFrameworkIngerationPersistableTests extends AbstractFrameworkIntegrationTests {
	
	protected static final String CORE_DOMAIN_ALIAS = "sample_core";
	
	protected static final String VIEW_PARAM_ROOT = PLATFORM_ROOT + "/sample_view";
	protected static final String CORE_PARAM_ROOT = PLATFORM_ROOT + "/" + CORE_DOMAIN_ALIAS;
	
	protected static String domainRoot_refId;
	
	public synchronized String createOrGetDomainRoot_RefId() {
		if(domainRoot_refId!=null) 
			return domainRoot_refId;
		
		MockHttpServletRequest home_newReq = MockHttpRequestBuilder.withUri(VIEW_PARAM_ROOT).addAction(Action._new).getMock();
		Object home_newResp = controller.handleGet(home_newReq, null);
		assertNotNull(home_newResp);
		
		domainRoot_refId  = ExtractResponseOutputUtils.extractDomainRootRefId(home_newResp);
		return domainRoot_refId;
	}
	
	@Override @After
	public void tearDown() {
		super.tearDown();
		
		domainRoot_refId = null;
	}
}
