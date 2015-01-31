package cn.itcast.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import cn.itcast.mobilesafe.db.dao.BlackNumberDao;
import cn.itcast.mobilesafe.domain.BlackNumberBean;

public class CallSmsSafeActivityCopy1 extends Activity {
	private ImageView iv_add;
	private ListView lv_call_sms_safe;
	private BlackNumberDao dao;
	private LinearLayout ll_loading;
	private List<BlackNumberBean> blacknumberBeans;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		iv_add = (ImageView) findViewById(R.id.iv_call_sms_safe_icon);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
		dao = new BlackNumberDao(this);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				ll_loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				ll_loading.setVisibility(View.INVISIBLE);
				lv_call_sms_safe.setAdapter(new CallSmsAdapter());
			}

			@Override
			protected Void doInBackground(Void... params) {
				blacknumberBeans = dao.findAll();
				return null;
			}
		}.execute();

		
	}

	
	private class CallSmsAdapter extends BaseAdapter {

		protected static final String TAG = "CallSmsAdapter";

		public int getCount() {
			return blacknumberBeans.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			//Log.i(TAG,"getview:"+position);
			View view ;
			//����������Щview��������ؼ�������.
			ViewHolder holder;
			//1.���û����view���� �Ż�listview ���ٲ����ļ�->view�Ĳ�������.
			if(convertView!=null && convertView instanceof RelativeLayout){
				view = convertView;
				Log.i(TAG,"����  old view :"+position);
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_call_sms, null);
				Log.i(TAG,"�����µ� view :"+position);
				holder = new ViewHolder();
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_call_sms_item_mode);
				holder.tv_number = (TextView) view
						.findViewById(R.id.tv_call_sms_item_number);
				holder.iv = (ImageView) view
						.findViewById(R.id.iv_call_sms_item_delete);
			//2.��һ���Ż�listview ��������ؼ��Ĳ��Ҵ���
				view.setTag(holder);
			}
			
			BlackNumberBean bean = blacknumberBeans.get(position);

			String mode = bean.getMode();
			final String number = bean.getNumber();
			holder.iv.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Log.i(TAG, "ɾ��"+number);

				}
			});
			holder.tv_number.setText(number);
			if ("1".equals(mode)) {
				holder.tv_mode.setText("ֻ���ص绰");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("ֻ���ض���");
			} else {
				holder.tv_mode.setText("�绰����ȫ������");
			}

			return view;
		}

	}
	static class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
		ImageView iv;
	}
	
	
}
