package senseHuge.testgateway.ui;

import java.io.IOException;

import com.example.testgateway.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Fragment_alertSetting extends Fragment {
	public MainActivity ma;
	public static int alertPower=15;// Ԥ��ֵ������
	Button musicSelect;
	private static String TAG = "MainActivity";
	private static final int REQUEST_CODE = 1;   //������
	public static String alertMusicPath;//Ԥ����������
	public static final String EXTRA_FILE_CHOOSER = "file_chooser";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		ma = (MainActivity) getActivity();
		View v = inflater.inflate(R.layout.fragment_alert_setting, container,
				false);
		musicSelect = (Button) v.findViewById(R.id.musicSelect);
		Spinner spinner = (Spinner) v.findViewById(R.id.powerSettingSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.powerSetting,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner.setAdapter(adapter);

		musicSelect.setOnClickListener(new MyButtonListener());
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				System.out.println("pos---->" + arg2);
				setAlertPower(arg2);

				if (ma.mSerialPort != null) {
					try {
						ma.mSerialPort.getOutputStream().write(alertPower);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		return v;
	}

	/**
	 * @param arg2
	 *            �û�ѡ�� ����Ԥ���㣬5����5%
	 */
	private void setAlertPower(int arg2) {
		// TODO Auto-generated method stub
		if (arg2 == 0) {
			alertPower = 5;
		} else if (arg2 == 1) {
			alertPower = 10;
		} else if (arg2 == 2) {
			alertPower = 15;
		} else if (arg2 == 3) {
			alertPower = 20;
		} else if (arg2 == 4) {
			alertPower = 25;
		} else if (arg2 == 5) {
			alertPower = 30;
		} else if (arg2 == 6) {
			alertPower = 35;
		} else if (arg2 == 76) {
			alertPower = 40;
		}
	}

	public class MyButtonListener implements OnClickListener {
		String filepath;

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent fileChooserIntent = new Intent(arg0.getContext(),
					FileChooserActivity.class);
			startActivityForResult(fileChooserIntent, REQUEST_CODE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.v(TAG, "onActivityResult#requestCode:" + requestCode
				+ "#resultCode:" + resultCode);
		this.getActivity();
		if (resultCode == FragmentActivity.RESULT_CANCELED) {
			toast("û�д��ļ�");
			return;
		}
		this.getActivity();
		if (resultCode == FragmentActivity.RESULT_OK && requestCode == REQUEST_CODE) {
			// ��ȡ·����
			String musicPath = data.getStringExtra(EXTRA_FILE_CHOOSER);
			Log.v(TAG, "onActivityResult # musicPath : " + musicPath);
			if (musicPath != null) {
				toast("Choose File : " + musicPath);
				alertMusicPath = musicPath;//Ԥ������·������
			} else
				toast("���ļ�ʧ��");
		}
	}
	private void toast(CharSequence hint){
	    Toast.makeText(this.getActivity().getBaseContext(), hint , Toast.LENGTH_SHORT).show();
	}
}
