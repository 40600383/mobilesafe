package cn.itcast.mobilesafe;

import cn.itcast.mobilesafe.service.WatchDogService2;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * ������������ ������� 
 * @author superboy
 *
 */
public class EnterPasswordActivity extends Activity {
	private EditText et_enterpwd;
	private ImageView iv_enterpwd_icon;
	private TextView tv_enterpwd_name;
	private String packname;
	
	private Intent watchDogIntent;
	private IService iService;
	private MyConn conn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_password);
		et_enterpwd = (EditText) findViewById(R.id.et_enterpwd);
		iv_enterpwd_icon = (ImageView) findViewById(R.id.iv_enterpwd_icon);
		tv_enterpwd_name = (TextView) findViewById(R.id.tv_enterpwd_name);
		
		watchDogIntent = new Intent(this,WatchDogService2.class);
		conn = new MyConn();
		bindService(watchDogIntent, conn, BIND_AUTO_CREATE);//activity�а� service,BIND_AUTO_CREATE���񲻴������Զ� ����
		
		//��ȡ���ݹ����Ĳ���
		Intent intent = getIntent();//��ȡ��ת����bean��װ������
		packname = intent.getStringExtra("packname");
		
		try {
			PackageManager pm = getPackageManager(); 
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);//��ʱ���ã�����0
			Drawable icon = info.loadIcon(pm);
			CharSequence name = info.loadLabel(pm);
			iv_enterpwd_icon.setImageDrawable(icon);
			tv_enterpwd_name.setText(name);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void enter (View view){
		String password = et_enterpwd.getText().toString().trim();
		if("123".equals(password)){//��Ӧ����Ӷ�Ӧ ���룬���� ͳһ����
			//����������ȷ.
		/*	Intent intent = new Intent();
			intent.setAction("cn.itcast.stop");//����һ���Զ���Ĺ㲥�¼�.
			intent.putExtra("stopedname", packname);
			sendBroadcast(intent);*/
			//���õ��÷�������ķ���. ֪ͨ������ʱ��ֹͣ����.
			iService.callTempStopProtect(packname);
			finish();
		}else{
			Toast.makeText(this, "���벻��ȷ", 0).show();
		}
		
	}
	
	
	
	private class MyConn implements ServiceConnection{

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("Binder","���񱻳ɹ�����.");
			iService = (IService) service;//Զ�̴������ĳ�ʼ��
		}

		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//�жϵ�ǰ�����Ƿ��Ǻ��˼�
		if(keyCode==KeyEvent.KEYCODE_BACK){
			//������Ĳ�������������Ĳ���
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			startActivity(intent);
			finish();//�ر���������Ľ���.
			return true;//����true����ֹ��һ�¼��Ľ�һ�������������������û�д�������¼�����Ӧ�ü���������
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		unbindService(conn);//�������ٵ�ʱ�� ������
		super.onDestroy();
	}
}
