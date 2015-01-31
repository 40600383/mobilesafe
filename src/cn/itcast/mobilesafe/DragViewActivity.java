package cn.itcast.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
/**
 * ��������ʾ��λ��
 * @author superboy
 *
 */
public class DragViewActivity extends Activity {
	protected static final String TAG = "DragViewActivity";
	private ImageView iv_drag_view;
	private TextView tv_drag_info;
	private Display  display;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.activity_drag_view);
		sp =getSharedPreferences("config", MODE_PRIVATE);
		WindowManager wm = getWindowManager();
		display = wm.getDefaultDisplay();
		
		iv_drag_view = (ImageView) findViewById(R.id.iv_drag_view);
		tv_drag_info = (TextView) findViewById(R.id.tv_drag_info);
		
		
		//��ʼ�� λ��
		int lastx = sp.getInt("lastx", 0);
		int lasty = sp.getInt("lasty", 0);
		
		//ע��: �ڽ���û�б���Ⱦ����֮ǰ layout�ķ����ǲ�����Ч�� ,����Ҫ���ò��ֵķ�ʽ ָ��λ��
		RelativeLayout.LayoutParams params = (LayoutParams) iv_drag_view.getLayoutParams();
		params.leftMargin = lastx;
		params.topMargin = lasty;
		iv_drag_view.setLayoutParams(params);
		
		iv_drag_view.setOnTouchListener(new OnTouchListener() {
			//��¼��ʼ������
			int startX;
			int startY;
			
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// ��ָ��һ�δ�������Ļ
				case MotionEvent.ACTION_DOWN:
					Log.i(TAG,"����!");
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				// ��ָ����Ļ���ƶ�
				case MotionEvent.ACTION_MOVE:
					Log.i(TAG,"����!");
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					int l = iv_drag_view.getLeft();
					int t = iv_drag_view.getTop();
					int b = iv_drag_view.getBottom();
					int r = iv_drag_view.getRight();
					
					int newl = l+dx;
					int newr = r+dx;
					int newt = t+dy;
					int newb = b+dy;
					
					if(newl<0||newr>display.getWidth()||newt<0||newb>display.getHeight()){
						break;
					}
					
					int tv_height = tv_drag_info.getBottom() - tv_drag_info.getTop();
					
					
					if(newt>display.getHeight()/2){
						//tv ������ʾ���Ϸ�
						tv_drag_info.layout(tv_drag_info.getLeft(), 0, tv_drag_info.getRight(), tv_height);
					}else{
						//tv ��ʾ���·�.
						tv_drag_info.layout(tv_drag_info.getLeft(), display.getHeight()- tv_height-30, tv_drag_info.getRight(), display.getHeight()-30);
					}
					
					iv_drag_view.layout(newl, newt, newr, newb);
					startX = (int) event.getRawX();//���ĳ�ʼλ�� 
					startY = (int) event.getRawY();
					
					break;
				// ��ָ�뿪����Ļ
				case MotionEvent.ACTION_UP:
					Log.i(TAG,"����!");
					Editor editor = sp.edit();
					editor.putInt("lastx", iv_drag_view.getLeft());
					editor.putInt("lasty", iv_drag_view.getTop());
					editor.commit();
					break;

				}

				return true;
			}
		});
	}

}
