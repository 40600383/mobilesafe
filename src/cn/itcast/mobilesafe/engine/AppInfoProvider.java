package cn.itcast.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import cn.itcast.mobilesafe.domain.AppInfo;

/**
 * ��ȡ���еİ�װ���ֻ��ϵĳ�����Ϣ
 * @author Administrator
 *
 */
public class AppInfoProvider {

	public static List<AppInfo> getAppInfos(Context context){
		//1.��ȡ���еİ�װ���ֻ��ϵĳ���.
		PackageManager pm = context.getPackageManager();
		List<PackageInfo>  packinfos =	pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for(PackageInfo packinfo: packinfos){
			AppInfo appinfo = new AppInfo();
			String packname = packinfo.packageName;
			appinfo.setPackname(packname);
			String version = packinfo.versionName;
			appinfo.setVersion(version);
			
			Drawable icon = packinfo.applicationInfo.loadIcon(pm);
			appinfo.setIcon(icon);
			String name = packinfo.applicationInfo.loadLabel(pm).toString();
			appinfo.setName(name);
			if((packinfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) !=0){
				//��װ��sd��
				appinfo.setInrom(false);
			}else{
				appinfo.setInrom(true);
			}
			if( (packinfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1 ){
				appinfo.setUserapp(false);
			}else{
				appinfo.setUserapp(true);
			}
			appInfos.add(appinfo);
		}
		return appInfos;
	}
}
