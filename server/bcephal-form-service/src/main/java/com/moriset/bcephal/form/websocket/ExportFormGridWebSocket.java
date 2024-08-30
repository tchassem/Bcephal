package com.moriset.bcephal.form.websocket;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.grid.service.AbstractExportFormGridWebSocket;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExportFormGridWebSocket extends AbstractExportFormGridWebSocket {

}
