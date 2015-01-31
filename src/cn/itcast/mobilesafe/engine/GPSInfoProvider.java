package cn.itcast.mobilesafe.engine;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
/**
 * ��ȡ�ֻ�����λ�� 
 * @author superboy
 *
 */
public class GPSInfoProvider {
	private GPSInfoProvider() {
	};

	private static GPSInfoProvider mGpsInfoProvider;
	private static LocationManager lm;
	private static MyLocationLinstener listener;
	private static SharedPreferences sp;
/**
 * ��ȡGPSInfoProvider����
 * @param context
 * @return
 */
	public static synchronized GPSInfoProvider getInstance(Context context) {
		if (mGpsInfoProvider == null) {
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			// ��ȡλ�þ�ȷ�ȵ�Ҫ��
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			// ���ĺ���
			criteria.setAltitudeRequired(true);
			// �Ƿ���ܲ�������  ���� ����
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_HIGH);//�ѵ��
			criteria.setSpeedRequired(true);//�ƶ��ٶ�
			String provider = lm.getBestProvider(criteria, true);//���� ��վ gps��λ
			System.out.println(provider);//��λ�������� ��ʽ���
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			mGpsInfoProvider = new GPSInfoProvider();
			
			listener = mGpsInfoProvider.new MyLocationLinstener();
			lm.requestLocationUpdates(provider, 0, 0, listener);
		}
		return mGpsInfoProvider;

	}

	public String getAddress(){
		return sp.getString("lastaddress", "");
	}
	
	private class MyLocationLinstener implements LocationListener {

		/**
		 * ���ֻ���λ�÷����ı��ʱ�� ���õķ���.
		 */
		public void onLocationChanged(Location location) {//location��װ�˵�ǰ�û���λ�� 
			// ��������. ��׼�ľ��Ⱥ�γ��. -> ��������
			double longtitude = location.getLongitude();
			double latitude = location.getLatitude();
			double accuracy = location.getAccuracy();
			PointDouble s2c=null;
			//����׼  ��γ  ת��
//			try {
//				ModifyOffset instance = ModifyOffset.getInstance(this.getClass().getClassLoader().getResourceAsStream("axisoffset.dat"));
//				PointDouble pt = new PointDouble(longtitude, latitude);
//				 s2c = instance.s2c(pt);
//			} catch (Exception e) {
//			}
//			
//			String lastaddress = (s2c+ "\na:" + accuracy);
			String lastaddress = ("j:" + longtitude + "\nw:" + latitude//����  ��γ��
					+ "\na:" + accuracy);
			Editor editor = sp.edit();
			editor.putString("lastaddress", lastaddress);
			editor.commit();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		public void onProviderEnabled(String provider) {

		}

		public void onProviderDisabled(String provider) {

		}
	}
}
