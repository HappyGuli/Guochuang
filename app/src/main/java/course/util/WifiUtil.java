package course.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WifiUtil extends Thread {

	public static final int WIFICIPHER_NOPASS = 1; // WIFICIPHER_NOPASS
	public static final int WIFICIPHER_WEP = 2; // WIFICIPHER_WEP
	public static final int WIFICIPHER_WPA = 3; // WIFICIPHER_WPA
	private static WifiManager wifiManager;
	private Context context;
	private WifiInfo mWifiInfo;
	/** 手机保持的连接wifi */
	private static List<WifiConfiguration> wifiConfigurations = new ArrayList<WifiConfiguration>();
	private Handler mHandler;
	public static boolean falg = true;

	// List<ScanResult> aList;
	public WifiUtil(Context context, Handler handler) {
		this.mHandler = handler;
		this.context = context;
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = wifiManager.getConnectionInfo();
		wifiConfigurations = wifiManager.getConfiguredNetworks();
	}

	private List<ScanResult> getListScResults() {
		wifiManager.startScan();
		return wifiManager.getScanResults();
	}

	public WifiInfo getWifiInfo() {
		return mWifiInfo;
	}

	public WifiManager getWifiManager() {
		return wifiManager;
	}

	public static boolean isFalg() {
		return falg;
	}

	public static void setFalg(boolean falag) {
		falg = falag;
	}

	/**
	 * 连接wifi
	 *
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return
	 */
	public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = isExsits(SSID);
		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == WIFICIPHER_NOPASS) // WIFICIPHER_NOPASS
		{
			config.hiddenSSID = true;
			//config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			//config.wepTxKeyIndex = 0;
		}
		if (Type == WIFICIPHER_WEP) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WIFICIPHER_WPA) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	/**
	 * 判断是否已经被记住
	 *
	 * @param SSID
	 * @param wifiManager
	 * @return
	 */
	public WifiConfiguration isExsits(String SSID) {
		if (wifiConfigurations != null) {
			for (WifiConfiguration existingConfig : wifiConfigurations) {
				if (existingConfig.SSID != null && existingConfig.SSID.equals("\"" + SSID + "\"")) {
					return existingConfig;
				}
			}
		}

		return null;
	}

	/** 断开指定ID的网络 */
	public void breakWifi(String ssid) {
		if (wifiConfigurations != null) {
			for (WifiConfiguration existingConfig : wifiConfigurations) {
				if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
					Log.i("", "*******断开网络******" + ssid);
					wifiManager.disableNetwork(existingConfig.networkId);
					wifiManager.disconnect();
					wifiConfigurations = wifiManager.getConfiguredNetworks();
				}
			}
		}

	}

	/** 忘记网络 */
	public void forGetPw(String ssid) {
		if (wifiConfigurations != null) {
			for (WifiConfiguration existingConfig : wifiConfigurations) {
				if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
					Log.i("", "*******忘记网络******" + ssid);
					wifiManager.removeNetwork(existingConfig.networkId);
					wifiConfigurations = wifiManager.getConfiguredNetworks();

				}
			}
		}

	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(String ssid) {
		if (wifiConfigurations != null) {
			for (WifiConfiguration existingConfig : wifiConfigurations) {
				if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
					// 连接配置好的指定ID的网络
					wifiManager.enableNetwork(existingConfig.networkId, true);
				}
			}
		}
	}

	/** 打开WIFI */
	public void openWifi() {
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
	}

	/** 关闭WIFI */
	public void closeWifi() {
		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
	}

	public void run() {
		while (falg) {
			Message message = Message.obtain();
			Log.i("", "**************Thread id *************" + this.getId());
			message.what = 1;
			wifiManager.startScan();
			message.obj = wifiManager.getScanResults();
			mHandler.sendMessage(message);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean checkWIFIConnection(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi.isConnected())
			return true;
		else
			return false;
	}



	/**
	 * @param i
	 * @return
	 */
	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
	}
}