package cn.itcast.mobilesafe.ui;

import cn.itcast.mobilesafe.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * �㶨��cn.itcast.mobilesafe.ui.SettingView
 * @author superboy
 *
 */
public class SettingView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_content;
	private CheckBox cb_setting_view_status;

	public SettingView(Context context) {
		super(context);
		inflateView(context);
	}


//�޹��캯��  xml���ûᱨ�� 
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflateView(context);
		TypedArray  a = context.obtainStyledAttributes(attrs, R.styleable.setting_view);//obtainStyledAttributes֮��һ��Ҫ���� recycle
		String title = a.getString(R.styleable.setting_view_title);
		String content = a.getString(R.styleable.setting_view_content);
		tv_title.setText(title);
		tv_content.setText(content);
		//�ǵ���������
		a.recycle();
	}
	
	private void inflateView(Context context){
		View view = View.inflate(context, R.layout.ui_setting_view, this);
		//�ڴ��������ҵ������ļ���������Ϣ. �޸�view�������������
		tv_title = (TextView) view.findViewById(R.id.tv_setting_view_title);
		tv_content = (TextView) view.findViewById(R.id.tv_setting_view_content);
		cb_setting_view_status = (CheckBox)view.findViewById(R.id.cb_setting_view_status);
	}
	
	/**
	 * �жϵ�ǰsettingview�Ƿ�ѡ��
	 * @return
	 */
	public boolean isChecked(){
		return cb_setting_view_status.isChecked();
	}
	/**
	 * ����settingview�Ĺ�ѡ״̬.
	 * @param checked
	 */
	public void setChecked(boolean checked){
		cb_setting_view_status.setChecked(checked);
	}
	public void setContent(String text){
		tv_content.setText(text);
	}
}
