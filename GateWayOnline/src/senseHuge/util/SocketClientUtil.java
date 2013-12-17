package senseHuge.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.run.park.Impl.AnalysisDataType;
import com.run.park.entity.DataType;
import com.run.park.entity.User;

import senseHuge.model.TelosbPackage;
import senseHuge.testgateway.ui.MainActivity;


import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

public class SocketClientUtil extends Thread {
//	public List<String> cmds = new ArrayList<String>();;
	public Context context;
    private String IP;
    private int port;
    public boolean isConneted = false;
    private Socket s;
    public static boolean status = true;
    ObjectOutputStream  os;
    ObjectInputStream is;
 

	public SocketClientUtil(String iP, int port) {
		super();
		IP = iP;
		this.port = port;
		s = getSocket(IP,this.port);
		
	}
 
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
	
	public boolean reConnectSocket(){
		System.out.println("reConnectSocket");
	    return this.getSocket(IP, port) != null?true:false;
		
	}
	
	@SuppressLint("NewApi")
	public Socket getSocket(String iP, int port){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads()
		.detectDiskWrites()
		.detectNetwork()   // or .detectAll() for all detectable problems
		.penaltyLog()
		.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects()
		.detectLeakedClosableObjects()
		.penaltyLog()
		.penaltyDeath()
		.build());
		/*iP = "192.168.10.62";
		port = 30000;*/
		try
		{
			
			s = new Socket(); //实例化socket
			SocketAddress socketAddress = new InetSocketAddress(iP,port); //获取sockaddress对象
			s.connect(socketAddress,5000);// 连接socket并设置连接超时为5秒，如果5秒后
		
		
			os =new ObjectOutputStream( s.getOutputStream());
			
			System.out.println("%%$%%%%OK");
		}
		catch (Exception e)
		{
			System.out.println("((((((((((((((((((((((((((((((((((((((");
			e.printStackTrace();
			return null;
		}
		
		System.out.println("started**************");
		isConneted = true;
		User user = new User("junfei", "junfei");
		sendData(user);
		status = true;
		new ReadThread(s).start();
		
		return s;
	}

	public String sendData(Object data){
		try
		{
			// 将用户在文本框内输入的内容写入网络
			os.writeObject(data);
			// 清空input文本框
			os.flush();
			
		}
		catch (Exception e)
		{
			System.out.println("_____________________网络断开连接________________________________");
			System.out.println(new Date().toString());
			System.out.println(e);
			
			try {
				if (os != null) {
					os.close();
				}
				
				if (is!= null) {
					is.close();
				}
				
				if (s != null) {
					s.close();
				}
				isConneted = false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			s = null;
			e.printStackTrace();
		}
		
		
		return "";
	}
	
	public void deleteSocket(){
		if (s != null) {
			status = false;
			try {
				Thread.sleep(100);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (os != null) {
					os.close();
				}
				
				if (is!= null) {
					is.close();
				}
				
				if (s != null) {
					s.close();
				}
				isConneted = false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			s = null;
		
			
			
		}
		
	}
	
	
	public String sendTelsobData(TelosbPackage telosbPackage){
		if (isConneted == false) {
			if(getSocket(IP, port) == null){
				return "fail";
			}
		}
	//	sendData(telosbPackage.getNodeID()+":"+telosbPackage.getCtype()+":"+telosbPackage.getReceivetime()+":"+telosbPackage.getMessage());
	
		
		return "success";
		
	}
	
	public class ReadThread  extends Thread{
		private Socket s;
		ObjectInputStream br = null;
		public ReadThread(Socket s) {
			super();
			this.s = s;
			
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			try {
				br = new ObjectInputStream(s.getInputStream());
			} catch (StreamCorruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			while (status) {
				  System.out.println("THread starting  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
					
					try
					
					{
						
						String infoString = (String)br.readObject();
						System.out.println(infoString+")))))))))))))))))))))");
						
						parseCommand(infoString);

					}
					catch (Exception e)
					{
						
						System.out.println("_____________________网络断开连接________________________________");
						System.out.println(new Date().toString());
						System.out.println(e);
						
						isConneted = true;
						try {
							if (os != null) {
								os.close();
							}
							
							if (is!= null) {
								is.close();
							}
							
							if (s != null) {
								s.close();
							}
							isConneted = false;
							MainActivity.serverConnect = false;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						s = null;
						status = false;
						e.printStackTrace();
					}
				
			}
		}
		
	}



	public void parseCommand(String content){
	    if (content.equals("FF")) {
		    MainActivity.serverConnect = true;
			isConneted = true;
		}else if (content.equals("00")) {
			deleteSocket();
		}else {               // 接收到其他命令  直接发送到串口
			try {
				MainActivity.mSerialPort.getOutputStream().write(
						content.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	
	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}



	public boolean isConneted() {
		return isConneted;
	}

	public void setConneted(boolean isConneted) {
		this.isConneted = isConneted;
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(ObjectOutputStream os) {
		this.os = os;
	}

	public InputStream getIs() {
		return is;
	}


	
	
	
}
