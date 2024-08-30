/**
 * 
 */
package com.moriset.bcephal.billing.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.billing.service.action.BillingActionRunner;
import com.moriset.bcephal.billing.service.action.BillingSendMailRunner;

/**
 * @author EMMENI Emmanuel
 *
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BillingSendMailWebSocket extends BillingActionWebSocket {

	@Autowired
	BillingSendMailRunner billingSendMailRunner;
	
	@Override
	protected BillingActionRunner getActionRunner() {
		return billingSendMailRunner;
	}

}
