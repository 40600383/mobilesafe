package cn.itcast.mobilesafe.domain;

public class AppTraffic {
	private int uid;// ���̵�uid
	private String name;// ���� ������
	private long uidRxBytes;// ����������
	private long uidTxBytes;// ����������

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getUidRxBytes() {
		return uidRxBytes;
	}

	public void setUidRxBytes(long uidRxBytes) {
		this.uidRxBytes = uidRxBytes;
	}

	public long getUidTxBytes() {
		return uidTxBytes;
	}

	public void setUidTxBytes(long uidTxBytes) {
		this.uidTxBytes = uidTxBytes;
	}

}
