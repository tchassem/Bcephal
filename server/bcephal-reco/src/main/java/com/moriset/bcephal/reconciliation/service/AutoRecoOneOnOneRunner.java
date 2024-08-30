/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AutoRecoOneOnOneRunner extends AutoRecoMethodRunner {

}
