package cn.itcast.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.engine.GPSInfoProvider;
/**
 * ����������
 * @author superboy
 *
 */
public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"���ŵ�����");
		
		Object[] objs = (Object[]) intent.getExtras().get("pdus");//�õ�ϵͳ���Ź㲥��������

		for(Object obj: objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();
			String sender = smsMessage.getOriginatingAddress();
			
			if("#*location*#".equals(body)){
				Log.i(TAG,"�����ֻ���λ��");
				String address = GPSInfoProvider.getInstance(context).getAddress();
				if(!TextUtils.isEmpty(address)){
					SmsManager manager = SmsManager.getDefault();
					manager.sendTextMessage(sender, null, address, null, null);
				}
				abortBroadcast();//���Ź㲥�Ǹ�����㲥 
			}else if("#*alarm*#".equals(body)){
				Log.i(TAG,"���ű�������");
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);//����2������Դ
				player.setVolume(1.0f, 1.0f);//������������
				player.start();
				abortBroadcast();
			}else if("#*wipedata*#".equals(body)){//DevicePolicyManager��Ҫϵͳ���ڹ���ԱȨ��
				Log.i(TAG,"����ֻ�����");
				DevicePolicyManager  dm =	(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dm.wipeData(0);
				
				abortBroadcast();
			}else if("#*lockscreen*#".equals(body)){
				Log.i(TAG,"�����ֻ�");
				DevicePolicyManager  dm =	(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dm.resetPassword("321", 0);
				dm.lockNow();
				
				abortBroadcast();//��ֹ�㲥
			}
		}
	}

}
