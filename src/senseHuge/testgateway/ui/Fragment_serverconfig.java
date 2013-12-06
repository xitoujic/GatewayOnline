package senseHuge.testgateway.ui;

import com.example.testgateway.R;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.serverconfig, null);
		button = (Button) view.findViewById(R.id.config);
		button.setOnClickListener(new ButtonClickListener());
		text = (TextView) view.findViewById(R.id.serverAddr);
		return view;
	}

	/**
	 * XXXXXXXXXXXXXXXXXXXXXXXX ≤‚ ‘
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

			//	tt();// ≤‚ ‘
				Context ctx = getActivity();
				SharedPreferences sp = ctx.getSharedPreferences("SP", 0);
				// ¥Ê»Î ˝æ›
				Editor editor = sp.edit();
				if (sp.getString("serverAddr", "none") != null) {
					editor.remove("serverAddr");
				}
				editor.putString("serverAddr", serverAddr);

				editor.commit();
				Log.i("SP", sp.getString("serverAddr", "none"));

				break;
			}

			default: {
				break;
			}
			}
		}
	}
}
