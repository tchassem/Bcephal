package com.moriset.bcephal.alarm.domain;
///**
// * 
// */
//package com.moriset.bcephal.dashboard.domain;
//
//import com.moriset.bcephal.dashboard.domain.AlarmLog.AlarmModes;
//import com.moriset.bcephal.domain.BrowserData;
//
///**
// * @author Joseph Wambo
// *
// */
//public class AlarmLogBrowserData extends BrowserData {
//
//	public String condition;
//    public boolean conditionTrue;
//    public String mode;
//    public String audience;
//    public boolean chatSended;
//    public boolean smsSended;
//    public boolean mailSended;
//    public String comment;
//    
//    public AlarmLogBrowserData() {
//    	
//    }
//    
//    public AlarmLogBrowserData(AlarmLog log) {
//    	super(log.getId(), null, log.getCreationDate(), log.getModificationDate());
//    	this.condition = log.getCondition();
//    	this.conditionTrue = log.isConditionTrue();
//    	this.mode = log.getMode() != null ? log.getMode().name() : AlarmModes.M.name();
//    	this.audience = log.getAudience();
//    	this.chatSended = log.isChatSended();
//    	this.smsSended = log.isSmsSended();
//    	this.mailSended = log.isMailSended();
//    	this.comment = log.getComment();
//    }
//	
//}
