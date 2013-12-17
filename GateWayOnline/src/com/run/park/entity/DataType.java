package com.run.park.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataType implements Serializable ,Cloneable{
	private static final long serialVersionUID = -5240152692961888096L;
	//入口名字
	private String entryName = null;
	//入口信息
	private String entryInfo = null;
	//
	private String attachedInfo = null;
	private String  dataType      = null;   
	private String  mapTable      = null;    
	private String  dataName      = null;   
	private String  useRuleName   = null;   
	private int  dataLength       = -1;
	private List<DataType_Property> propertyList  = null;
	private String temp = "";
	private String colTemp = "";
	private String valTemp = "";
	private String value = "";
	private String  originalData= "";
	private String datainfo = "";
	private String propertyinfo = "";
	public DataType() {
		super();
	}
	public DataType(String dataType ,String mapTable , 
			String dataName ,String useRule, List<DataType_Property> Propertys){
		this.dataType = dataType;
		this.mapTable = mapTable;
		this.dataName = dataName;
		this.useRuleName = useRule;
		this.propertyList = Propertys;
	
	}


	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public String getDataType() {
		return dataType;
	}

	public String getMapTable() {
		return mapTable;
	}

	public String getDataName() {
		return dataName;
	}

	public String getUseRuleName() {
		return useRuleName;
	}

	
	public List<DataType_Property> getPropertyList() {
		return propertyList;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setMapTable(String mapTable) {
		this.mapTable = mapTable;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public void setUseRuleName(String useRuleName) {
		this.useRuleName = useRuleName;
	}

	public void setPropertyList(List<DataType_Property> propertyList) {
		this.propertyList = propertyList;
	}
	
	public String toInfo(){
		DataType_Property property = null;
		int index = 0;
		while(index < this.propertyList.size()){
			property = propertyList.get(index);
			temp += property.getMean() +" : "+property.getValue()+"\n";
			index++;
		}
		property = null;
		return temp;
		
	}
	public List<DataType_Property> getUnknow(){
		List<DataType_Property> propertys = new ArrayList<DataType_Property>();
		Iterator<DataType_Property > iterators = propertyList.iterator();
		while(iterators.hasNext()){
			DataType_Property temp = iterators.next();
			if(temp.getType().equals("unknown"))  propertys.add(temp);
		}
		return propertys;
	}

	public String toSql(){
		int index = 0;
		int nodetype = Integer.parseInt(this.propertyList.get(1).getValue(),16);
		int moteid =  Integer.parseInt(this.propertyList.get(0).getValue(),16);
		DataType_Property property = null;
		temp = temp + "  insert into  "+this.mapTable;
		
		
		while(index < this.propertyList.size()){
			property = propertyList.get(index);
			//检测是否符合条件
			if(property.getColTable().equals(null) || property.getColTable() == "" || property.getColTable().equals("")) {
				index++;
				continue;
			}
			
			
			switch (mean(property.getType())) {
			case 1:
				if(!this.dataType.equals("C1")){
					
					value = "" + Integer.parseInt(property.getValue(), 16);
				break;
				}else
				if(property.getColTable().equals("CO2_ID")){
					if(moteid<1800){
						//11
						if(nodetype == 1){
							value = "" + (Integer.parseInt(property.getValue(), 16)*0.1445 - 160.4);
							break;
						}
						if(nodetype == 3){
							value = "" + (Integer.parseInt(property.getValue(), 16)*(5/7034) - 13.29);
							break;
						}
						if(nodetype == 7){
							if(Integer.parseInt(property.getValue(), 16) > 983){
								value = "" + (Integer.parseInt(property.getValue(), 16)*(983.04)/983.4 - 13.29);
								break;
							}else{
								//(上限-下限)*Math.random()+下限 
								value = "" + (Math.random()*10);
								break;
							}
						}
					}else{
						//21
					}
				}else if(property.getColTable().equals("Humidity_ID")){
					if(moteid<1800){
						value = "" + (Integer.parseInt(property.getValue(), 16)*(5/7034) - 13.29);
						break;
					}else{
						int tem = Integer.parseInt(property.getValue(), 16);
						value = "" + (tem*0.0405 - tem*tem*0.0000028 -4);
						break;
					}
					
				}else if(property.getColTable().equals("Temperature_ID")){
					if(moteid<1800){
					value = "" + (Integer.parseInt(property.getValue(), 16) * 0.01 - 40);
					break;
					}else{
						value = "" + (Integer.parseInt(property.getValue(), 16) * 175.72/65536 - 46.85);
						break;
					}					
				}else if(property.getColTable().equals("Light_ID")){
					if(moteid<1800){
						value = "" + (Integer.parseInt(property.getValue(), 16) * 0.085);
						break;
					}
					
				}
				
					value = "" + Integer.parseInt(property.getValue(), 16);
				
				break;
			case 2:
				value = ""+Long.parseLong(property.getValue(), 16);
				break;
			case 3:
				value ="'"+property.getValue()+"'";
				break;
			default:
				value = "";
				break;
			}
			colTemp += property.getColTable()+" , ";
			valTemp += value+" , ";
			index++;
		}
		if(colTemp.endsWith(" , ")){
			colTemp = colTemp.substring(0, colTemp.lastIndexOf(" , "));
			valTemp = valTemp.substring(0, valTemp.lastIndexOf(" , "));
		}
		temp += "  ( "+colTemp+" )  "+"values"+"  ( "+valTemp+" )  ;";

		property = null;
		return temp;
		
	}
	
	private int mean(String type) {
		// TODO Auto-generated method stub
	if (type.equals("int")) {
		return 1;
	}else if (type.equals("long")) {
		return 2;
	}else if (type.equals("string")) {
		return 3;
	}else {
		return 0;
	}
	}
	public String toData(){
		DataType_Property property = null;
		int index = 0;
		while(index < this.propertyList.size()){
			property = propertyList.get(index);
			temp += property.getColTable() +" : "+property.getValue()+"\n";
			index++;
		}
		property = null;
		return temp;
		
	}
//	public DataType stringToObject(String object){
//	DataType type =	new DataType();
//	String[] temp = object.split(" ");
//	type.setEntryInfo(temp[0]);
//	type.setAttachedInfo(temp[1]);
//	type.setDataType(temp[2]);
//	type.setMapTable(temp[3]);
//	type.setDataName(temp[4]);
//	type.setUseRuleName(temp[5]);
//	type.setDataLength(Integer.parseInt(temp[6]));
//	int index = 7;
//	List<DataType_Property> properties = new ArrayList<>();
//	while(index < object.length() ){
//		DataType_Property type_Property = new DataType_Property();
//		String porperty = temp[index];
//		String[] porpertyTemp = porperty.split("|");
//		type_Property.setMean(porpertyTemp[0]);
//		type_Property.setColTable(porpertyTemp[1]);
//		type_Property.setType(porpertyTemp[2]);
//		type_Property.setIndex(Integer.parseInt(porpertyTemp[3]));
//		type_Property.setLength(Integer.parseInt(porpertyTemp[4]));
//		type_Property.setValue(porpertyTemp[5]);
//		properties.add(type_Property);
//		index++;
//	}
//	type.setPropertyList(properties);
//		return type;
//		
//	}
	public String toOriginalData(){
		originalData += this.entryName;
		originalData +=" "+this.entryInfo;
		originalData +=" "+this.attachedInfo;
		originalData +=" "+ this.dataType +":";
		int index = 0;
		DataType_Property property = null;
		while(index < this.propertyList.size()){
			property = propertyList.get(index);
			originalData += property.getValue();
			index++;
		}
		return originalData;
	}
	public String toString(){
		datainfo = "";
		propertyinfo = "";
		datainfo += this.entryInfo;
		datainfo += " "+this.attachedInfo;
		datainfo += " "+this.dataType;
		datainfo += " "+this.mapTable;
		datainfo += " "+this.dataName;
		datainfo += " "+this.useRuleName;
		datainfo += " "+this.dataLength;
	
		DataType_Property property = null;
		int index = 0;
		while(index < this.propertyList.size()){
			property = propertyList.get(index);
			propertyinfo += " "+property.getMean();
			propertyinfo += ":"+property.getColTable();
			propertyinfo += ":"+property.getType();
			propertyinfo += ":"+property.getIndex();
			propertyinfo += ":"+property.getLength();
			propertyinfo += ":"+ property.getValue();	
			index++;
		}
		return datainfo+propertyinfo;
		
	}
	public String getEntryInfo() {
		return entryInfo;
	}
	public void setEntryInfo(String entryInfo) {
		this.entryInfo = entryInfo;
	}
	public String getAttachedInfo() {
		return attachedInfo;
	}
	public void setAttachedInfo(String attachedInfo) {
		this.attachedInfo = attachedInfo;
	}
	public String getEntryName() {
		return entryName;
	}
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}
	public DataType clone() throws CloneNotSupportedException{
		DataType temp = (DataType) super.clone();
		List<DataType_Property> tempList = new ArrayList<DataType_Property>();
		int index = 0;
		while(index < this.getPropertyList().size() ){
			tempList.add(this.getPropertyList().get(index).clone());
			index++;
		}
		temp.setPropertyList(tempList);
		return temp;
		
	}
}
