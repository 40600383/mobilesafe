package cn.itcast.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * �ֻ���������
 * @author superboy
 *
 */
public class LostFindActivity extends Activity {

	private SharedPreferences sp;
	private TextView tv_lostfind_number;
	private ImageView iv_lostfind_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_lost_find);
		//��ȫ��������
		tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
		//������������״̬
		iv_lostfind_status = (ImageView) findViewById(R.id.iv_lostfind_status);
	
		if(isSetup()){
			//��ʾ��ϸ���ý���
			tv_lostfind_number.setText(sp.getString("safenumber", ""));
			boolean isprotecting = sp.getBoolean("isprotecting", false);
			if(isprotecting){
				iv_lostfind_status.setImageResource(R.drawable.lock);
			}else{
				iv_lostfind_status.setImageResource(R.drawable.unlock);
			}
		}else{
			//����ҳ�浽�����򵼽���.
			loadSetupUI();
		}
	}



	private void loadSetupUI() {
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
	}
 
	
	
	/**
	 * �ж��û��Ƿ���й�������
	 * @return
	 */
	private boolean isSetup(){
		return sp.getBoolean("setup", false);
	}
	/**
	 * ��������
	 * @param view
	 */
	public void reEntrySetup(View view){
		loadSetupUI();
	}
}
