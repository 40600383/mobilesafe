package cn.itcast.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.itcast.mobilesafe.db.BlackNumberDBOpenHelper;
import cn.itcast.mobilesafe.domain.BlackNumberBean;
/**
 * ������ ����dao,blacknumber.db�������ɾ�Ĳ�
 * @author superboy
 *
 */
public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * ��Ӻ���������
	 * 
	 * @param number
	 *            ����������
	 * @param mode
	 *            ����ģʽ
	 */
	public void add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into blacknumber (number,mode) values (?,?)",
				new Object[] { number, mode });
		db.close();
	}

	/**
	 * ��ѯ����������
	 * 
	 * @param number
	 *            ����������
	 * @return
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select * from blacknumber where number=?",
				new String[] { number });
		if (curosr.moveToNext()) {
			result = true;
		}
		curosr.close();
		db.close();
		return result;
	}
	/**
	 * ��ѯ���������������ģʽ
	 * 
	 * @param number
	 *            ����������
	 * @return 0 ȫ������
	 *         1 �绰����
	 *         2 ��������
	 *         -1 ������ ���ݿ�û�м�¼
	 */
	public String findMode(String number) {
		String mode  = "-1";
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select mode from blacknumber where number=?",
				new String[] { number });
		if (curosr.moveToNext()) {
			mode = curosr.getString(0);
		}
		curosr.close();
		db.close();
		return mode;
	}
	/**
	 * ���ݿ���޸� �޸�����ģʽ.
	 * 
	 * @param newmode
	 *            �µ�����ģʽ
	 * @param number
	 *            Ҫ�޸ĵĺ���������
	 */
	public void update(String newmode, String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update blacknumber set mode=? where number=?",
				new Object[] { newmode, number });
		db.close();
	}

	/**
	 * ɾ��һ����������¼
	 * 
	 * @param number
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from blacknumber where number=?",
				new Object[] { number });
		db.close();
	}

	/**
	 * ��ѯȫ������������.
	 */
	public List<BlackNumberBean> findAll() {
		ArrayList<BlackNumberBean> blacknumbers = new ArrayList<BlackNumberBean>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select number,mode from blacknumber ",
				new String[] {});
		while (curosr.moveToNext()) {
			String number = curosr.getString(0);
			String mode = curosr.getString(1);
			BlackNumberBean bean = new BlackNumberBean();
			bean.setMode(mode);
			bean.setNumber(number);
			blacknumbers.add(bean);
			bean = null;
		}
		curosr.close();
		db.close();
		return blacknumbers;
	}

	/**
	 * ��ѯ���ֺ���������.
	 * 
	 * @param startindex
	 *            �ӵڼ������ݿ�ʼ��ѯ
	 * @param maxnum
	 *            ��෵�ض��ٸ���Ϣ
	 * @return
	 */
	public List<BlackNumberBean> findPart(int startindex, int maxnum) {
		ArrayList<BlackNumberBean> blacknumbers = new ArrayList<BlackNumberBean>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery(
				"select number,mode from blacknumber order by id desc limit ? offset ? ",
				new String[] { String.valueOf(maxnum),
						String.valueOf(startindex) });
		while (curosr.moveToNext()) {
			String number = curosr.getString(0);
			String mode = curosr.getString(1);
			BlackNumberBean bean = new BlackNumberBean();
			bean.setMode(mode);
			bean.setNumber(number);
			blacknumbers.add(bean);
			bean = null;
		}
		curosr.close();
		db.close();
		return blacknumbers;
	}
	/**
	 * ����һ���ж���ҳ������
	 * @param maxnumber
	 * @return
	 */
	public int getTotalPage(int maxnumber){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber", null);
		int totalnumber = cursor.getCount();
		cursor.close();
		db.close();
		if(totalnumber%maxnumber==0){
			return totalnumber/maxnumber;
		}else{
			return totalnumber/maxnumber + 1;
		}
		
	}
}
