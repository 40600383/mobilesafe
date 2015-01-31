package cn.itcast.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * ������ �������ݿ�blacknumber.db�Ĵ���
 * @author superboy
 *
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	public BlackNumberDBOpenHelper(Context context) {
		
		super(context, "blacknumber.db", null, 1);
	}

	/**
	 * ���ݿ��һ�α�������ʱ�� ���õķ���.
	 * ����ģʽ 0 ȫ������ 1�绰���� 2��������
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//���������� , ����ģʽ
		db.execSQL("create table blacknumber (id integer primary key autoincrement,number varchar(20),mode varchar(2) )", new Object[]{});
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
