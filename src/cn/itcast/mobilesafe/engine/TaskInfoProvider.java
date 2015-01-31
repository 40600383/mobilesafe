package cn.itcast.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.domain.TaskInfo;

public class TaskInfoProvider {

	public static List<TaskInfo> getTaskInfos(Context context){
		List<TaskInfo> taskinfos = new ArrayList<TaskInfo>();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo>  infos = am.getRunningAppProcesses();
		PackageManager pm = context.getPackageManager();
		for(RunningAppProcessInfo info : infos){
			TaskInfo taskInfo = new TaskInfo();
			String packname = info.processName;//������==��������
			
			long memsize = am.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty()*1024;
			taskInfo.setMemsize(memsize);
			taskInfo.setPackname(packname);
			try {
				ApplicationInfo  appinfo =	pm.getApplicationInfo(packname, 0);
				if( (appinfo.flags & ApplicationInfo.FLAG_SYSTEM)==1 ){//�ж� �Ƿ����û���ϵͳӦ�� 
					taskInfo.setUsertask(false);
				}else{
					taskInfo.setUsertask(true);
				}
				Drawable icon = appinfo.loadIcon(pm);//Ӧ�� ͼ�� 
				taskInfo.setIcon(icon);
				CharSequence name = appinfo.loadLabel(pm);//Ӧ��
				taskInfo.setName(name.toString());
			} catch (Exception e) {
				e.printStackTrace();
				//todo:�쳣���� �����ϵͳӦ��û�г�����.�ð������������Ĭ��ͼ��
				taskInfo.setName(packname);
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
			}
			taskinfos.add(taskInfo);
		}
		return taskinfos;
	}
}
