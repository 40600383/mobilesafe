package cn.itcast.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.itcast.mobilesafe.CallSmsSafeActivity;
import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.db.dao.BlackNumberDao;

import com.android.internal.telephony.ITelephony;
/**
 * ���������ط��� 
 * @author superboy
 *
 */
public class CallSmsFirewallService extends Service {

	public static final String TAG = "CallSmsFirewallService";
	private BlackNumberDao dao;
	private InnerSmsReceiver receiver;
	private TelephonyManager tm;
	private CallStatusBlockListener listener;

	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		receiver = new InnerSmsReceiver();//���������㲥������
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
//		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//`
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(receiver, filter);
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new CallStatusBlockListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/**
	 * �绰������
	 * @author superboy
	 *
	 */
	private class CallStatusBlockListener extends PhoneStateListener {
		long startTime = 0;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// ����״̬
				startTime = System.currentTimeMillis();
				String mode = dao.findMode(incomingNumber);
				if ("0".equals(mode) || "1".equals(mode)) {
					// �Ҷϵ绰.
					endCall();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:// �绰�ҶϺ�Ŀ���״̬.
				if(dao.find(incomingNumber)){
					break;
				}
				long endtime = System.currentTimeMillis();
				long ringingtime = endtime - startTime;
				if (ringingtime < 3000) {
					// ��ʾ�û�ɧ�ŵ绰.
					// notification�ķ�ʽ��ʾ�û�.
					showNotification(incomingNumber);
					// TODO:�Ӻ��м�¼�����Ƴ���Ϣ
					// �۲�ϵͳ�ĺ��м�¼����Ϣ,����������ݱ仯��.
//					 deleteFromCallLog(incomingNumber);

					getContentResolver().registerContentObserver(
							CallLog.Calls.CONTENT_URI, true,
							new CallLogObserver(new Handler(),incomingNumber));

				}
				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}

	}
	/**
	 * ͨ�����ݹ۲�����ɾ��������һ����ĺ��м�¼
	 * @author superboy
	 *
	 */
	private class CallLogObserver extends ContentObserver {
		private String incomingNumber;

		public CallLogObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		// ���۲쵽���ݷ����ı��ʱ�� ���õķ���.
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteFromCallLog(incomingNumber);
			getContentResolver().unregisterContentObserver(this);
		}

	}
	/**
	 * ��������
	 * @author superboy
	 *
	 */
	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");

			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String body = smsMessage.getMessageBody();
				String sender = smsMessage.getOriginatingAddress();
				if (body.contains("fapiao")) {
					Log.i(TAG, "��Ʊ����,����");
					abortBroadcast();//�����Ǹ� ����㲥 
				}
				String mode = dao.findMode(sender);
				//���ݻ��� 0 ȫ������ 1�绰���� 2��������
				if ("0".equals(mode) || "2".equals(mode)) {
					Log.i(TAG, "����������,����");
					abortBroadcast();
				}
			}

		}

	}

	/**
	 *  �Ҷϵ绰
	 */
	public void endCall() {
		try {
			Method method = Class.forName("android.os.ServiceManager")//ͨ�� ���䷽ʽ
					.getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null,
					new Object[] { TELEPHONY_SERVICE });
			ITelephony telephony = ITelephony.Stub.asInterface(binder);
//			telephony.answerRingingCall();  //���� �绰 
			telephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * �Ӻ��м�¼�Ƴ�һ�������
	 * 
	 * @param incomingNumber
	 */
	public void deleteFromCallLog(String incomingNumber) {
		Log.i(TAG, "ɾ�����м�¼:" + incomingNumber);
		Uri uri = Uri.parse("content://call_log/calls");// �õ����м�¼�����ṩ�ߵ�·��
		Cursor cursor = getContentResolver().query(uri, new String[] { "_id" },
				"number=?", new String[] { incomingNumber }, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			getContentResolver().delete(uri, "_id=?", new String[] { id });
		}
		cursor.close();
	}

	/**
	 * ��ʾ����һ����
	 * 
	 * @param incomingNumber
	 *            �绰����
	 */
	public void showNotification(String incomingNumber) {
		// 1.��ȡNotificationManager������:
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 2. ʵ���� Notification:
		Notification notification = new Notification(R.drawable.notification,
				"����������һ������", System.currentTimeMillis());
		// 3. ����notification����ϸ��Ϣ
		Context context = getApplicationContext();
		CharSequence contentTitle = "���ص���һ������";
		CharSequence contentText = "����Ϊ:" + incomingNumber;
		Intent notificationIntent = new Intent(this, CallSmsSafeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.flags = Notification.FLAG_AUTO_CANCEL;//���֮���ȡ��
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		// 4. ��notification����ʾ����
		nm.notify(0, notification);
	}

}
