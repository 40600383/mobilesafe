package cn.itcast.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KillTaskRecevicer extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("KillTaskRecevicer","�Զ���ɱ������Ĺ㲥o");//����  ���� �ڴ�
		
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo>  infos = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info : infos){
			am.killBackgroundProcesses(info.processName);
		}
	
	}

}
