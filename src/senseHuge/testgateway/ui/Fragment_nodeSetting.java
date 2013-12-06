package senseHuge.testgateway.ui;

import java.io.IOException;

import com.example.testgateway.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Fragment_nodeSetting extends Fragment {
	// 节点设置
	public MainActivity ma;
	private boolean isAvalable;//标识串口是否已连接
	private int cycle;//标识设置的周期时间

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		ma = (MainActivity) getActivity();
		//默认发包周期是5秒
		cycle = 5;
		if (ma.mSerialPort == null) {
			isAvalable = false;
		} else {
			isAvalable = true;
		}
		
		View v = inflater.inflate(R.layout.fragment_node_setting, container,
				false);

		Spinner spinner = (Spinner) v.findViewById(R.id.sendCycleSelect);
		ArrayAdapter<CharSequence> LTRadapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.nodeSettingCycle,
						android.R.layout.simple_spinner_item);
		LTRadapter
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner.setAdapter(LTRadapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				System.out.println("pos---->" + arg2);
				setCycle(arg2);
				
				if(isAvalable) {
					try {
						ma.mSerialPort.getOutputStream().write(cycle);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			/**
			 * @param arg2 用户选择
			 * 设置发包周期，以秒为单位
			 */
			private void setCycle(int arg2) {
				// TODO Auto-generated method stub
				if(arg2 == 0){
					cycle = 5;
				}else if(arg2 == 1) {
					cycle = 10;
				}else if(arg2 == 2) {
					cycle = 30;
				}else if(arg2 == 3) {
					cycle = 1*60;
				}else if(arg2 == 4) {
					cycle = 2*60;
				}else if(arg2 == 5) {
					cycle = 5*60;
				}else if(arg2 == 6) {
					cycle = 10*60;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		return v;
	}
}
