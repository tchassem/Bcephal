package com.moriset.bcephal.etl.service.mt;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.etl.domain.ParameterCode;
import com.moriset.bcephal.etl.domain.TransactionItem;
import com.moriset.bcephal.etl.domain.Transactions;
import com.prowidesoftware.swift.model.SwiftTagListBlock;
import com.prowidesoftware.swift.model.Tag;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;

public class MT940ItemProcessor extends MTItemProcessor<MT940, TransactionItem> {

	String entryDateFormat = "MMdd";
	String valueDateFormat = "yyMMdd";
	
	@Override
	public List<TransactionItem> process(MT940 item) throws Exception {
		Transactions transactions = new Transactions();
		transactions.setRunId(getStepExecution().getExecutionContext().getString(ParameterCode.RUN_ID));
		transactions.setFileName(getCurrentFileName());
		transactions.setRunDate(getStepExecution().getExecutionContext().getString(ParameterCode.RUN_DATE, ""));
		transactions.setClient(getStepExecution().getExecutionContext().getString(ParameterCode.CLIENT, ""));
		transactions.setProject(getStepExecution().getExecutionContext().getString(ParameterCode.PROJECT, ""));
		transactions.setUser(getStepExecution().getExecutionContext().getString(ParameterCode.USER, ParameterCode.USER_VALUE));
		
		transactions.setMessageType(item.getMessageType());
		transactions.setReceiver(item.getReceiver());
		transactions.setSender(item.getSender());
		transactions.setReference(null);
		
		
		Tag start = item.getSwiftMessage().getBlock4().getTagByNumber(60);
        Tag end = item.getSwiftMessage().getBlock4().getTagByNumber(62);
        SwiftTagListBlock loop = item.getSwiftMessage().getBlock4().getSubBlock(start, end);
        for (int i = 0; i < loop.size(); i++) {
            Tag t = loop.getTag(i);
            if (t.getName().equals("61")) {      
            	Field61 tx = (Field61) t.asField();
            	TransactionItem transactionItem = transactions.buildNewItem();
            	transactionItem.setDate(formatInToOutDate(tx.getComponent(Field61.DATE), valueDateFormat));
            	transactionItem.setAmount(formatInToOutDecimal(tx.getComponent(Field61.AMOUNT)));
                
				transactionItem.setCurrency(tx.getComponent(Field61.FUNDS_CODE));
				transactionItem.setDc(tx.getComponent(Field61.DEBITCREDIT_MARK));
				transactionItem.setValueDate(formatInToOutDate(tx.getComponent(Field61.VALUE_DATE), valueDateFormat));
				transactionItem.setEntryDate(formatEntryDateFormat(tx.getComponent(Field61.ENTRY_DATE), tx.getComponent(Field61.DATE), valueDateFormat));
				transactionItem.setTransactionType(tx.getComponent(Field61.TRANSACTION_TYPE));
				transactionItem.setIdentification(tx.getComponent(Field61.IDENTIFICATION_CODE));
				transactionItem.setReferenceAccOwner(tx.getComponent(Field61.REFERENCE_FOR_THE_ACCOUNT_OWNER));
                
                if (i + 1 < loop.size() && loop.getTag(i + 1).getName().equals("86")) {
                	transactionItem.setDescription(loop.getTag(i + 1).getValue());
                    i++;
                }
                transactions.getItems().add(transactionItem);
            }            
        }
        return transactions.getItems();
	}
	
	
	protected String formatEntryDateFormat(String entryDateString, String dateString, String format) throws ParseException  {
		if(StringUtils.hasText(dateString) && StringUtils.hasText(entryDateString)) {
			entryDateString = dateString.substring(0, 2) + entryDateString.trim();
		}
		Date entryDate = formatInDate(entryDateString, format);		
		return formatDate(entryDate);
	}
	
}
