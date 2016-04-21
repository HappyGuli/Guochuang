package course.util;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * 上传工具类
 * @author spring sky<br>
 * Email :vipa1888@163.com<br>
 * QQ: 840950105<br>
 * 支持上传文件和参数
 */
public class UploadUtil {

	//用来解析 图片地址的
	public static final String mBitmapTag_pre = "p_";
	private static UploadUtil uploadUtil;
	private static final String BOUNDARY =  UUID.randomUUID().toString(); // 边界标识 随机生成
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型

	private UploadUtil() {

	}


	/**
	 * 单例模式获取上传工具类
	 * @return
	 */
	public static UploadUtil getInstance() {
		if (null == uploadUtil) {
			uploadUtil = new UploadUtil();
		}
		return uploadUtil;
	}

	private static final String TAG = "TTTT";
	private int readTimeOut = 10 * 1000; // 读取超时
	private int connectTimeout = 10 * 1000; // 超时时间
	/***
	 * 请求使用多长时间
	 */
	private static int requestTime = 0;
	
	private static final String CHARSET = "utf-8"; // 设置编码

	/***
	 * 上传成功
	 */
	public static final int UPLOAD_SUCCESS_CODE = 11;
	/**
	 * 文件不存在
	 */
	public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 22;
	/**
	 * 服务器出错
	 */
	public static final int UPLOAD_SERVER_ERROR_CODE = 33;





	/**
	 *
	 * 将edittext中的spnnableString  上传string指向的图像文件
	 */
	public void upLoadFiles(final List<String> files, final String fileKey,
						   final String RequestURL, final Map<String, String> param ,final boolean isUploadUserImg) {

		new Thread(new Runnable() {  //开启线程上传文件
			@Override
			public void run() {
				List<String> list_result = new ArrayList<String>();
				File file;
				try {

					for(int i =0;i<files.size();i++){
						String str_file =new String();

						str_file = files.get(i);

						if(str_file.startsWith(UploadUtil.mBitmapTag_pre)){
							//除去图片地址的前缀 mBitmapTag_pre
							str_file = str_file.substring(2);

							//输出测试
							Log.e("TTTT","trimmed str_file "+str_file);


							file = new File(str_file);
							String str_result = toUploadFile(file, fileKey, RequestURL, param,isUploadUserImg);

							//将str_result放入到 list——result里面
							list_result.add(str_result);

						}else{

							//输出测试
							Log.e("TTTT","trimmed str_file"+str_file);
							//将str_file放入到 list——result里面
							list_result.add(str_file);
						}
					}


					//返回结果
					sendMessage(UPLOAD_SUCCESS_CODE, list_result);


				} catch (Exception e) {

					List<String> resutl = new ArrayList<String>();
					resutl.add("文件不存在");
					sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,resutl);
					e.printStackTrace();
					return ;
				}

			}
		}).start();

	}







	/**
	 * 上传文件到 服务器 并且返回文件在服务器中的地址
	 * @param file
	 * @param fileKey
	 * @param RequestURL
	 * @param param
	 * @return
	 */
	private String toUploadFile(File file, String fileKey, String RequestURL,
			Map<String, String> param,boolean isUploadUserImg) {
		String result = null;
		requestTime= 0;
		
		long requestTime = System.currentTimeMillis();
		long responseTime = 0;

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(readTimeOut);
			conn.setConnectTimeout(connectTimeout);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			/**
			 * 当文件不为空，把文件包装并且上传
			 */
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = null;
			String params = "";
			
			/***
			 * 以下是用于上传参数
			 */
			if (param != null && param.size() > 0) {
				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					sb = null;
					sb = new StringBuffer();
					String key = it.next();
					String value = param.get(key);
					sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
					sb.append(value).append(LINE_END);
					params = sb.toString();
					Log.i(TAG, key + "=" + params + "##");
					dos.write(params.getBytes());
//					dos.flush();
				}
			}
			
			sb = null;
			params = null;
			sb = new StringBuffer();
			/**
			 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
			 * filename是文件的名字，包含后缀名的 比如:abc.png
			 */
			sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
			sb.append("Content-Disposition:form-data; name=\"" + fileKey
					+ "\"; filename=\"" + file.getName() + "\"" + LINE_END);
			sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
			sb.append(LINE_END);
			params = sb.toString();
			sb = null;
			
			Log.i(TAG, file.getName() + "=" + params + "##");
			dos.write(params.getBytes());
			/**上传文件*/
			InputStream is = new FileInputStream(file);
			onUploadProcessListener.initUpload((int)file.length());
			byte[] bytes = new byte[1024];
			int len = 0;
			int curLen = 0;
			while ((len = is.read(bytes)) != -1) {
				curLen += len;
				dos.write(bytes, 0, len);
				onUploadProcessListener.onUploadProcess(curLen);
			}
			is.close();
			
			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
//			
//			dos.write(tempOutputStream.toByteArray());

			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();
			responseTime = System.currentTimeMillis();
			this.requestTime = (int) ((responseTime-requestTime)/1000);
			Log.e(TAG, "response code:" + res);
			if (res == 200) {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);


				//sendMessage(UPLOAD_SUCCESS_CODE, result);

				//如果上传的是头像的图片 则不要加 "<img src='"

				if(isUploadUserImg){
					result = result;

				}else{
					result = "<img src='"+result+"' />";
				}


				return  result;
			} else {
				Log.e(TAG, "request error");
				List<String> resutl = new ArrayList<String>();
				resutl.add("上传失败：code=" + res);
				sendMessage(UPLOAD_SERVER_ERROR_CODE,resutl);
				return null;
			}
		} catch (MalformedURLException e) {
			List<String> resutl = new ArrayList<String>();
			resutl.add("上传失败：error=" + e.getMessage());
			sendMessage(UPLOAD_SERVER_ERROR_CODE,resutl);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			List<String> resutl = new ArrayList<String>();
			resutl.add("上传失败：error=" + e.getMessage());
			sendMessage(UPLOAD_SERVER_ERROR_CODE, resutl);
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 发送上传结果
	 * @param responseCode
	 * @param responseMessage
	 */
	private void sendMessage(int responseCode,List<String> responseMessage)
	{
		onUploadProcessListener.onUploadDone(responseCode, responseMessage);
	}









	/**
	 * 下面是一个自定义的回调函数，用到回调上传文件是否完成
	 * 
	 * @author shimingzheng
	 * 
	 */
	public static interface OnUploadProcessListener {
		/**
		 * 上传响应
		 * @param responseCode
		 * @param message
		 */
		void onUploadDone(int responseCode, List<String> message);
		/**
		 * 上传中
		 * @param uploadSize
		 */
		void onUploadProcess(int uploadSize);
		/**
		 * 准备上传
		 * @param fileSize
		 */
		void initUpload(int fileSize);
	}

	private OnUploadProcessListener onUploadProcessListener;
	
	

	public void setOnUploadProcessListener(
			OnUploadProcessListener onUploadProcessListener) {
		this.onUploadProcessListener = onUploadProcessListener;
	}

	public int getReadTimeOut() {
		return readTimeOut;
	}

	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	/**
	 * 获取上传使用的时间
	 * @return
	 */
	public static int getRequestTime() {
		return requestTime;
	}


	public static interface uploadProcessListener{
		
	}
	
	
	
	
}
