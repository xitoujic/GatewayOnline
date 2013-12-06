package senseHuge.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import senseHuge.model.PackagePattern;
import senseHuge.testgateway.ui.Fragment_listNode;
import senseHuge.testgateway.ui.MainActivity;
import android.database.Cursor;
import android.util.Log;

import com.example.testgateway.R;

public class ListNodePrepare {
	public void prepare() {
		Thread listNodeThread = new Thread(new MyThread());
		listNodeThread.start();
	}
	class MyThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			prepareData();
		}
	}
	private void prepareData() {

		// TODO Auto-generated method stub
		// 得到数据并解析
		// 按接受时间的降序排列,只查找C1包，因为只有C1包有电压信息
		Cursor cursor = MainActivity.mDb.query("Telosb",
				new String[] { "message" }, "CType=?", new String[] { "C1" },
				null, null, "receivetime DESC");
		while (cursor.moveToNext()) {
			String message = cursor.getString(cursor.getColumnIndex("message"));
//			System.out.println("query--->" + message);
			try {
				// 解析后的数据
				PackagePattern mpp = MainActivity.xmlTelosbPackagePatternUtil
						.parseTelosbPackage(message);
				getTheNodeInfo(mpp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// 在解析后的数据中提取出节点编号，若无重复就存入要显示的节点列表中
		private void getTheNodeInfo(PackagePattern mpp) {
			// TODO Auto-generated method stub
			Iterator<?> it = mpp.DataField.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				if (pairs.getKey().equals("源节点编号")) {
					String id = pairs.getValue().toString();
					if (!Fragment_listNode.nodeId.contains(id)) {
						Fragment_listNode.nodeId.add(id);
						addNodeIntoList();
					}
				}
			}
		}

		// 将节点加入到显示列表中
		private void addNodeIntoList() {
			// TODO Auto-generated method stub
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("图片", R.drawable.ic_launcher);
			for (int i = 0; i < Fragment_listNode.nodeId.size(); i++) {
				item.put("源节点编号", Fragment_listNode.nodeId.get(i));
				computeTheNodePower(Fragment_listNode.nodeId.get(i), item);
			}
			// item.put("节点电压", "11");
			Fragment_listNode.nodeList.add(item);
		}
		/**
		 * @param string
		 *            节点编号的字符串标识
		 * @param item
		 *            待存入的map 得到节点的电量，并存入数据中
		 */
		private void computeTheNodePower(String string, Map<String, Object> item) {
			// TODO Auto-generated method stub
			// 得到该节点的最近4条记录,并计算其电量平均值
			Cursor cursor = MainActivity.mDb.query("Telosb",
					new String[] { "message" }, "NodeID=? AND CType=?",
					new String[] { string, "C1" }, null, null, "receivetime DESC");
			int i = 4;
			int cur = 0;
			float[] powers = new float[i];

			while (cursor.moveToNext() && i > 0) {
				String message = cursor.getString(cursor.getColumnIndex("message"));
				try {
					// 解析后的数据
					PackagePattern mpp = MainActivity.xmlTelosbPackagePatternUtil
							.parseTelosbPackage(message);
					float power = getTheNodePower(mpp);
					powers[cur++] = power;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i--;
			}
			item.put("节点电压", getTheAverage(powers)+"v");
		}

		// 计算电压平均值
		private float getTheAverage(float[] powers) {
			// TODO Auto-generated method stub
			float powerSum = 0;
			for(int i=0; i<powers.length; i++) {
				powerSum+=powers[i];
			}
			float power = (float) (powerSum/powers.length/4096*2.5);
			float  b   =  (float)(Math.round(power*1000))/1000;
			//(这里的100就是2位小数点,如果要其它位,如4位,这里两个100改成10000)
			return b;
		}
		private float getTheNodePower(PackagePattern mpp) {
			// TODO Auto-generated method stub
			Iterator<?> it = mpp.DataField.entrySet().iterator();
			String power = null;//十六进制表示
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				if (pairs.getKey().equals("节点电压")) {
					power = pairs.getValue().toString();
				}
			}
			int powerDeci= 0;
			for(int i=power.length()-1; i>=0; i--) {
				char n = power.charAt(i);
				int num=0;
				if(n=='A') {
					num = 10;
				}else if(n=='B') {
					num = 11;
				}else if(n=='C') {
					num = 12;
				}else if(n=='D') {
					num = 13;
				}else if(n=='E') {
					num = 14;
				}else if(n=='F') {
					num = 15;
				}else
					num = Integer.parseInt(String.valueOf(n));
				
				powerDeci+=num*Math.pow(16, 3-i);
			}
			return powerDeci;
		}

}
