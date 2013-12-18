package senseHuge.testgateway.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.testgateway.R;
import com.run.park.Impl.AnalysisDataType;
import com.run.park.entity.DataType;

import senseHuge.Dao.MySQLiteDbHelper;

import senseHuge.listener.Listenable;
import senseHuge.listener.MyEvent;
import senseHuge.listener.MySource;
import senseHuge.model.PackagePattern;
import senseHuge.model.RingBuffer;
import senseHuge.model.TelosbPackage;
import senseHuge.service.LocalConfigService;
import senseHuge.service.OfflineBackupService;
import senseHuge.util.HttpClientUtil;
import senseHuge.util.ListNodePrepare;
import senseHuge.util.OfflineBackupUtil;
import senseHuge.util.SerialUtil;
import senseHuge.util.SocketClientUtil;
import senseHuge.util.XmlTelosbPackagePatternUtil;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android_serialport_api.SerialPort;

public class MainActivity extends FragmentActivity {

	private static final int capibity = 50;
	public static boolean isWork = false;
	private static final String tag = "sensehuge:";
	protected static final String tags = "sensehuge:";
	private static final String Fragment_serialconfig = null;
	public static String serialNum = "/dev/ttyUSB0";
	public static String gatewayName = " first_floor";
	public static	Context ctx ;
	public static  SharedPreferences sp ;
	public static  Editor editor ;
	//public static  boolean socketPermisson = false;
	 public static String SocketKey = "username:key:produceID";
	public static string packagePath = null;

	// public TelosbDao telosbDao;
	public static MySQLiteDbHelper mDbhelper;
	public static SQLiteDatabase mDb;
	public static SerialPort mSerialPort;
	public SocketClientUtil socketClientUtil;
	SerialUtil serialUtil = new SerialUtil();
	HttpClientUtil httpClientUtil;
	// Fragment_listNode fListNode;
	PackagePattern packagePattern = null;
	public static XmlTelosbPackagePatternUtil xmlTelosbPackagePatternUtil;
	public HaveData havadata = null;
	// public ReceiveFromServer receiveFromServer = null ;
	ListNodePrepare listNodePrepare;
	// HaveData havadata = new HaveData();
	List<String> list = new ArrayList<String>();
	int listSingnal = 0;

	public static boolean serverConnect = false;// 服务器是否连接
	public static boolean serialPortConnect = false;// 串口是否连接

	public RingBuffer<String> ringBuffer = new RingBuffer<String>(capibity);
	

	// 调用以下资源的getvalue方法也可以判断当前的连接状态
	MySource ms;// 这个变量应该是多余的，待删除
	MySource httpserverState;
	MySource serialState;
	MySource havePackage;

	FragmentManager manager;
	LinearLayout layout;
	Fragment f_serialPort, f_server, f_listnode, f_nodeSetting, f_alertSetting,
			f_dataCenter, f_aboutUs;
	Button serialPortSetting;
	Button serverSetting;
	Button linkSetting;
	/*
	 * Button sinkSetting; Button alertSetting; Button sinkCheck; Button
	 * internetSetting; Button wifiSetting; Button dataCenter;
	 */
	Button aboutUs;
	Button quit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// fListNode = new Fragment_listNode();

		// 串口，服务器，节点监听事务
		ctx = this.getBaseContext();
		sp = ctx.getSharedPreferences("myconfig", 0);
		editor = sp.edit();
		
		
		manager = getSupportFragmentManager();
		listNodePrepare = new ListNodePrepare();
		FragmentTransaction transaction = manager.beginTransaction();
		f_serialPort = new Fragment_serialconfig();
	
		f_server = new Fragment_serverconfig();
		/*
		 * f_listnode = new Fragment_listNode(); f_nodeSetting = new
		 * Fragment_nodeSetting(); f_alertSetting = new Fragment_alertSetting();
		 */
		f_dataCenter = new Fragment_dataCenter();
		f_aboutUs = new Fragment_aboutUs();

		// 得到按钮以及设置按钮监听器
		serialPortSetting = (Button) findViewById(R.id.serialPortSetting);
		serverSetting = (Button) findViewById(R.id.serverSetting);
		linkSetting = (Button) findViewById(R.id.linkSetting);
		/*
		 * sinkSetting = (Button) findViewById(R.id.sinkSetting); alertSetting =
		 * (Button) findViewById(R.id.alertSetting); sinkCheck = (Button)
		 * findViewById(R.id.sinkCheck); internetSetting = (Button)
		 * findViewById(R.id.internetSetting); wifiSetting = (Button)
		 * findViewById(R.id.wifiSetting); dataCenter = (Button)
		 * findViewById(R.id.dataCenter);
		 */
		aboutUs = (Button) findViewById(R.id.aboutUs);
		quit = (Button) findViewById(R.id.quit);

		serialPortSetting.setOnClickListener(new ButtonClickListener());
		serverSetting.setOnClickListener(new ButtonClickListener());
		linkSetting.setOnClickListener(new ButtonClickListener());
		quit.setOnClickListener(new ButtonClickListener());

		// 默认启动事务：节点
		transaction.add(R.id.fragment_container, f_serialPort);
		transaction.commit();

		init();
		System.out.println("inited:");
	}

	class ButtonClickListener implements OnClickListener {
		FragmentTransaction transaction;

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.serialPortSetting: {
				transaction = manager.beginTransaction();
				transaction.replace(R.id.fragment_container, f_serialPort);
				Log.i(tag, "button1");
				transaction.commit();
				break;
			}
			case R.id.serverSetting: {
				transaction = manager.beginTransaction();
				transaction.replace(R.id.fragment_container, f_server);
				transaction.commit();
				break;
			}
			case R.id.linkSetting: {
				// remember change
			
				startActivity(new Intent(
						android.provider.Settings.ACTION_SETTINGS));
				break;
			}
			case R.id.aboutUs:
				transaction = manager.beginTransaction();
				transaction.replace(R.id.fragment_container, f_aboutUs);
				transaction.commit();
				break;
			case R.id.quit: {
				finish();
				break;
			}
			default: {
				transaction.replace(R.id.fragment_container, f_listnode);
				transaction.commit();
				break;
			}
			}
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		// menu.add(0,1,1,"quit");//类型于asshow = never
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.serialPort_check:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("串口连接状态");
			if (serialPortConnect) {
				dialog.setMessage("已连接");
			} else {
				dialog.setMessage("未连接");
			}
			dialog.show();
			break;
		case R.id.server_check:
			AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
			dialog2.setTitle("服务器连接状态");
			if (serverConnect) {
				dialog2.setMessage("已连接");
			} else {
				dialog2.setMessage("未连接");
			}
			dialog2.show();
			break;
		case R.id.quit:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * mainactivity 初始化
	 */
	@SuppressLint("NewApi")
	public void init() {
		
		String IP = "192.168.10.188";
		String port = "1030";
		if (sp.getString("serverAddr", null) != null) {
			IP =sp.getString("serverAddr", "192.168.10.5");
		}
		if (sp.getString("serverCOM", null) != null) {
			port =sp.getString("serverCOM", "1030");
		}
	
		System.out.println(port);
		socketClientUtil = new SocketClientUtil(IP, Integer.valueOf(port));
	

		httpserverState = new MySource();
		serialState = new MySource();
		ms = new MySource();
		havePackage = new MySource();

		HttpserverState hl = new HttpserverState();
		SerialListener ml = new SerialListener();
		PackageListener pl = new PackageListener();
		httpserverState.addListener(hl);
		serialState.addListener(ml);
		havePackage.addListener(pl);

		
	}
	
		
		
		
	


	public void ProcessData() {
		if (listSingnal == 0) {
			listSingnal = 1;
		} else {
			return;
		}
		while (list.size() > 0) {
			System.out.println("成功接收数据" + list.get(0));
			list.remove(0);
		}
		listSingnal = 0;
	}

	public class SerialListener implements Listenable {
		@Override
		public void eventChanged(MyEvent e) {
			// TODO Auto-generated method stub
			System.out.println("串口‘s e:" + e.getValue());
			if (serialState.value) {
				System.out.println("串口打开：XXXXXXXXXXXXXXXXXXXXXXXXXXX");
			} else {
				System.out.println("串口关闭中。。。。。XXXXXXXXXXXXXXXXXXXXS");
			}
		}
	}

	public class PackageListener implements Listenable {

		@Override
		public void eventChanged(MyEvent e) {
			// TODO Auto-generated method stub
			System.out.println("包's e:" + e.getValue());
			if (ms.value) {
				System.out.println("接收到数据，数据正在出处理。。。。");
				ProcessData();
			} else {
				System.out.println("数据接收完毕，等待中。。。。");
			}
		}
	}

	public class HttpserverState implements Listenable {
		@Override
		public void eventChanged(MyEvent e) {
			// TODO Auto-generated method stub
			System.out.println("server's e:" + e.getValue());
			if (e.getValue()) {
				System.out.println("服务器已连接：");
			} else {
				System.out.println("服务器已断开：");
			}
		}
	}

	
	public class HaveData extends Thread {
		public int count=0;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			// 获取到的数据包
			String telosbData;
			String dataString;
			String ctype ="C1C2C3";
			String headTest = "00FFFF";
			TelosbPackage telosbPackage = new TelosbPackage();
			int i,num=0;
			while (isWork) {
				i = serialUtil.findhead(headTest);
				if (i < 0 && serialUtil.stringBuffer.length() > 6) {
					serialUtil.delete(serialUtil.stringBuffer.length() - 6);
				} else if (i > 0) {
					serialUtil.delete(i);
				}
				if (serialUtil.stringBuffer.length() > 300) {
					System.out
							.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println(serialUtil.stringBuffer.toString());
					System.out
							.println("+++++++++++++++++++++++++++++++++++++++++++++");
					telosbData="";
					telosbData = serialUtil.getFirstData();
					System.out.println("receive package:" + telosbData);
					/**
					 * 向socket 服务器发送数据
					 */

					

				
    				 if (socketClientUtil.isConneted && serverConnect) {
						
						if (telosbData != null &&telosbData.length()>1) {
							dataString = MainActivity.gatewayName + "::"
							+ MainActivity.serialNum + ":"
							+ telosbData.substring(36, 38) + ":"
							+ telosbData.substring(16);
							if (ctype.contains(telosbData.substring(36, 38))) {
								
									System.out.println(telosbData.substring(36, 38));
						
									
									if (telosbData.substring(36, 38).equals("C2")) {
										System.out.println(telosbData.substring(36, 38));
									}
							DataType temp= 	 AnalysisDataType.getAnalysisDataType().analysis(dataString);
							if (temp == null) {
								continue;
							}
								socketClientUtil.sendData(temp);
								
							}
							
						}
						
					} else if (!socketClientUtil.isConneted){
                       
						System.out
								.println("重连服务器***************************************************");
					
						if ( count % 50 == 0) {
							socketClientUtil.reConnectSocket();
						}
						count = count++ % 50;
						
						serialUtil.clear();
					}else if (!serverConnect) {
						socketClientUtil.sendData(SocketKey);
					}

					System.gc();

				}
			}
		}
	}

	
	public HaveData getHaveData() {
		return new HaveData();
	}

	public void Packagesingnal() {
		havePackage.setValue(true);
	}

	public void Packagewaite() {
		havePackage.setValue(false);
	}

	public RingBuffer<String> getRingBuffer() {
		return ringBuffer;
	}

	public void setRingBuffer(RingBuffer<String> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public static String getSerialNum() {
		return serialNum;
	}

	public static void setSerialNum(String serialNum) {
		MainActivity.serialNum = serialNum;
	}
	
  
}
