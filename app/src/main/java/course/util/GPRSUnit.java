package course.util;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.reflect.Method;

public class GPRSUnit {

	private ConnectivityManager mCM;

	public GPRSUnit(ConnectivityManager mCM) {
		this.mCM = mCM;
	}

	// 打开或关闭GPRS
	public  boolean gprsEnable(boolean bEnable) {
		Object[] argObjects = null;

		boolean isOpen = gprsIsOpenMethod("getMobileDataEnabled");
		if (isOpen == !bEnable) {
			setGprsEnable("setMobileDataEnabled", bEnable);
		}

		return isOpen;
	}

	// 检测GPRS是否打开
	public boolean gprsIsOpenMethod(String methodName) {
		Class cmClass = mCM.getClass();
		Class[] argClasses = null;
		Object[] argObject = null;

		Boolean isOpen = false;
		try {
			Method method = cmClass.getMethod(methodName, argClasses);

			isOpen = (Boolean) method.invoke(mCM, argObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isOpen;
	}

	// 开启/关闭GPRS
	private  void setGprsEnable(String methodName, boolean isEnable) {
		Class cmClass = mCM.getClass();
		Class[] argClasses = new Class[1];
		argClasses[0] = boolean.class;

		try {
			Method method = cmClass.getMethod(methodName, argClasses);
			method.invoke(mCM, isEnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 检验网络连接 并toast提示
	 *
	 * @return
	 */
	public  boolean isNetworkAvailable(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifi.isConnected())
			return true;
		else
			return false;
	}
}
