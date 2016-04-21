package course.util;

import java.security.MessageDigest;

/**
 */
public class MD5 {

    private static MessageDigest md5 = null;
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param
     * @return
     */
    public static String getMd5(String str) {
        byte[] bs = md5.digest(str.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for(byte x:bs) {
            if((x & 0xff)>>4 == 0) {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return sb.toString();
    }

    
    public static String getDiff(String stuNum,String stuPwd){
    	 
    	 String mima = MD5.getMd5(stuPwd).substring(0,30).toUpperCase()+"10611";
         String xuehao= stuNum+mima;
         return MD5.getMd5(xuehao).substring(0, 30).toUpperCase();
    }
    public static void main(String[] args) {
       
    }
}