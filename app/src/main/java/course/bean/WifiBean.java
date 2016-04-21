package course.bean;

/**
 *
 * @filename WifiBean.java
 * @description l閿熸枻鎷烽敓楗虹鎷烽敓鏂ゆ嫹閿燂拷
 * @author Administrator
 * @created 2014-4-14 閿熸枻鎷烽敓鏂ゆ嫹5:00:08
 *
 */
public class WifiBean {
	private String falg;//
	private String ssid;// ssid
	private boolean isSelect;// 连接
	private boolean isRead;// 记住
	private String passType, deviceMac;// 加密方式，设备mac
	private boolean isIntent;// 是否能上网

	public boolean isIntent() {
		return isIntent;
	}

	public void setIntent(boolean isIntent) {
		this.isIntent = isIntent;
	}

	public String getFalg() {
		return falg;
	}

	public void setFalg(String falg) {
		this.falg = falg;
	}

	private int leave;

	public int getLeave() {
		return leave;
	}

	public void setLeave(int leave) {
		this.leave = leave;
	}

	public String getDeviceMac() {
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getPassType() {
		return passType;
	}

	public void setPassType(String passType) {
		this.passType = passType;
	}
}
