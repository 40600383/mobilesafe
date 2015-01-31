package cn.itcast.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * ��ȡ�ֻ���������ص�Dao
 * 
 * @author Administrator
 * 
 */
public class AddressDao {
	public static String getAddress(String number) {
		// �������û�в�ѯ�� �ͷ��ص�ǰ����
		String address = number;
		// 1.�����ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/address.db", null,
				SQLiteDatabase.OPEN_READONLY);
		// �����ж� number�Ƿ���һ���ֻ�����.
		// ����11λ 1��ͷ 3458 9λ������.
		if (number.matches("^1[3458]\\d{9}$")) {
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id = ?)",
							new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();

		} else {// ���ֻ�����
			switch (number.length()) {
			case 3:
				address = "�����绰";
				break;
			case 4:
				address = "ģ����";
				break;
			case 5:
				address = "����绰";
				break;

			case 7:
				address = "���ص绰";
				break;
			case 8:
				address = "���ص绰";
				break;
			default://ǰ3λ ��4λ �����ظ�
				if (number.length() >= 10 && number.startsWith("0")) {
					Cursor cursor = db.rawQuery(
							"select location from data2 where area = ?",
							new String[] { number.substring(1, 3) });
					if (cursor.moveToFirst()) {
						String location = cursor.getString(0);
						location = location.replace("����", "");
						location = location.replace("�ƶ�", "");
						location = location.replace("��ͨ", "");
						address = location;
					}
					cursor.close();
					cursor = db.rawQuery(
							"select location from data2 where area = ?",
							new String[] { number.substring(1, 4) });
					if (cursor.moveToFirst()) {
						String location = cursor.getString(0);
						location = location.replace("����", "");
						location = location.replace("�ƶ�", "");
						location = location.replace("��ͨ", "");
						address = location;
					}
					cursor.close();
				}

				break;
			}

		}
		db.close();
		return address;
	}
}
