package com.moriset.bcephal.dashboard.service.socket;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.grid.service.AbstractExportHomeScreenReportSocket;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HomeScreenReportSocket extends AbstractExportHomeScreenReportSocket {

}
