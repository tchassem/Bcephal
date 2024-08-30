/**
 * 
 */
package com.moriset.bcephal.reporting.websocket;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.grid.service.AbstractExportUnionGridWebSocket;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExportUnionGridWebSocket extends AbstractExportUnionGridWebSocket {

	
}
