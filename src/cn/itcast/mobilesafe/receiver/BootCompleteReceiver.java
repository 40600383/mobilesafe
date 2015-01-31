package cn.itcast.mobilesafe.receiver;

import cn.itcast.mobilesafe.HomeActivity;
import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.service.CallSmsFirewallService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
/**
 * ���Ŀ����㲥
 * @author superboy
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "�ֻ�������.");
		
		
		//1. 
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification no = new Notification(R.drawable.notification, "������������", System.currentTimeMillis());
		no.flags = Notification.FLAG_NO_CLEAR;//���ú���Ϣ����� ������ʧ
		Intent homeIntent = new Intent(context,HomeActivity.class);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//��activity��������һ��activity����Ҫ��
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, homeIntent, 0);
		no.setLatestEventInfo(context, "����������", "���������ֻ�", contentIntent);
		
		nm.notify(0, no);
		
		/**
		 * ���������������ǽ����
		 */
		Intent callIntent = new Intent(context,CallSmsFirewallService.class);
		context.startService(callIntent);
		
		
		
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String currentSim = tm.getSimSerialNumber();

		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String bindSim = sp.getString("sim", "");

		if (!bindSim.equals(currentSim)) {
			// �ֻ���sim����������.
			// �ж��ֻ��Ƿ��Ǵ��ڷ���������״̬.
			boolean isprotecting = sp.getBoolean("isprotecting", false);
			if (isprotecting) {
				//���ͱ�������.
				SmsManager smsManager =SmsManager.getDefault();
				String safenumber = sp.getString("safenumber", "");//�ɲ�ȡbase64���ܺ����
				smsManager.sendTextMessage(safenumber, null, "sim changed", null, null);
			}
		}
	}

}
