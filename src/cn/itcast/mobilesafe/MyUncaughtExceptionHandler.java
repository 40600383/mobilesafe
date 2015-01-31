package cn.itcast.mobilesafe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.os.Build;
import android.os.Environment;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private MyUncaughtExceptionHandler() {
	};

	private static MyUncaughtExceptionHandler mExceptionHandler;

	/**
	 * δ������쳣������ ��һ������ �����쳣���߳� �ڶ������� ��ǰ���쳣����.
	 */
	public void uncaughtException(Thread thread, Throwable err) {

		// throwable.printStackTrace(err);
		System.out.println("�������쳣,���Ǳ��Ҳ�����.!!!");
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					"error.log");
			FileOutputStream fos;
			fos = new FileOutputStream(file);
			fos.write(("time:"+System.currentTimeMillis()+"\n").getBytes());
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// ��������˽���ֶ�.
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				fos.write((name+":"+value+"\n").getBytes());
			}
			
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			err.printStackTrace(writer);
			err.printStackTrace();
			
			String errorlog = sw.toString();
			System.out.println("---"+errorlog);
			fos.write(errorlog.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public synchronized static MyUncaughtExceptionHandler getInstance() {
		if (mExceptionHandler == null) {
			mExceptionHandler = new MyUncaughtExceptionHandler();
		}
		return mExceptionHandler;
	}
}
