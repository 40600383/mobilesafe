package cn.itcast.mobilesafe;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
/**
 * ��װ �ֻ����������Ĳ���
 * @author superboy
 *
 */
public abstract class BaseSetupActivity extends Activity {
	protected static final String TAG = "BaseSetupActivity";
	protected SharedPreferences sp;
	// 1.����һ������ʶ����
	protected GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 2.��ʼ������ʶ����
		mGestureDetector = new GestureDetector(
				new GestureDetector.SimpleOnGestureListener() {
					// velocityx ˮƽ�����ƶ����ٶ�
					@Override//����true���ѵ�  �� ��ֹ��
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						//e1 e2  ��һ��ڶ�����   velocityX velocityY  ��ֱ�봹ֱ�������ƶ����ٶ�
						if (Math.abs(velocityX) < 100) {//ȡ����ֵ   ��λ ����
							Log.i(TAG, "�ƶ���̫��");
							return true;
						}

						if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
							Log.i(TAG, "�������Ϸ�");
							return true;
						}
						if (e2.getRawX() - e1.getRawX() > 200) {
							showPre();
							return true;
						}

						if (e1.getRawX() - e2.getRawX() > 200) {
							showNext();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});

		sp = getSharedPreferences("config", MODE_PRIVATE);
		findView();
		setupView();
	}
	/**
	 *���ҿؼ�
	 */
	public abstract void findView();
    /**
     * ���ݻ���
     */
	public abstract void setupView();
    /**
     * ��һҳ
     */
	public abstract void showNext();
    /**
     * ǰһҳ
     */
	public abstract void showPre();

	public void next(View view) {
		showNext();
	}

	public void pre(View view) {
		showPre();
	}

	public void openActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
		finish();
	}

	// 3.������ʶ��������
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
