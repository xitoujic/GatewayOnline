package senseHuge.testgateway.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.testgateway.R;

import senseHuge.model.PackagePattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Fragment_listNode extends Fragment {
	// 界面显示的节点数据
	public static List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
	// 查找出来的显示的不重复的节点id
	public static List<String> nodeId = new ArrayList<String>();
	ListView packageList;
	View dialog;
	TextView packageAfterParse;
	// dialog显示出来的当前节点的所有包的内容
	List<Map<String, String>> content;
	GridView gridview;

	// 右侧包内容显示
	/*
	 * FragmentManager fManager; Fragment packageDetialFragment;
	 * FragmentTransaction fTransaction;
	 */

	// List<String> powerList = new ArrayList<String>();
	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
//		init();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// fManager = this.getChildFragmentManager();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_node, container,
				false);
		gridview = (GridView) view.findViewById(R.id.gridview);
		show();
		return view;
	}
	private void show() {
		// TODO Auto-generated method stub
		// 实例化一个适配器
		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), nodeList,
				R.layout.list_node_page_style, new String[] { "图片", "源节点编号",
						"节点电压" }, new int[] { R.id.listNodeImage,
						R.id.listNodeId, R.id.listNodePower });
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new MyItemClickListener());
	}

	class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			dialog = LayoutInflater.from(arg1.getContext()).inflate(
					R.layout.list_node_package_style, null);

			packageAfterParse = (TextView) dialog
					.findViewById(R.id.packageRight);

			getThePackage(nodeId.get(arg2));
			AlertDialog.Builder d = new AlertDialog.Builder(arg1.getContext());
			d.setTitle("第" + (arg2 + 1) + "个节点中的包，节点ID是：" + nodeId.get(arg2))
					.setView(dialog).setPositiveButton("确定", null);

			// 初始化右侧包显示

			/*
			 * packageDetialFragment = Fragment_packageDetail.newInstance(0);
			 * fTransaction = getFragmentManager().beginTransaction();
			 * fTransaction.add(R.id.packageRight, packageDetialFragment);
			 * fTransaction.commit();
			 */
			d.show();

		}

		// 得到相应节点的全部包，参数为节点ID
		private void getThePackage(String string) {
			// TODO Auto-generated method stub
			Cursor cursor = MainActivity.mDb.query("Telosb", new String[] {
					"Ctype", "status", "message" }, "NodeID=?",
					new String[] { string }, null, null, "receivetime DESC");
			content = new ArrayList<Map<String, String>>();
			Map<String, String> data;
			while (cursor.moveToNext()) {
				data = new HashMap<String, String>();
				String message = cursor.getString(cursor
						.getColumnIndex("message"));
				String status = cursor.getString(cursor
						.getColumnIndex("status"));
				String type = cursor.getString(cursor.getColumnIndex("Ctype"));

				data.put("type", type);
				data.put("status", status);
				data.put("message", message);
				content.add(data);
			}
			putDataIntoPackage(content);
		}

		private void putDataIntoPackage(List<Map<String, String>> content) {
			// TODO Auto-generated method stub
			packageList = (ListView) dialog.findViewById(android.R.id.list);
			SimpleAdapter adapter = new SimpleAdapter(dialog.getContext(),
					content, R.layout.list_node_package, new String[] { "type",
							"status", "message" }, new int[] {
							R.id.packageType, R.id.packageStatus,
							R.id.packageMessage });
			packageList.setAdapter(adapter);

			// 设置列表点击事件
			packageList.setOnItemClickListener(new MyListItemClickListener());
		}

		class MyListItemClickListener implements OnItemClickListener {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Map<String, String> packageMessage = content.get(arg2);
				Iterator<?> it = packageMessage.entrySet().iterator();
				String message;
				PackagePattern pp;
				// 取出当前需要解析的数据
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					if (pairs.getKey().equals("message")) {
						message = pairs.getValue().toString();
						try {
							pp = MainActivity.xmlTelosbPackagePatternUtil
									.parseTelosbPackage(message);
							showTheParsedPackage(pp);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}

			private void showTheParsedPackage(PackagePattern pp) {
				// TODO Auto-generated method stub
				Iterator<?> it = pp.DataField.entrySet().iterator();
				StringBuffer sb = new StringBuffer();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					sb.append(pairs.getKey().toString() + ": "
							+ pairs.getValue().toString());
					sb.append("\n");
				}
				packageAfterParse.setText(sb.toString());
			}
		}

	}
	
}
