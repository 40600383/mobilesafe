package cn.itcast.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.itcast.mobilesafe.domain.ContactInfo;
/**
 * * ��ȡϵͳ�������ϵ��.
 * @author superboy
 *
 */
public class ContactInfoProvider {
	/**
	 * ��ȡϵͳ�������ϵ��.
	 * 
	 * @param context
	 * @return
	 */
	public static List<ContactInfo> getContactInfos(Context context) {
		// 1.��raw_contact ����ϵ�˵�idȡ��ȥ .
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		while (cursor.moveToNext()) {
			// 2.�������id ��data�����������ȡ����.
			String id = cursor.getString(0);
			if (id != null) {//��ϵ��ɾ��  �� �޸�data����contact_idΪnullͬ�����䡣����
				ContactInfo info = new ContactInfo();
				Cursor dataCursor = resolver.query(datauri, new String[] {
						"mimetype", "data1" }, "raw_contact_id=?",
						new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String mime = dataCursor.getString(0);
					String data1 = dataCursor.getString(1);
					if ("vnd.android.cursor.item/phone_v2".equals(mime)) {
						info.setNumber(data1);
					} else if ("vnd.android.cursor.item/name".equals(mime)) {
						info.setName(data1);
					}
				}
				dataCursor.close();//�ر�cursor
				infos.add(info);
			}
		}
		cursor.close();//�ر�cursor
		return infos;
	}
}
