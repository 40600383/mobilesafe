package cn.itcast.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.mobilesafe.domain.AppTraffic;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class TrafficManagerActivity extends Activity {
	ProgressBar pb_traffic_manager;
	ListView lv_traffic_manager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_manager);
		 lv_traffic_manager= (ListView)findViewById(R.id.lv_traffic_manager);
		 pb_traffic_manager= (ProgressBar)findViewById(R.id.pb_traffic_manager);
		 
		fillData();
	}

	private void fillData() {
		new AsyncTask<Void, Void, Void>(){

			protected Void doInBackground(Void... params) {
				pb_traffic_manager.setVisibility(View.VISIBLE);
				 List<AppTraffic> appTraffics=	getTrafficCount();
				 lv_traffic_manager.setAdapter(new TrafficAdapter());
				return null;
			}

			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(Void result) {
				pb_traffic_manager.setVisibility(View.INVISIBLE);
				super.onPostExecute(result);
			}

			protected void onProgressUpdate(Void... values) {
				super.onProgressUpdate(values);
			}
			
		};
		
	}
   class TrafficAdapter extends BaseAdapter {

	public int getCount() {
		return 0;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
	   
   }
	private List<AppTraffic> getTrafficCount() {
		PackageManager pm = getPackageManager();
		List<PackageInfo> infos = pm
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);//���� /data/data/��  Ӧ�� �����AndroidManifest.xml�ļ�  �����Ȩ��
		List<AppTraffic> appTraffics=new ArrayList<AppTraffic>();
		
		for (PackageInfo info : infos) {
			String[] permissions = info.requestedPermissions;

			if (permissions != null && permissions.length > 0) {
				for (String p : permissions) {
					if ("android.permission.INTERNET".equals(p)) {//��� ���������Ȩ�� 
						AppTraffic appTraffic=new AppTraffic();
						System.out.println(info.applicationInfo.loadLabel(pm)
								+ "��������.");
						
						appTraffic.setUid(info.applicationInfo.uid);//��ȡϵͳ���� Ӧ�� ��uid
						appTraffic.setName(info.applicationInfo.name);
						appTraffic.setUidRxBytes(TrafficStats.getTotalRxBytes());
						appTraffic.setUidTxBytes(TrafficStats.getTotalTxBytes());
						
						appTraffics.add(appTraffic);
					}
				}
			}
			/*
			TrafficStats.getUidRxBytes(uid); // ��ȡ��������
			TrafficStats.getUidTxBytes(uid); // ��ȡ�ϴ�����.

			TrafficStats.getMobileRxBytes(); // 2g/3g ���ص�������
			TrafficStats.getMobileTxBytes(); //

			TrafficStats.getTotalRxBytes(); // 2g/3g wifi
			TrafficStats.getTotalTxBytes();
			*/
		}
		return  appTraffics;
	}
}
