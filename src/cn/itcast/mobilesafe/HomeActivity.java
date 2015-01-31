package cn.itcast.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import cn.itcast.mobilesafe.adapter.HomeAdapter;
import cn.itcast.mobilesafe.utils.MD5Util;
/**
 * HomeActivity ��ҳ��  GridView ������ ����ӵ���¼�
 * @author superboy
 *
 */
public class HomeActivity extends Activity implements OnClickListener {
	private GridView gv_main;
	private SharedPreferences sp;
	
	//��һ�ν���Ի�������ĳ�ʼ��
	private EditText et_first_pwd;
	private EditText et_first_pwd_confirm;
	private Button bt_first_ok;
	private Button bt_first_cancle;
	
	
	//�ڶ��ν���Ի�������ĳ�ʼ��
	private EditText et_normal_pwd;
	private Button bt_normal_ok;
	private Button bt_normal_cancle;
	
	private AlertDialog dialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gv_main = (GridView) findViewById(R.id.gv_main);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_main.setAdapter(new HomeAdapter(this));
		
		
		gv_main.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				case 0:
					loadLostProtectedUI();//�ֻ�����
					
					break;
				case 1:
					intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
					startActivity(intent);
					
					break;
				case 2:
					intent = new Intent(HomeActivity.this,AppManagerActivity2.class);
					startActivity(intent);
					
					break;
				case 3:
					intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);
					
					break;
				case 4:
					intent = new Intent(HomeActivity.this,TrafficManagerActivity.class);
					startActivity(intent);
					
					break;
				case 5:
					intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
					startActivity(intent);
					
					break;
				case 6:
					intent = new Intent(HomeActivity.this,SystemOptActivity.class);
					startActivity(intent);
					
					break;
				case 7:
					intent = new Intent(HomeActivity.this,AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8://��������
					intent = new Intent(HomeActivity.this,SettingCenterActivity.class);
					startActivity(intent);
					break;

				
				}
				
				
			}
		});
	}
	/**
	 * �����ֻ�������UI
	 */
	protected void loadLostProtectedUI() {
     	//�ж��û��Ƿ����ù�����.
		if(isSetupPWD()){
			//��������ĶԻ���
			showNormalEntryDialog();
		}else{
			//��������Ի���
			showFirstEntryDialog();
		}
		
	}

	/**
	 * ����Ƿ����ù�����
	 * @return
	 */
	private boolean isSetupPWD(){
		String savedPwd = sp.getString("password", "");
		if(TextUtils.isEmpty(savedPwd)){
			return false;
		}else{
			return true;
		}
	}
	
	
	/**
	 * ��һ�ν���Ի���
	 */
	private void showFirstEntryDialog() {
		AlertDialog.Builder builder = new Builder(this);
		
		View view = View.inflate(this, R.layout.dialog_first_entry, null);
		et_first_pwd = (EditText) view.findViewById(R.id.et_first_pwd);
		et_first_pwd_confirm = (EditText) view.findViewById(R.id.et_first_pwd_confirm);
		bt_first_ok = (Button)view.findViewById(R.id.bt_first_ok);
		bt_first_cancle = (Button)view.findViewById(R.id.bt_first_cancle);
		
		bt_first_ok.setOnClickListener(this);
		bt_first_cancle.setOnClickListener(this);
		
		dialog = builder.create();//�����Ի���ĺڱ�
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
	/**
	 * ��������Ի���
	 */
	private void showNormalEntryDialog() {
		AlertDialog.Builder builder = new Builder(this);
		
		View view = View.inflate(this, R.layout.dialog_normal_entry, null);
		et_normal_pwd = (EditText) view.findViewById(R.id.et_normal_pwd);
		bt_normal_ok = (Button)view.findViewById(R.id.bt_normal_ok);
		bt_normal_cancle = (Button)view.findViewById(R.id.bt_normal_cancle);
		
		bt_normal_ok.setOnClickListener(this);
		bt_normal_cancle.setOnClickListener(this);
		
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);//ȥ����ɫ�߿�
		dialog.show();
		
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_first_cancle:
			dialog.dismiss();
			break;

		case R.id.bt_first_ok:
			String pwd = et_first_pwd.getText().toString().trim();
			String pwd_confirm = et_first_pwd_confirm.getText().toString().trim();
			
			if(TextUtils.isEmpty(pwd)||TextUtils.isEmpty(pwd_confirm)){
				Toast.makeText(this, "���벻��Ϊ��", 0).show();//LENGTH_SHORT = 0; LENGTH_LONG = 1;
				return ;
			}
			if(pwd.equals(pwd_confirm)){
				Editor editor = sp.edit();
				editor.putString("password", MD5Util.encode(pwd));
				editor.commit();
				dialog.dismiss();//�رնԻ���
			}else {
				Toast.makeText(this, "�������벻һ��", 0).show();
				return;
			}
			break;
		case R.id.bt_normal_cancle:
			dialog.dismiss();
			break;
		case R.id.bt_normal_ok:
			String enterdpwd = et_normal_pwd.getText().toString().trim();
			
			if(TextUtils.isEmpty(enterdpwd)){
				Toast.makeText(this, "���벻��Ϊ��", 0).show();
				return;
			}
			String savedpwd = sp.getString("password", "");
			if((MD5Util.encode(enterdpwd)).equals(savedpwd)){
				dialog.dismiss();
				//�������
				Intent intent = new Intent(this,LostFindActivity.class);
				startActivity(intent);
			}else{
				Toast.makeText(this, "���벻��ȷ", 0).show();
				return;
			}
			break;
		}
		
	}
}
