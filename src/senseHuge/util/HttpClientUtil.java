package senseHuge.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

public class HttpClientUtil extends Thread {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}

	private static final String tag = "HttpClient";
	HttpClient httpClient;
	Context context;
	public HttpClientUtil(Context context) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
	}
	
	@SuppressLint("NewApi")
	public Boolean PostTelosbData(String url,String data){
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
		/*
		HttpClient httpClient;
		httpClient = new DefaultHttpClient();*/
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
		
		url ="http://192.168.10.107:8080/foo/login.jsp";
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String name ="1";
		params
		.add(new BasicNameValuePair("name", name));
		String pass ="1";
		params
		.add(new BasicNameValuePair("pass", pass ));
		params
		.add(new BasicNameValuePair("data", data ));
		
		
		
		
		try
		{
			// 设置请求参数
			post.setEntity(new UrlEncodedFormEntity(
					params, HTTP.UTF_8));
			// 发送POST请求
			HttpResponse response = httpClient
					.execute(post);
			// 如果服务器成功地返回响应
			if (response.getStatusLine()
					.getStatusCode() == 200)
			{
				String msg = EntityUtils
						.toString(response.getEntity());
				// 提示登录成功
			/*	Toast.makeText(this.context,
						msg, 5000).show();*/
			//	System.out.println("hello:"+msg);
				Log.i(tag, "httpclient send sucess"+msg);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
		
	}

}
