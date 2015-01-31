package cn.itcast.mobilesafe.domain;
/**
 * ���ص绰ģʽ��װ
 * @author superboy
 *
 */
public class BlackNumberBean {

	private String number;
	private String mode; // 0 ȫ������ 1�绰���� 2��������

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		if ("0".equals(mode) || "1".equals(mode) || "2".equals(mode)) {
			this.mode = mode;
		}else{
			this.mode = "0";
		}
	}

	@Override
	public String toString() {
		return "BlackNumberBean [number=" + number + ", mode=" + mode + "]";
	}
	
	
}
