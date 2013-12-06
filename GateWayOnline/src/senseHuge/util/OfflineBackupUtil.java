package senseHuge.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.widget.Toast;

import senseHuge.model.TelosbPackage;
import senseHuge.service.OfflineBackupService;

public class OfflineBackupUtil {
	
  public static	OfflineBackupService   offlineBackupService = new OfflineBackupService();
  static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
  public boolean upLoadTelsob2WebServer(List<TelosbPackage> list,String path){
	 Boolean result = true;
	 String file =  path+"/Telsob-"+sdf.format(new Date())+"-"+System.currentTimeMillis()+".xml";

	 this.CreatBackUpFile(list, file);
	 if (this.upLoadFile(file)) {
		 result = false;           //上传网络服务器失败
	  }
	 this.deleteBackupFiel(file);
	  return result;
  }
  
  
  public  boolean CreatBackUpFile(List<TelosbPackage> list,String file){
	  File f = new File(file);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}/* else {

			return false;
		}*/
		OutputStream outPut = null;
		try {
			outPut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	  return offlineBackupService.save(list, outPut);
  }
  
  
  
  public boolean deleteBackupFiel(String file){
	  File f = new File(file);
		if (f.exists()) {
			f.delete();
			return true;
			//f.createNewFile();
		} else {

			return false;
		}
	
  }
  
  
  @SuppressLint("NewApi")
public boolean upLoadFile(String fileName){
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
	  
	    String uploadUrl = "http://192.168.10.62:8080/first/UploadServlet";
	    
	    
	    String end = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "******";
	    String srcPath = fileName ;
	    try
	    {
	  /*    System.out.println(srcPath+"*************************************************");
	      srcPath = getFilesDir()+"/san.txt";System.out.println(srcPath);*/
	      URL url = new URL(uploadUrl);
	      HttpURLConnection httpURLConnection = (HttpURLConnection) url
	          .openConnection();
	      httpURLConnection.setDoInput(true);
	      httpURLConnection.setDoOutput(true);
	      httpURLConnection.setUseCaches(false);
	      httpURLConnection.setRequestMethod("POST");
	      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
	      httpURLConnection.setRequestProperty("Charset", "UTF-8");
	      httpURLConnection.setRequestProperty("Content-Type",
	          "multipart/form-data;boundary=" + boundary);

	      DataOutputStream dos = new DataOutputStream(httpURLConnection
	          .getOutputStream());
	      dos.writeBytes(twoHyphens + boundary + end);
	      dos
	          .writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
	              + srcPath.substring(srcPath.lastIndexOf("/") + 1)
	              + "\"" + end);
	      dos.writeBytes(end);
	      /*System.out.println(srcPath+"*************************************************");
	      srcPath = getFilesDir()+"/san.txt";System.out.println(srcPath);*/
	      FileInputStream fis = new FileInputStream(srcPath);
	      byte[] buffer = new byte[8192*1024]; // 8k
	      int count = 0;
	      while ((count = fis.read(buffer)) != -1)
	      {
	        dos.write(buffer, 0, count);

	      }
	      fis.close();

	      dos.writeBytes(end);
	      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
	      dos.flush();

	      InputStream is = httpURLConnection.getInputStream();
	      InputStreamReader isr = new InputStreamReader(is, "utf-8");
	      BufferedReader br = new BufferedReader(isr);
	      String result = br.readLine();

	    //  Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	      dos.close();
	      is.close();

	    } catch (Exception e)
	    {
	      e.printStackTrace();
	    //  setTitle(e.getMessage());
	    }
	  return true;
  }
}
