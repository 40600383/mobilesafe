package cn.itcast.mobilesafe;

import android.app.Application;

//�ڵ�ǰӦ�ö�Ӧ�Ľ��� ������ʱ��  ʵ����. ʵ�������е��Ĵ����֮ǰ��.
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		// ȫ�ֳ�ʼ���Ĳ���.
		super.onCreate();
		// ��ϵͳ�������е�δ�ܲ�����쳣������������һ��Ĭ�ϵĴ�����.
		
		//����ǰ�����߳� ����һ��Ĭ�ϵ� δ�������Ϣ������
		Thread.currentThread().setUncaughtExceptionHandler(MyUncaughtExceptionHandler.getInstance());
	
	}
}
