package com.moriset.bcephal.etl.service;

import org.springframework.stereotype.Service;

import com.prowidesoftware.swift.model.SwiftTagListBlock;
import com.prowidesoftware.swift.model.Tag;
import com.prowidesoftware.swift.model.field.Field;
import com.prowidesoftware.swift.model.field.Field20;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;

@Service
public class MT940Service {

	
	public void parseTansaction() {
       
        String msg = "{1:F01AAAABB99BSMK3513951576}" +
                "{2:O9400934081223BBBBAA33XXXX03592332770812230834N}" +
                "{4:\n" +
                ":20:0112230000000890\n" +
                ":25:SAKG800030155USD\n" +
                ":28C:255/1\n" +
                ":60F:C011223USD175768,92\n" +
                ":61:0112201223CD110,92NDIVNONREF//08 IL053309\n" +
                "/GB/2542049/SHS/312,\n" +
                ":61:980623C50000000,NTRFNONREF//9999234\n" +
                "ORDER BK OF NYC FOO CASH RESERVE\n" +
                ":61:980626C200000,NDIVNONREF//9999543\n" +
                ":86:DIVIDEND FOO CORP\n" +
                "PREFERRED STOCK 1ST QUARTER 1998\n" +
                ":61:980625C5700000,NFEX036960//8954321\n" +
                ":86:FOO INC\n" +
                ":62F:C011021USD175879,84\n" +
                ":20:NONREF\n" +
                ":25:4001400010\n" +
                ":28C:58/1\n" +
                ":60F:C140327EUR6308,75\n" +
                ":61:1403270327C3519,76NTRF50RS201403240008//2014032100037666\n" +
                "ABC DO BRASIL LTDA\n" +
                ":86:INVOICE NR. 6000012801 \n" +
                "ORDPRTY : ABC LTDA RUA LIBERO BADARO,293-SAO \n" +
                "PAULO BRAZIL }";
        
        MT940 mt = MT940.parse(msg);
        System.out.println();
        Tag start = mt.getSwiftMessage().getBlock4().getTagByNumber(60);
        Tag end = mt.getSwiftMessage().getBlock4().getTagByNumber(62);
        SwiftTagListBlock loop = mt.getSwiftMessage().getBlock4().getSubBlock(start, end);
        for (int i = 0; i < loop.size(); i++) {
            Tag t = loop.getTag(i);
            if (t.getName().equals("61")) {
                Field61 tx = (Field61) t.asField();
                System.out.println("---------------------------");
                System.out.println("Date: " + tx.getComponent(Field61.DATE));
                System.out.println("Amount: " + tx.getComponent(Field61.AMOUNT));
                System.out.println("Currency: " + tx.getComponent(Field61.FUNDS_CODE));
                System.out.println("DC: " + tx.getComponent(Field61.DEBITCREDIT_MARK));
                System.out.println("Entry date: " + tx.getComponent(Field61.ENTRY_DATE));
                System.out.println("Value date: " + tx.getComponent(Field61.VALUE_DATE));
                System.out.println("Transaction Type: " + tx.getComponent(Field61.TRANSACTION_TYPE));
                System.out.println("Identification: "+tx.getComponent(Field61.IDENTIFICATION_CODE));
                System.out.println("Reference Acc Owner: " + tx.getComponent(Field61.REFERENCE_FOR_THE_ACCOUNT_OWNER));
                /*
                 * look ahead for field 86
                 */
                if (i + 1 < loop.size() && loop.getTag(i + 1).getName().equals("86")) {
                    System.out.println("Description: " + loop.getTag(i + 1).getValue());
                    i++;
                }
            }
        }
        parse();
    }
	
	public void parse() {        
        String msg = "{1:F01AAAABB99BSMK3513951576}" +
                "{2:O9400934081223BBBBAA33XXXX03592332770812230834N}" +
                "{4:\n" +
                ":20:0112230000000890\n" +
                ":25:SAKG800030155USD\n" +
                ":28C:255/1\n" +
                ":60F:C011223USD175768,92\n" +
                ":61:0112201223CD110,92NDIVNONREF//08 IL053309\n" +
                "/GB/2542049/SHS/312,\n" +
                ":62F:C011021USD175879,84\n" +
                ":20:NONREF\n" +
                ":25:4001400010\n" +
                ":28C:58/1\n" +
                ":60F:C140327EUR6308,75\n" +
                ":61:1403270327C3519,76NTRF50RS201403240008//2014032100037666\n" +
                "ABC DO BRASIL LTDA\n" +
                ":86:INVOICE NR. 6000012801 \n" +
                "ORDPRTY : ABC DO BRASIL LTDA RUA LIBERO BADARO,293-SAO \n" +
                "PAULO BRAZIL }";
        
        MT940 mt = MT940.parse(msg);
        System.out.println("****************************************************");
        System.out.println("--- HEADER");
        System.out.println("Message type: " + mt.getMessageType());
        System.out.println("Sender: " + mt.getSender());
        System.out.println("Receiver: " + mt.getReceiver());
        System.out.println();
        
        System.out.println("--- FIELD 20");
        Field20 field20 = mt.getField20();
        System.out.println(Field.getLabel(field20.getName(), mt.getMessageType(), null) + ": " + field20.getReference());
        System.out.println();

        for (Field61 tx : mt.getField61()) {
            System.out.println("--- FIELD 61");
        	System.out.println("Date: " + tx.getComponent(Field61.DATE));
            System.out.println("Amount: " + tx.getComponent(Field61.AMOUNT));
            System.out.println("Currency: " + tx.getComponent(Field61.FUNDS_CODE));
            System.out.println("DC: " + tx.getComponent(Field61.DEBITCREDIT_MARK));
            System.out.println("Entry date: " + tx.getComponent(Field61.ENTRY_DATE));
            System.out.println("Value date: " + tx.getComponent(Field61.VALUE_DATE));
            System.out.println("Transaction Type: " + tx.getComponent(Field61.TRANSACTION_TYPE));
            System.out.println("Identification: "+tx.getComponent(Field61.IDENTIFICATION_CODE));
            System.out.println("Reference Acc Owner: " + tx.getComponent(Field61.REFERENCE_FOR_THE_ACCOUNT_OWNER));
            System.out.println();
        }
    }
	
}
