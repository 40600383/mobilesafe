package cn.itcast.mobilesafe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.itcast.mobilesafe.domain.UpdateInfo;
import cn.itcast.mobilesafe.engine.UpdateInfoParser;
import cn.itcast.mobilesafe.utils.AssetCopyUtil;
import cn.itcast.mobilesafe.utils.DownLoadUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * SplashActivity���� 1���������ӷ��������ϵͳ�汾2���������ݿ��ļ�assets-->data/data/cn.itcast.mobliesafe/databases/��
 * 
 * @author superboy
 *
 */
public class SplashActivity extends Activity {
	public static final int PARSE_SUCCESS = 10;
	public static final int PARSE_ERROR = 11;
	public static final int SERVER_ERROR = 12;
	public static final int URL_ERROR = 13;
	public static final int NETWORK_ERROR = 14;
	public static final int DOWNLOAD_SUCCESS=15;
	public static final int DOWNLOAD_ERROR=16;
	protected static final String TAG = "SplashActivity";
	public static final int COPY_ADDRESSDB_ERROR = 17;
	public static final int COPY_ADDRESSDB_SUCCESS = 18;
	public static final int COPY_NUMBERDB_ERROR = 19;
	public static final int COPY_NUMBERDB_SUCCESS = 20;
	public static final int COPY_VIRUS_SUCCESS = 21;
	private TextView tv_splash_version;
	private RelativeLayout rl_splash_main;
	private UpdateInfo updateInfo;
	
	private ProgressDialog pd;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PARSE_SUCCESS:
				// �жϰ汾��
				if (getVersion().equals(updateInfo.getVersion())) {
					loadMainUI();
				} else {
					// ��ʾ������ʾ�Ի���
					showUpdateDialog();
				}

				break;

			case PARSE_ERROR:
				showMsg("����XMLʧ��,....");
				loadMainUI();
				break;
			case SERVER_ERROR:
				showMsg("����������");
				loadMainUI();
				break;
			case URL_ERROR:
				showMsg("url·������ȷ");
				loadMainUI();
				break;
			case NETWORK_ERROR:
				showMsg("�������Ӵ���");
				loadMainUI();
				break;
			case DOWNLOAD_ERROR:
				showMsg("�����ļ�����");
				loadMainUI();
				break;
			case DOWNLOAD_SUCCESS:
				//�ļ���װ  ϵͳӦ�� PackageInstaller�嵥�ļ�
				File file = (File) msg.obj;
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				startActivity(intent);
				finish();
				break;
			case COPY_ADDRESSDB_ERROR:
				showMsg("��ʼ���绰���ݿ����");
				break;
			case COPY_ADDRESSDB_SUCCESS:
				showMsg("��ʼ���绰���������ݳɹ�");
				break;
			case COPY_NUMBERDB_ERROR:
				showMsg("��ʼ�����ú������ݿ����");
				break;
			case COPY_NUMBERDB_SUCCESS:
				showMsg("��ʼ�����ú������ݳɹ�");
				break;
			case COPY_VIRUS_SUCCESS:
				showMsg("��ʼ���������ݿ����ݳɹ�");
				break;
			}

		}

	};
	private void showMsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, 0)
		.show();
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("�汾:" + getVersion());
		rl_splash_main = (RelativeLayout) findViewById(R.id.rl_splash_main);
		new Thread(new CheckVersionTask()).start();//���߳������ӷ��������Ѳ���

		//rootȨ��
		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);//͸���ȶ���
		aa.setDuration(2000);//����ʱ�� 
		rl_splash_main.startAnimation(aa);
		
		new Thread(new CopyFileTask()).start();
/*		�������ͼ��
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ���ʿ���ͼ��");
		Intent homeIntent = new Intent();
		homeIntent.setAction("cn.itcast.home");
		homeIntent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, homeIntent);
		
		sendBroadcast(intent);*/
	}
	/**
	 * �����ļ� assets-->data/data/cn.itcast.mobliesafe/databases/
	 * @param dbname  Ҫ�������ļ�
	 * @param success  �ɹ���ʾ
	 * @param fail  ʧ����ʾ
	 */
	private void copyFile(String dbname, int success,int fail){
		//�ж�һ���ļ��Ƿ��Ѿ�������ϵͳ��Ŀ¼��.
		File file = new File(getFilesDir(),dbname);
		if(file.exists()&&file.length()>0){
			//ʲô���鶼����
		}else{
			//�����ļ���ϵͳĿ¼.
			File copyedfile = AssetCopyUtil.copy(getApplicationContext(), dbname, file.getAbsolutePath());
			Message msg = Message.obtain();
			if(copyedfile==null){
				msg.what=success;
			}else{
				msg.what=fail;
			}
			handler.sendMessage(msg);
		}
	}
	
	/**
	 * �����ļ���ϵͳĿ¼
	 * @author Administrator
	 *
	 */
	private class CopyFileTask implements Runnable{

		public void run() {
			//�ж�һ���ļ��Ƿ��Ѿ�������ϵͳ��Ŀ¼��.
			copyFile("address.db",COPY_ADDRESSDB_ERROR,COPY_ADDRESSDB_SUCCESS);
			copyFile("commonnum.db",COPY_ADDRESSDB_ERROR,COPY_ADDRESSDB_SUCCESS);
			copyFile("antivirus.db",COPY_ADDRESSDB_ERROR,COPY_VIRUS_SUCCESS);
		}
	}
	
	/**
	 * ��AndroidManifest.xml�ļ��л�ȡ������Ϣ
	    android:versionName="1.0"
	 * @return
	 */
	private String getVersion() {

		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);//����2�ò��� ��0
			return packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// todo: can't reach
			return "";
		}

	}

	private class CheckVersionTask implements Runnable {
		public void run() {
			//����Ƿ����Զ�����
			SharedPreferences sp  = getSharedPreferences("config", MODE_PRIVATE);
			boolean update = sp.getBoolean("update", true);
			if(!update){//������
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				loadMainUI();
				return ;
			}
			
			long startTime = System.currentTimeMillis();
			Message msg = Message.obtain(); //���þɵ� msg����Ч��
			// ���ӷ����� ��ȡ������Ϣ.
			try {
				URL url = new URL(getResources().getString(R.string.serverurl));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(2000);

				int code = conn.getResponseCode();
				if (code == 200) {
					InputStream is = conn.getInputStream();
					// ����xml
					updateInfo = UpdateInfoParser.getUpdateInfo(is);
					if (updateInfo != null) {
						// �����ɹ�.
						msg.what = PARSE_SUCCESS;
					} else {
						// ����xmlʧ��.
						msg.what = PARSE_ERROR;
					}
				} else {
					msg.what = SERVER_ERROR;
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (NotFoundException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (IOException e) {
				e.printStackTrace();
				msg.what = NETWORK_ERROR;
			} finally {
				long endtime = System.currentTimeMillis();
				long dtime = endtime - startTime;
				if (dtime < 2000) {//����ʱ����2����
					try {
						Thread.sleep(2000 - dtime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				handler.sendMessage(msg);
			}

		}

	}

	/**
	 * ����Ӧ�ó���������
	 */
	private void loadMainUI() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();//�رյ�ǰactivity
	}

	/**
	 * ��ʾ������ʾ�ĶԻ���
	 */
	protected void showUpdateDialog() {

		Log.i(TAG, "��ʾ������ʾ�Ի���");
		AlertDialog.Builder buidler = new Builder(this);
		buidler.setIcon(R.drawable.notification);
		buidler.setTitle("������ʾ");
		buidler.setMessage(updateInfo.getDescription());
		buidler.setOnCancelListener(new OnCancelListener() {//���˼�    �¼� 

			public void onCancel(DialogInterface dialog) {
				loadMainUI();//���뵽������ 
			}
		});
		buidler.setPositiveButton("����", new OnClickListener() {//���ȷ���¼� 

			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "����:" + updateInfo.getPath());
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {//�ж� �����ڴ濨״̬ 
					
					pd = new ProgressDialog(SplashActivity.this);//�ڲ�����˺��޴�����ʾ
					pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//���÷���
					pd.show();//��ʾ ����
					
					//������ �ļ�������sd����Ŀ¼
					final File file = new File(Environment.getExternalStorageDirectory(),DownLoadUtil.getFileName(updateInfo.getPath()));
					new Thread() {
						public void run() {
							File downloadfile = DownLoadUtil.downLoad(updateInfo.getPath(),
									file.getAbsolutePath(), pd);
							Message msg = Message.obtain();//���߳��д��� msg
							if(downloadfile!=null){
								//���سɹ�,��װ....
								msg.what= DOWNLOAD_SUCCESS;//what��� ���� obj��Ŷ���
								msg.obj = downloadfile;
							}else{
								//��ʾ�û�����ʧ��.
								msg.what= DOWNLOAD_ERROR;
							}
							handler.sendMessage(msg);//����msg
							pd.dismiss();//�رո�����
						};
					}.start();
				}else{
					showMsg("sd��������");
					loadMainUI();
				}

			}
		});
		buidler.setNegativeButton("ȡ��", new OnClickListener() {//���ȡ���¼�

			public void onClick(DialogInterface dialog, int which) {
				loadMainUI();
			}
		});
		// buidler.create().show();
		buidler.show();//������� ����show����
	}
}