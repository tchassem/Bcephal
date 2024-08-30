/**
 * 
 */
package com.moriset.bcephal.sourcing.grid.socket;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.grid.service.AbstractExportGrilleWebSocket;

/**
 * @author Moriset
 *
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExportGrilleWebSocket extends AbstractExportGrilleWebSocket {

	
}
