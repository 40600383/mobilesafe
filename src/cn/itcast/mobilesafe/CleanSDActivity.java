package cn.itcast.mobilesafe;

import java.io.File;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

public class CleanSDActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				File sd = Environment.getExternalStorageDirectory();
				File[] files = sd.listFiles();
				for (File file : files) {
					if (file.isDirectory()) {
						String name = file.getName();
						// ��ѯ���ݿ� �Ƿ����������.
						// ��ӵ�������Ľ�����.
					}
				}

				return null;
			}

			@Override
			protected void onPreExecute() {
				Toast.makeText(getApplicationContext(), "��ʼɨ��sd��", 0).show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				Toast.makeText(getApplicationContext(), "ɨ��sd�����", 0).show();
				super.onPostExecute(result);
			}

		}.execute();

	}

	
	//����ļ���
	private void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for (File f : fs) {
				deleteFile(f);
			}
		} else {
			file.delete();
		}
		file.delete();
	}

}
