package senseHuge.testgateway.ui;

import com.example.testgateway.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Fragment_serverconfig extends Fragment {
	Button button;
	TextView text;
	TextView textCOM;
   /* public static	Context ctx ;
	public static  SharedPreferences sp ;
	public static  Editor editor ;*/
	// 存入数据
	

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.serverconfig, null);
		
	
		button = (Button) view.findViewById(R.id.config);
		
		button.setOnClickListener(new ButtonClickListener());
		text = (TextView) view.findViewById(R.id.serverAddr);
		textCOM = (TextView) view.findViewById(R.id.serverCOM);
		
		
		initLocalConfig();
	
		return view;
	}
	
	public void initLocalConfig(){
		/*ctx = getActivity();
		sp = ctx.getSharedPreferences("myconfig", 0);
		editor = sp.edit();*/
		
		if (MainActivity.sp.getString("serverAddr", "192.168.0.1") != null) {
			text.setText(MainActivity.sp.getString("serverAddr", "192.168.0.1"));
		}else {
			text.setText("192.168.0.1");
		}
		if (MainActivity.sp.getString("serverCOM", "8080") != null) {
			textCOM.setText(MainActivity.sp.getString("serverCOM", "8080"));
		}else {
			textCOM.setText("1024");
		}
		
	}

	/**
	 * XXXXXXXXXXXXXXXXXXXXXXXX 测试
	 */
	public void tt() {
		Bundle bundle = new Bundle();
		System.out.println(bundle.getSerializable("key"));
		String string = getArguments().getString("key");
		System.out.println(string);
		// this.setArguments(bundle);
	}

	class ButtonClickListener implements OnClickListener {
		FragmentTransaction transaction;

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.config: {
				String serverAddr = text.getText().toString().trim();
				String serverCOM = textCOM.getText().toString().trim();

			//	tt();// 测试
				/*Context ctx = getActivity();
				SharedPreferences sp = ctx.getSharedPreferences("SP", 0);
				// 存入数据
				Editor editor = sp.edit();*/
				if (MainActivity.sp.getString("serverAddr", "none") != null) {
					MainActivity.editor.remove("serverAddr");
				}
				if (MainActivity.sp.getString("serverCOM", "none") != null) {
					MainActivity.editor.remove("serverCOM");
				}
				MainActivity.editor.putString("serverCOM", serverCOM);
				MainActivity.editor.putString("serverAddr", serverAddr);

				MainActivity.editor.commit();
				Log.i("SP", MainActivity.sp.getString("serverAddr", "none"));

				break;
			}

			default: {
				break;
			}
			}
		}
	}
}
