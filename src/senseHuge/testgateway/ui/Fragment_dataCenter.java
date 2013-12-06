package senseHuge.testgateway.ui;

import com.example.testgateway.R;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Fragment_dataCenter extends ListFragment {
	ListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_data_center, container,
				false);
		list = (ListView) view.findViewById(android.R.id.list);

		showData();

		return view;
	}

	private void showData() {
		// TODO Auto-generated method stub

//		Cursor cursor = MainActivity.mDb.query("Telosb", new String[] {
//				"message", "Ctype", "NodeID", "status", "receivetime" }, null,
//				null, null, null, "receivetime DESC");
		Cursor cursor = MainActivity.mDb.rawQuery("select * from Telosb", null);
		/*
		 * while (cursor.moveToNext()) {
		 * 
		 * }
		 */
		// SimpleAdapter adapter = new SimpleAdapter(this, );
		// Cursor cursor = MainActivity.mDb.

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this.getActivity(), R.layout.data_center_style, cursor,
				new String[] { "message", "Ctype", "NodeID", "status",
						"receivetime" }, new int[] { R.id.DBmessageShow,
						R.id.DBCtypeShow, R.id.DBNodeIDShow, R.id.DBstatusShow,
						R.id.DBreceivetimeShow },
				CursorAdapter.FLAG_AUTO_REQUERY);
		list.setAdapter(adapter);

	}
}
