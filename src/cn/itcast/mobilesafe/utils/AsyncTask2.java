package cn.itcast.mobilesafe.utils;

import android.os.Handler;

public abstract class AsyncTask2 {
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//��̨����ִ�к󱻵���.
			onPostExecute();
		};
	};
	
	/**
	 * ��ʱ����ִ��֮ǰ���õķ���.
	 */
	public abstract void onPreExecute();
	
	/**
	 * ��ʱ����ִ����Ϻ���õķ���.
	 */
	public abstract void onPostExecute();
	/**
	 * ִ��һ����̨�ĺ�ʱ������
	 */
	public abstract void doInBackground();
	

	public void execute(){
		onPreExecute();
		new Thread(){
			public void run() {
				doInBackground();
				//֪ͨ��ʱ����ִ�������.
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
}
