package com.run.park.Impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import senseHuge.testgateway.ui.MainActivity;


import com.run.park.entity.DataType;
import com.run.park.entity.DataType_Property;




public class XMLDataTypeImpl  {
	
	private static XMLDataTypeImpl dataTypeImpl = null;
	
	
	
	private  SAXReader reader = null;
	private Document document = null;
	private XMLWriter writer   = null;
	private OutputFormat format = null;
	

	private XMLDataTypeImpl(){
	
		//若存在执行读取 否则不什么多不做
		
			reader = new SAXReader();
			try {
				try {
					document = reader.read(
						MainActivity.ctx.getResources().getAssets().open("DataType.xml")
);
					
					System.out.println(document.asXML());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public static XMLDataTypeImpl getDataTypeImpl(){
		if(dataTypeImpl == null){
			dataTypeImpl = new XMLDataTypeImpl();
		}
		return dataTypeImpl;
	}
	

	
	
	public List<DataType> getXMLDatas() {
		// TODO Auto-generated method stub
		if(document == null)
			return null;
		List<DataType> datas = new ArrayList<DataType>();
		List list = document.selectNodes("/DataTypes/DataType");
		Iterator iter = list.iterator();	
		Element node = null;
		while(iter.hasNext()){
			DataType dataType = new DataType();
			node = (Element) iter.next();
			dataType.setDataType(node.selectSingleNode("Type").getText().trim());
			dataType.setDataName(node.selectSingleNode("Name").getText().trim());
			dataType.setMapTable(node.selectSingleNode("MapTable").getText().trim());
			dataType.setUseRuleName(node.selectSingleNode("UseRule").getText().trim());
			dataType.setDataLength(Integer.parseInt(node.selectSingleNode("DataLength").getText().trim()));
			List paramList = node.selectNodes("Propertys/Property");
			Iterator paramIter = paramList.iterator();
			List<DataType_Property> propertyList = new ArrayList<DataType_Property>();
			while(paramIter.hasNext()){
				 Element param = (Element)paramIter.next();
				 DataType_Property type_Property = new DataType_Property();
				 type_Property.setMean(param.attributeValue("Mean").trim());
				 type_Property.setColTable(param.attributeValue("ColTable").trim());
				 type_Property.setIndex(Integer.parseInt(param.attributeValue("Index").trim()));
				 type_Property.setLength(Integer.parseInt(param.attributeValue("Lenght").trim()));
				 type_Property.setType(param.attributeValue("Type").trim());		
				 propertyList.add(type_Property);
			}
			dataType.setPropertyList(propertyList);
			datas.add(dataType);
		 }
		  
		return datas;
	}
	
	private int mean(String type) {
		// TODO Auto-generated method stub
		if(type.equals("Type"))
			return 1;
		else if(type.equals("Name"))
			return 2;
		else if(type.equals("MapTable"))
			return 3;
		else if(type.equals("UseRule"))
			return 4;
		else if(type.equals("Propertys"))
			return 5;
		else if(type.equals("DataLength"))
			return 6;
		else
		return 0;
	}
	
}
