package cn.itcast.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.mobilesafe.EnterPasswordActivity;
import cn.itcast.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
/**
 * Ӧ�ó����Ź�����
 * @author superboy
 *
 */
public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag = true;
	private AppLockDao dao;
	private Intent lockIntent;
	private List<String> tempStopProtectNames;
	private StopProtectReceiver receiver;
	private LockScreenReceiver lockScreenReceiver;
	private UnLockScreenReceiver unlockScreenReceiver;
	
	private List<String> lockedPacknames;
	
	private AppLockObserver observer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class StopProtectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("StopProtectReceiver", "��ʱֹͣ����");
			String packname = intent.getStringExtra("stopedname");
			tempStopProtectNames.add(packname);
		}

	}
	private class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("LockScreenReceiver", "�ֻ�������");
			tempStopProtectNames.clear();
			flag = false;
			
		}

	}
	
	private class UnLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("LockScreenReceiver", "�ֻ�������");
			flag = true;
			startWatchDog();
		}

	}
	
	private class AppLockObserver extends ContentObserver{

		public AppLockObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i("AppLockObserver","���ݿ�����ݱ仯��.");
			lockedPacknames = dao.findAll();
		}
		
	}
	
	@Override
	public void onCreate() {
	
		
		receiver = new  StopProtectReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("cn.itcast.stop");//ע��һ���Զ���Ĺ㲥������
		registerReceiver(receiver, filter);
		
		lockScreenReceiver = new LockScreenReceiver();
		IntentFilter lockFilter = new IntentFilter();
		lockFilter.addAction(Intent.ACTION_SCREEN_OFF);//�����㲥
		registerReceiver(lockScreenReceiver, lockFilter);
		
		unlockScreenReceiver = new UnLockScreenReceiver();
		IntentFilter unlockFilter = new IntentFilter();
		unlockFilter.addAction(Intent.ACTION_SCREEN_ON);//�����㲥
		registerReceiver(unlockScreenReceiver, unlockFilter);
		
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		tempStopProtectNames = new ArrayList<String>();//��ʱ���ܱ��� ����
		dao = new AppLockDao(this);
		//������������Ӧ�ó������Ϣ ���뵽�ڴ漯������.
		
		lockedPacknames = dao.findAll();
		//ע��һ�����ݱ仯�Ĺ۲���. �۲����ݵı仯.
		observer = new AppLockObserver(new Handler());
		getContentResolver().registerContentObserver(AppLockDao.applockuri, true, observer);
		
		
		lockIntent = new Intent(this, EnterPasswordActivity.class);
		lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//������activity���� ��������
		startWatchDog();

		super.onCreate();
	}

	private void startWatchDog() {
		new Thread() {
			public void run() {
				while (flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(1);// ��ȡ����򿪵�ǰ1������ջ
					RunningTaskInfo taskinfo = infos.get(0);// ��ȡ����򿪵�����
					ComponentName activityinfo = taskinfo.topActivity; // ��ǰ�û�Ҫ������activity.
					String packname = activityinfo.getPackageName();
					Log.i("WATCHDOG",packname);
					// �ж� �жϵ�ǰ����İ����Ƿ�Ҫ������.
					//if (dao.find(activityinfo.getPackageName())) { //��ѯ���ݿ�
					if(lockedPacknames.contains(packname)){
						// Ҫ����.
						// �ж��Ƿ�Ҫ��ʱֹͣ����.
						if (tempStopProtectNames.contains(packname)) {
							// ��ʱֹͣ������...
						} else {
							lockIntent.putExtra("packname",
									packname);
							startActivity(lockIntent);
						}
					}
					//1. ���Ӧ�ó���ɱ����. ���̹����� ɱ�����û�õĽ���.
					//2. 
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
		unregisterReceiver(lockScreenReceiver);
		lockScreenReceiver = null;
		unregisterReceiver(unlockScreenReceiver);
		flag = false;
	}
}
