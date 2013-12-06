package senseHuge.util;


public class SerialUtil {
	
	//static String string = "EEE00FFFF00002A0093000030E8002A0050FD6B008CFFBAFD6B008CFFBAFDC8FFE1FE6200004B8100000000000000000000000000FFFF00002A0093000030E8002A0050FD6B008CFFBAFD6B008CFFBAFDC8FFE1FE6200004B81000000000000000000000000";
	
	public String headString = "00FFFF";
	public int headLength;
	public int Length;
	public int type;
	public String SegmentData;
	public StringBuffer stringBuffer = new StringBuffer();
	public int stringBufferLength ;

	public SerialUtil() {
		super();
	//	stringBuffer.append(string);
	}
	
	public SerialUtil(String headString, int headLength) {
		super();
		this.headString = headString;
		this.headLength = headLength;
	}

	public String getFirstData(){
		System.out.println(stringBuffer);
		int start = findhead(headString);
		int packageLength = Integer.parseInt(stringBuffer.substring(start+10, start+12),16);
		int length =2*packageLength+16;
		String string = "";
		if (packageLength == 0) {
			stringBuffer.delete(0, start+16);
		} else {

			if (start+length > stringBuffer.length()) {
				return null;
			}
			string = stringBuffer.substring(start, start+length);
		}
		stringBuffer.delete(0, start+length);
		return 	string;
	}
	
	public int findhead(String headString){
		return stringBuffer.indexOf(headString);
	}
	
	public boolean delete(int start,int end)
	{
		if (end>stringBuffer.capacity()) {
			return false;
		} else {

			stringBuffer.delete(start,end);
			return true;
		}
	}
	public boolean delete(int end)
	{
		if (end>stringBuffer.length()) {
			return false;
		} else {
         System.out.println("end:"+ end + "length:"+stringBuffer.length() );
			stringBuffer.delete(0,end);
			return true;
		}
	}
	
	public void clear(){
		stringBuffer.delete(0, stringBuffer.length());
	}

}
