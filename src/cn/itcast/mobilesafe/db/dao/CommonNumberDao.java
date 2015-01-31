package cn.itcast.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {

	/**
	 * ��ȡ���ݿ��ж��ٸ�������Ϣ
	 * 
	 * @return
	 */
	public static int getGroupCount() {
		int count = 0;
		// 1.�����ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from classlist", null);
		count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	/**
	 * ��ȡ���ݿ����еķ�����Ϣ
	 * 
	 * @return
	 */
	public static List<String> getGroupItems() {
		List<String>  groupItems = new ArrayList<String>();
		// 1.�����ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select name from classlist", null);
		while(cursor.moveToNext()){
			groupItems.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return groupItems;
	}
	/**
	 * ��ȡ���ݿ�ĳ�������ж��ٸ�������Ϣ
	 * groupPosition��0��ʼ
	 * @return
	 */
	public  static int getChildCount(int groupPosition) {
		int count = 0;
		// 1.�����ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		int newid = groupPosition + 1;
		Cursor cursor = db.rawQuery("select * from table" + newid, null);
		count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
		/**
	 * ��ȡ���ݿ�ĳ���������еĺ�����Ϣ
	 * 
	 * @return
	 */
	public  static List<String> getChildItems(int groupPosition) {
		List<String> childItems =new ArrayList<String>();
		// 1.�����ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		int newid = groupPosition + 1;
		Cursor cursor = db.rawQuery("select name,number from table" + newid, null);
		while(cursor.moveToNext()){
			childItems.add(cursor.getString(0)+"\n"+cursor.getString(1));
		}
		cursor.close();
		db.close();
		return childItems;
	}

	/**
	 * ����ĳ�����������
	 * @param groupPosition
	 * @return
	 */
	public static  String getGroupName(int groupPosition){
		String groupname="";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		int newid = groupPosition + 1;
		Cursor cursor = db.rawQuery("select name from classlist where idx=?", new String[]{newid+""});
		if(cursor.moveToFirst()){
			groupname = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return groupname;
	}
	/**
	 * ����ĳ�������ĳ��������Ϣ
	 * @param groupPosition
	 * @return
	 */
	public static  String getChildInfoByPosition(int groupPosition,int childPosition){
		String result="";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		int newgroupid = groupPosition + 1;
		int newchildid = childPosition + 1;
		
		Cursor cursor = db.rawQuery("select name,number from table"+newgroupid+" where _id=?", new String[]{newchildid+""});
		if(cursor.moveToFirst()){
			String name  = cursor.getString(0);
			String number  = cursor.getString(1);
			result = name+"\n"+number;
		}
		cursor.close();
		db.close();
		return result;
	}
}










