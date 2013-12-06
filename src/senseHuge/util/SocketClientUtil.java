package senseHuge.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import senseHuge.model.TelosbPackage;
import senseHuge.testgateway.ui.MainActivity;


import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

public class SocketClientUtil extends Thread {
	public List<String> cmds = new ArrayList<String>();;
	public Context context;
    private String IP;
    private int port;
    public boolean isConneted = false;
    private Socket s;
    OutputStream os;
    InputStream is;
 

	public SocketClientUtil(String iP, int port) {
		super();
		IP = iP;
		this.port = port;
		s = getSocket(iP,port);
		
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
			/*System.out.println("starting connect socket!!!!!!!!!");
			s = new Socket(iP, port);
			os = s.getOutputStream();


			is = s.getInputStream();
			System.out.println("starting -------------");*/
			
			s = new Socket(); //实例化socket
			SocketAddress socketAddress = new InetSocketAddress(iP,port); //获取sockaddress对象
			s.connect(socketAddress,5000);// 连接socket并设置连接超时为5秒，如果5秒后
			os = s.getOutputStream();


			is = s.getInputStream();
			
			
		
			// 客户端启动ClientThread线程不断读取来自服务器的数据
		//	new Thread(new MainActivity.ClientThread(s)).start(); // ①
			new Thread(new ClientThread(s)).start();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		System.out.println("started**************");
		isConneted = true;
		return s;
	}

	public String sendData(String data){
		try
		{
			// 将用户在文本框内输入的内容写入网络
			os.write(data.getBytes("utf-8"));
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
	
	
	public String sendTelsobData(TelosbPackage telosbPackage){
		if (isConneted == false) {
			if(getSocket(IP, port) == null){
				return "fail";
			}
		}
		sendData(telosbPackage.getNodeID()+":"+telosbPackage.getCtype()+":"+telosbPackage.getReceivetime()+":"+telosbPackage.getMessage());
		
		
		return "success";
		
	}

	
	public class ClientThread implements Runnable
	{
		//该线程负责处理的Socket
		private Socket s;
		private Handler handler;
		//该线程所处理的Socket所对应的输入流
		BufferedReader br = null;
		public ClientThread(Socket s )
			throws IOException
		{
			this.s = s;
		//	this.handler = handler;
			br = new BufferedReader(
				new InputStreamReader(s.getInputStream()));
		}
		
		public void run()
		{
			
			try
			{
				String content = null;
				//不断读取Socket输入流中的内容。
				while ((content = br.readLine()) != null)
				{
		
					System.out.println("content:"+content);
					cmds.add(content);
		
				
				}
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
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				s = null;
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

	public void setOs(OutputStream os) {
		this.os = os;
	}

	public InputStream getIs() {
		return is;
	}

	public List<String> getCmds() {
		return cmds;
	}

	public void setCmds(List<String> cmds) {
		this.cmds = cmds;
	}

	
	
	
}
