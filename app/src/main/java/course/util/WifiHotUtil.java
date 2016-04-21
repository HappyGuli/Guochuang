package course.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @ClassName:  WifiHotUtil   
 * @Description:  打印日志信息WiFi热点工具
 * @author: jajuan.wang  
 * @date:   2015-05-28 15:12  
 * version:1.0.0
 */   
public class WifiHotUtil {  
    public static final String TAG = "WifiApAdmin";

    private WifiManager mWifiManager = null;

    private Context mContext = null;
    public WifiHotUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }


    //判断AP是否开启
    public boolean isWifiApEnabled() {  
        try {  
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);  
            return (Boolean) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  
        } catch (Exception e) {
            e.printStackTrace();  
        }  
        return false;  
    }

    //判断WIFI是否打开
    private boolean isWifiConnected(){

        ConnectivityManager cm;

        cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ? true : false ;

    }


    //获取连接到自己热点的学生
    public ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {

                //Log.e("TTTT", line);

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String mac = splitted[3];
                    if(!mac.equals("type")){

                        StringBuffer stringBuffer = new StringBuffer();
                        String[] macs = mac.split(":");
                        for(int i=0;i<macs.length;i++){
                            stringBuffer.append(macs[i]);
                        }
                        connectedIP.add(stringBuffer.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }


//    public String getLocalMacAddress() {
//        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        return info.getMacAddress();
//    }

} 