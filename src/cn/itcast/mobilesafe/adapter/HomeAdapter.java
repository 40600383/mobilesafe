package cn.itcast.mobilesafe.adapter;

import cn.itcast.mobilesafe.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * HomeActivity GridView ����������(���Ż��ο�listview�Ż�)
 * @author superboy
 *
 */
public class HomeAdapter extends BaseAdapter {
	
	private Context context;
	

	public HomeAdapter(Context context) {
		this.context = context;
	}

	private static final String[] names={"�ֻ�����","ͨѶ��ʿ","�������","���̹���","����ͳ��",
		"�ֻ�ɱ��","ϵͳ����","�߼�����","��������"};
	
	private static final int[] icons={R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,R.drawable.taskmanager,R.drawable.netmanager,
		R.drawable.trojan,R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
	public int getCount() {
		return names.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//1.��xml����ת����һ��view����.
		View view = View.inflate(context, R.layout.grid_item_main, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_item_main_name);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_item_main_icon);
		tv.setText(names[position]);
		iv.setImageResource(icons[position]);
		return view;
	}

}
