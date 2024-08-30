package com.moriset.bcephal.loader.service;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SpreadSheetData 
{

	private String repositoryOnServer;
	private List<SheetData> sheetDatas;

	public SpreadSheetData() 
	{
		sheetDatas = new ArrayList<SheetData>();
	}
	
	
}
