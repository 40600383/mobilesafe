package cn.itcast.mobilesafe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobilesafe.db.dao.AppLockDao;
import cn.itcast.mobilesafe.domain.AppInfo;
import cn.itcast.mobilesafe.engine.AppInfoProvider;
/**
 * �������
 * @author superboy
 *
 */
public class AppManagerActivity2 extends Activity implements OnClickListener {
	protected static final String TAG = "AppManagerActivity2";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_appmanger;
	private View ll_loading;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private AppManagerAdapter adapter;

	private TextView tv_app_manager_status;

	private PopupWindow popupwindow;

	// ���������Ŀ ������Ķ���
	private AppInfo appinfo;
	
	//�����������ݿ���ʰ�����
	private AppLockDao dao;

	//�����ĳ���ļ���.
	private List<String> lockedPacknames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager2);
		dao = new AppLockDao(this);
		lockedPacknames = dao.findAll();
		
		ll_loading = findViewById(R.id.ll_loading);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		lv_appmanger = (ListView) findViewById(R.id.lv_appmanger);
		
		tv_avail_rom.setText("�����ڴ�:" + getAvailRom());
		tv_avail_sd.setText("����SD��:" + getAvailSD());
		
		tv_app_manager_status = (TextView) findViewById(R.id.tv_app_manager_status);
		fillData();

		// 1.��listviewע��һ�������Ĳ˵�.
		registerForContextMenu(lv_appmanger);

		lv_appmanger.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismisPopupWindow();
			}

			/**
			 * 
			 * @param view
			 * @param firstVisibleItem��һ���û��ɼ�����Ŀ��λ��
			 *            .
			 * @param visibleItemCount
			 * @param totalItemCount
			 */
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismisPopupWindow();//����ʱ  dismiss��
				int position = lv_appmanger.getFirstVisiblePosition();
				if (userAppInfos != null && systemAppInfos != null) {
					if (position == 0) {
						tv_app_manager_status.setVisibility(View.INVISIBLE);
					} else if (position == userAppInfos.size() + 1) {
						tv_app_manager_status.setVisibility(View.INVISIBLE);
					} else if (position < userAppInfos.size() + 1) {
						tv_app_manager_status.setVisibility(View.VISIBLE);
						tv_app_manager_status.setText("�û�����("
								+ userAppInfos.size() + "��)");
					}

					else {
						tv_app_manager_status.setVisibility(View.VISIBLE);
						tv_app_manager_status.setText("ϵͳ����("
								+ systemAppInfos.size() + "��)");
					}
				}
			}
		});

		lv_appmanger.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_appmanger.getItemAtPosition(position);//����� ������� getItem
				if (obj != null) {
					dismisPopupWindow();

					appinfo = (AppInfo) obj;
					Log.i(TAG, "������İ���:" + appinfo.getPackname());
					View popupView = View.inflate(getApplicationContext(),
							R.layout.popup_item, null);
					// �ֱ�ע�����¼�
					popupView.findViewById(R.id.ll_share).setOnClickListener(
							AppManagerActivity2.this);
					popupView.findViewById(R.id.ll_start).setOnClickListener(
							AppManagerActivity2.this);
					popupView.findViewById(R.id.ll_uninstall)
							.setOnClickListener(AppManagerActivity2.this);
						
//					new PopupWindow(View contentView, int width, int height, boolean focusable)  
					//�������� ������Դ������Ч
					//���ò���4Ϊtrueʱ ����popwʧȥ�����رմ��� 
					popupwindow = new PopupWindow(popupView,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					popupwindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));

					int[] location = new int[2];
					view.getLocationInWindow(location);//view��ȡ��ǰview�����λ�� 
					popupwindow.showAtLocation(view,
							Gravity.TOP | Gravity.LEFT, location[0] + 60,
							location[1]);

					ScaleAnimation sa = new ScaleAnimation(0.2f, 1.0f, 0.2f,
							1.0f);
					sa.setDuration(200);
					popupView.startAnimation(sa);
				}
			}
		});

	}

	/**
	 * �������
	 */
	private void fillData() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				List<AppInfo> appinfos = AppInfoProvider
						.getAppInfos(getApplicationContext());
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appinfos) {
					if (info.isUserapp()) {
						userAppInfos.add(info);
					} else {
						systemAppInfos.add(info);
					}
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				ll_loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				ll_loading.setVisibility(View.INVISIBLE);
				adapter = new AppManagerAdapter();
				lv_appmanger.setAdapter(adapter);
				super.onPostExecute(result);
			}

		}.execute();

	}

	private class AppManagerAdapter extends BaseAdapter {

		public int getCount() {
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		public Object getItem(int position) {
			AppInfo appinfo;
			if (position == 0 || position == (userAppInfos.size() + 1)) {
				return null;
			} else if (position <= userAppInfos.size()) {
				// �û�����
				int newpostion = position - 1;
				appinfo = userAppInfos.get(newpostion);
			} else {
				// ϵͳ����
				int newposition = position - 1 - userAppInfos.size() - 1;
				appinfo = systemAppInfos.get(newposition);
			}
			return appinfo;
		}

		public long getItemId(int position) {
			return 0;
		}

		@Override
		public boolean isEnabled(int position) {
			if (position == 0 || position == (userAppInfos.size() + 1))
				return false;

			return super.isEnabled(position);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appinfo;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("�û�����(" + userAppInfos.size() + ")��");
				tv.setTextColor(Color.BLACK);
				tv.setBackgroundColor(R.color.gray);
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.BLACK);
				tv.setBackgroundColor(R.color.gray);
				tv.setText("ϵͳ����(" + systemAppInfos.size() + ")��");
				return tv;
			} else if (position <= userAppInfos.size()) {
				// �û�����
				int newpostion = position - 1;
				appinfo = userAppInfos.get(newpostion);
			} else {
				// ϵͳ����
				int newposition = position - 1 - userAppInfos.size() - 1;
				appinfo = systemAppInfos.get(newposition);
			}

			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_app_item, null);
				holder.iv = (ImageView) view
						.findViewById(R.id.iv_app_item_icon);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_item_location);
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_app_item_name);
				holder.tv_version = (TextView) view
						.findViewById(R.id.tv_app_item_version);
				holder.iv_status = (ImageView)view.findViewById(R.id.iv_app_item_lock_status);
				view.setTag(holder);

			}

			holder.iv.setImageDrawable(appinfo.getIcon());
			if (appinfo.isInrom()) {
				holder.tv_location.setText("�ֻ��ڴ�");
			} else {
				holder.tv_location.setText("SD��");
			}
			holder.tv_name.setText(appinfo.getName());
			holder.tv_version.setText(appinfo.getVersion());
			
		//	if(dao.find(appinfo.getPackname())){ ��ѯ���ݿ� Ч�ʵ�
			if(lockedPacknames.contains(appinfo.getPackname())){
				holder.iv_status.setImageResource(R.drawable.lock);
			}else{
				holder.iv_status.setImageResource(R.drawable.unlock);
			}
			
			return view;
		}

	}

	static class ViewHolder {
		ImageView iv;
		TextView tv_name;
		TextView tv_location;
		TextView tv_version;
		ImageView iv_status;
	}

	/**
	 * ��ȡ�ֻ�sd�����õĿռ�
	 * 
	 * @return
	 */
	private String getAvailSD() {
		File path = Environment.getExternalStorageDirectory();//sd��
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize(); // ��ȡ��ÿһ��ռ�洢���ݵĴ�С
		long availableBlocks = stat.getAvailableBlocks();// �õ����õ�sd�ռ�ĸ���

		long size = blockSize * availableBlocks;
		return Formatter.formatFileSize(this, size);
	}

	/**
	 * ��ȡ�ֻ�Rom���õĿռ�
	 * 
	 * @return
	 */
	private String getAvailRom() {
		File path = Environment.getDataDirectory();//Rom����
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long size = blockSize * availableBlocks;
		return Formatter.formatFileSize(this, size);
	}

	private void dismisPopupWindow() {
		if (popupwindow != null && popupwindow.isShowing()) {
			popupwindow.dismiss();
			popupwindow = null;
		}
	}

	public void onClick(View v) {
		// �رյ������Ĵ���
		dismisPopupWindow();
		switch (v.getId()) {
		case R.id.ll_share:
			shareApk();
			break;

		case R.id.ll_start:
			startApk();
			break;
		case R.id.ll_uninstall:
			if (appinfo.isUserapp()) {
				uninstallApk();
			} else {
				Toast.makeText(this, "ϵͳӦ����ҪrootȨ�޲���ж��", 0).show();
			}
			break;
		}

	}

	/**
	 * ����Ӧ�ó��� �ʼ�,����,����΢�� ��Ѷ΢�� ....
	 */
	private void shareApk() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ�����,����Ϊ:" + appinfo.getName()
				+ "���ص�ַ,url:http://xxx.xxx");
		startActivity(intent);
	}

	/**
	 * ����һ��Ӧ�ó���
	 * <activity
            android:name=".SplashActivity"
	 */
	private void startApk() {
		// ��ȡ��ǰӦ�ó���ĵ�һ��activity.
		try {
			PackageInfo packinfo = getPackageManager().getPackageInfo(
					appinfo.getPackname(), PackageManager.GET_ACTIVITIES);//��ȡACTIVITIES����Ϣ
			ActivityInfo[] infos = packinfo.activities;
			if (infos != null && infos.length > 0) {
				ActivityInfo activityinfo = infos[0];
				String classname = activityinfo.name;
				Intent intent = new Intent();
				intent.setClassName(appinfo.getPackname(), classname);//һ��Ӧ�� ������һ��Ӧ�� 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//���� Ҫ���� ���flag
				startActivity(intent);
			} else {
				Toast.makeText(this, "�޷�������ǰӦ��", 0).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "�޷�������ǰӦ��", 0).show();
		}

	}

	/**
	 * ж�ص�ϵͳ��һ��Ӧ��
	 */
	private void uninstallApk() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appinfo.getPackname()));
		startActivityForResult(intent, 0);//1ж�� �� ˢ�µ�ǰ�б�  2��д onActivityResult���� ���°� ����
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}

	// 2. �������Ĳ˵���������ʱ�� ���õķ���.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.app_lock_menu, menu);
	}

	// 3.ʵ�������Ĳ˵��ĵ���¼�

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// �õ���ǰ�����˵���Ӧ����Ŀ��λ��.
		int position =  info.position;
		Log.i(TAG,"λ��:"+position);
		AppInfo appinfo = (AppInfo) lv_appmanger.getItemAtPosition(position);
		
		switch (item.getItemId()) {
		case R.id.item_add_to_lock:
			Log.i(TAG,"�������:"+appinfo.getPackname());
			if(dao.find(appinfo.getPackname())){
				Toast.makeText(getApplicationContext(), "�Ѿ�����.", 1).show();
				return true;
			}
			dao.add(appinfo.getPackname());
			//�������ݿ� ���ڴ滺���ͬ��
			lockedPacknames.add(appinfo.getPackname());
			adapter.notifyDataSetChanged();//���½���
			return true;
		case R.id.item_remove_from_lock:
			Log.i(TAG,"�Ƴ�����:"+appinfo.getPackname());
			dao.delete(appinfo.getPackname());
			lockedPacknames.remove(appinfo.getPackname());
			adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}
