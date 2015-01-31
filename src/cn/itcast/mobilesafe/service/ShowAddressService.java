package cn.itcast.mobilesafe.service;

import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.db.dao.AddressDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
/**
 * ��������ʾ���� 
 * @author superboy
 *
 */
public class ShowAddressService extends Service {

	public static final String TAG = "ShowAddressService";
	private TelephonyManager tm;
	private MyPhoneStatusListener listener;
	private OutCallReceiverInService receiver;
	private WindowManager wm;
	private View view;
	// "��͸��","������","��ʿ��","������","ƻ����"
	private static final int[] bgs = { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green };

	private SharedPreferences sp;
	/**
	 * �����д����㲥������(�������嵥�ļ��������� )
	 * @author superboy
	 *
	 */
	private class OutCallReceiverInService extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			Log.i(TAG, "�ڲ��Ĺ㲥������");
			if (number != null) {
				String address = AddressDao.getAddress(number);
				// Toast.makeText(context, address, 1).show();
				showAddress(address);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		// �ڷ��񴴽���ʱ�� ���ô���ķ�ʽ ע��һ���㲥������.
		// �㲥�����ߵ��������� �͸� ����һ�µ�
		receiver = new OutCallReceiverInService();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");//�����Ⲧ�绰�Ķ���
		this.registerReceiver(receiver, filter);//ע��㲥������

		// ע��һ���绰״̬�ļ�����.
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStatusListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//�����绰״̬ 
		
		//PhoneStateListener.LISTEN_DATA_ACTIVITY    ���� ���� 
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);//ȡ������ 
		listener = null;
		this.unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}

	private class MyPhoneStatusListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// �ֻ����ڿ���״̬.
				if (view != null) {
					wm.removeView(view);//�Ƴ�ģ����˾
					view = null;
				}
				break;

			case TelephonyManager.CALL_STATE_RINGING:// ����״̬
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(getApplicationContext(), address, 1).show();
				showAddress(address);

				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:// ��ͨ�绰��״̬

				break;
			}

		}
	}

	/**
	 * ģ����˾ ���ֻ��Ĵ����� ��ʾһ���Զ����view����.
	 * 
	 * @param address �ֻ�������
	 */
	public void showAddress(String address) {
		view = View.inflate(this, R.layout.toast_address, null);
		// �������� �޸�һ�±�����ɫ.
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(bgs[which]);
		TextView tv = (TextView) view.findViewById(R.id.tv_toast_address);
		tv.setText(address);
		
		//�ο�ϵͳ���� 
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.gravity = Gravity.LEFT | Gravity.TOP;// �����ϽǶ���
		
		//���ú�Ĳ���
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//��Ҫָ�� 

		//���ƿؼ��ƶ�
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:
					int x = (int) event.getRawX();
					int y = (int) event.getRawY();
					int dx = x - startX;
					int dy = y - startY;
					params.x += dx;
					params.y += dy;
					
					wm.updateViewLayout(view, params);//����view
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				}

				return true;
			}
		});

		wm.addView(view, params);

	}
}
