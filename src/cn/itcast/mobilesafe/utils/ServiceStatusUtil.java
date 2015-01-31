package cn.itcast.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
/**
 * �ж� �����Ƿ���
 * @author superboy
 *
 */
public class ServiceStatusUtil {
	/**
	 * �жϷ����Ƿ�������״̬
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviceName){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for(RunningServiceInfo info : infos){
			if(serviceName.equals(info.service.getClassName())){//serviceȫ����ƥ��
				return true;
			}
		}
		return false;
	}
}
