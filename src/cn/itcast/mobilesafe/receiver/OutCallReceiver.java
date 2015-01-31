package cn.itcast.mobilesafe.receiver;

import cn.itcast.mobilesafe.LostFindActivity;
import cn.itcast.mobilesafe.db.dao.AddressDao;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();
		if("20182018".equals(number)){
			Intent lostfindIntent = new Intent(context,LostFindActivity.class);
			lostfindIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//�������������ʶ
			context.startActivity(lostfindIntent);
			//abortBroadcast();
			setResultData(null);//����绰 Ϊ����㲥
			return;
		}
		//���� ���ù㲥������ �� �����������,��������ط�����,ִ�й㲥�����ߵĴ���
		//Toast.makeText(context, AddressDao.getAddress(number), 1).show();
	}
}
