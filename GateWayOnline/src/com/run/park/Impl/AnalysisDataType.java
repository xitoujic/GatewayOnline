package com.run.park.Impl;

import java.util.List;
import com.run.park.entity.DataType;
import com.run.park.entity.DataType_Property;

public class AnalysisDataType{
	private static AnalysisDataType analysisDataType = null;
	private static  List<DataType> workTypeList = null;
	private DataType currentType = null; 	
	private String mean = "";
	private String enterInfo = "";
	private String tempData = "";
	private String datatype = "";
	private String type ="";
	public static AnalysisDataType getAnalysisDataType(){
		if(analysisDataType == null){
			analysisDataType = new AnalysisDataType();
			workTypeList = XMLDataTypeImpl.getDataTypeImpl().getXMLDatas();
		}
		return analysisDataType;
	}

	public synchronized DataType analysis(String take) {
		// TODO Auto-generated method stub
		int jj = take.indexOf("::");
		if(jj<0) 
			return null;
		mean = take.substring(0,jj );
		take = take.substring(jj+2);
		int ii = take.indexOf(":");
		enterInfo = take.substring(0,ii );
		take = take.substring(ii+1);
		int index = 0;
		tempData = "";
		while(index < workTypeList.size()){
			//数据类型一样  长度一样
			int begin = take.indexOf(":");			
			 type= take.substring(0,begin).trim() ;
			datatype = workTypeList.get(index).getDataType().trim() ;
			tempData = take.substring(begin+1);
			int length = tempData.length();
			int totalLength = workTypeList.get(index).getDataLength()*2;
			
			if(type.equals(datatype)  && length ==  totalLength){
			
				currentType = workTypeList.get(index)  ;
				//真实入口
				currentType.setEntryName(enterInfo);
				//虚拟入口（停车场）
				currentType.setEntryInfo(mean);
			
				break;
			}
			index++;
		}
		
		if(index == workTypeList.size()){
			System.out.println("UnKnown DataType："+type +" size："+tempData.length());
			return null;
		}
		index = 0;
		DataType_Property tempProperty;
		while(index < currentType.getPropertyList().size()){
			tempProperty = currentType.getPropertyList().get(index);
			int beginIndex = tempProperty.getIndex()*2;
			int endIndex = (tempProperty.getIndex()+tempProperty.getLength())*2;

			tempProperty.setValue(
					tempData.substring(beginIndex, endIndex)
					);
			index++;
		}	
			try {
				return currentType.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		
	}
	
}
