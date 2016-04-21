package course.util;

import java.util.Comparator;

import course.bean.WifiBean;


/**排序 热点*/
public class ComparatorWifi implements Comparator {
	@Override
	public int compare(Object lhs, Object rhs) {
		WifiBean wifiBean1 = (WifiBean)lhs;
		WifiBean wifiBean2 = (WifiBean)rhs;
		int flag =wifiBean1.getFalg().compareTo(wifiBean2.getFalg());
		if(flag == 0){
			return wifiBean1.getSsid().compareTo(wifiBean2.getSsid());
		}else {
			return flag;
		}
	}
}
