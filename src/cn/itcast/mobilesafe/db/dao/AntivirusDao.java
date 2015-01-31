package cn.itcast.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntivirusDao {

	/**
	 * ��ѯ��ǰmd5�Ƿ��ڲ������ݿ�����.
	 * 
	 * @param md5
	 * @return ������������Ϣ. ��������� ����null
	 */
	public static String findVirus(String md5) {
		String result = null;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/antivirus.db", null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select desc from datable where md5=?",
				new String[] { md5 });
		if (cursor.moveToNext()) {
			result = cursor.getString(0);

		}
		cursor.close();
		db.close();
		return result;
	}
}
