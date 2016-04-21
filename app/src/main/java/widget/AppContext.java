package widget;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.GuardedObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import api.ApiClient;
import common.StringUtils;
import common.UIHelper;
import course.netdata.AnswerDetailBean;
import course.netdata.AnswerToSpecQuestionBeanList;
import course.netdata.CollectInfoBean;
import course.netdata.CollectInfoBeanList;
import course.netdata.CommentToSpecAnswerBeanList;
import course.netdata.CourseboardBean;
import course.netdata.MyAnsweredBean;
import course.netdata.MyAnsweredBeanList;
import course.netdata.MyQuestionBean;
import course.netdata.MyQuestionBeanList;
import course.netdata.QuesitonInSpecificCourseBeanList;
import course.netdata.StudentInfoInClassBeanList;
import course.netdata.UserInfoBean;


/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext {

	private Application application;

	public static final int NETTYPE_WIFI  = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 10;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	//构造函数　
	public AppContext(Application application){

		this.application = application;
	}



	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

	private Handler unLoginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				UIHelper.ToastMessage(application,
						"还没有登陆");
			}
		}
	};



	/**
	 * 检测当前系统声音是否为正常模式
	 *
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)application.getSystemService(Application.AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 应用程序是否发出提示音
	 *
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}

	/**
	 * 检测网络是否可用
	 *
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 *
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 *
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信息
	 *
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取App唯一标识
	 *
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 是否加载显示文章图片
	 *
	 * @return
	 */
	public boolean isLoadImage() {
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		// 默认是加载的
		if (StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}

	/**
	 * 设置是否加载文章图片
	 *
	 * @param b
	 */
	public void setConfigLoadimage(boolean b) {
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}

	/**
	 * 是否发出提示音
	 *
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		// 默认是开启提示声音
		if (StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}

	/**
	 * 设置是否发出提示音
	 *
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * 是否左右滑动
	 *
	 * @return
	 */
	public boolean isScroll() {
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		// 默认是关闭左右滑动
		if (StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}

	/**
	 * 设置是否左右滑动
	 *
	 * @param b
	 */
	public void setConfigScroll(boolean b) {
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}

	/**
	 * 是否Https登录
	 *
	 * @return
	 */
	public boolean isHttpsLogin() {
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		// 默认是http
		if (StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}

	/**
	 * 设置是是否Https登录
	 *
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b) {
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}

	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * 判断缓存数据是否可读
	 *
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存是否存在
	 *
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = application.getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 *
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = application.getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		// //清除webview缓存
		// File file = CacheManager.getCacheFileBaseDir();
		// if (file != null && file.exists() && file.isDirectory()) {
		// for (File item : file.listFiles()) {
		// item.delete();
		// }
		// file.delete();
		// }
		// deleteDatabase("webview.db");
		// deleteDatabase("webview.db-shm");
		// deleteDatabase("webview.db-wal");
		// deleteDatabase("webviewCache.db");
		// deleteDatabase("webviewCache.db-shm");
		// deleteDatabase("webviewCache.db-wal");
		// //清除数据缓存
		// clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		// clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		// //2.2版本才有将应用缓存转移到sd卡的功能
		// if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
		// clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		// }
		// //清除编辑器保存的临时内容
		// Properties props = getProperties();
		// for(Object key : props.keySet()) {
		// String _key = key.toString();
		// if(_key.startsWith("temp"))
		// removeProperty(_key);
		// }
	}

	/**
	 * 清除缓存目录
	 *
	 * @param dir
	 *            目录
	 * @param
	 *
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 将对象保存到内存缓存中
	 *
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * 从内存缓存中获取对象
	 *
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * 保存磁盘缓存
	 *
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = application.openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取磁盘缓存数据
	 *
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = application.openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}



	/**
	 * 读取对象
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = application.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = application.getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 保存对象
	 *
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = application.openFileOutput(file, Application.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}


	/**
	 * 获取课程中问题数据列表
	 *
	 * @return
	 * @throws AppException
	 */
	public QuesitonInSpecificCourseBeanList getQuestionsList(boolean isRefresh,String str_cid) throws AppException {
		QuesitonInSpecificCourseBeanList list = null;
		String key = "questions_in_"+str_cid;
		//如果是刷新数据
		if(isRefresh){
			if(isNetworkConnected()){
				try {
					list = ApiClient.getQuestionsByCid(this, str_cid);
				} catch (AppException e) {
					list = (QuesitonInSpecificCourseBeanList) readObject(key);
					if (list == null)
						throw e;
				}
			}else{
				list = new QuesitonInSpecificCourseBeanList();
			}

		}
		//如果不是刷新
		else{
			if (isNetworkConnected() && !isReadDataCache(key)) {
				try {
					list = ApiClient.getQuestionsByCid(this, str_cid);
				} catch (AppException e) {
					list = (QuesitonInSpecificCourseBeanList) readObject(key);
					if (list == null)
						throw e;
				}
			} else {
				list = (QuesitonInSpecificCourseBeanList) readObject(key);
				if (list == null)
					list = new QuesitonInSpecificCourseBeanList();
			}
		}

		return list;
	}


	/**
	 * 获取问题对应的所有答案
	 * @param qid
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public AnswerToSpecQuestionBeanList getAnswerList(int qid,boolean isRefresh) throws AppException {

		AnswerToSpecQuestionBeanList list = null;
		String key = "answerslist_";
		//如果是刷新数据
		if(isRefresh){
			if(isNetworkConnected()){
				try {
					list = ApiClient.getAnswersByQid(this, qid);
				} catch (AppException e) {
					if (list == null)
						throw e;
				}
			}else{
				list = new AnswerToSpecQuestionBeanList();
			}

		}
		//如果不是刷新
		else{
			if (isNetworkConnected() && !isReadDataCache(key)) {
				try {
					list = ApiClient.getAnswersByQid(this, qid);
				} catch (AppException e) {
					//list = (QuestionFromServerBeanList) readObject(key);
					if (list == null)
						throw e;
				}
			} else {
				list = (AnswerToSpecQuestionBeanList) readObject(key);
				if (list == null)
					list = new AnswerToSpecQuestionBeanList();
			}
		}

		return list;
	}


	/**
	 * 获取问题对应的所有答案
	 * @return
	 * @throws AppException
	 */
	public StudentInfoInClassBeanList getStudentsInfo(String cid,String tname) throws AppException {

		StudentInfoInClassBeanList list = null;
		String key = "studentlist_";


		if (isNetworkConnected() && !isReadDataCache(key)) {
			try {
				list = ApiClient.getStudentsByTname(this, cid,tname);
			} catch (AppException e) {
				//list = (QuestionFromServerBeanList) readObject(key);
				if (list == null)
					throw e;
			}
		} else {
			list = (StudentInfoInClassBeanList) readObject(key);
			if (list == null)
				list = new StudentInfoInClassBeanList();
		}

		return list;
	}


	/**
	 * 获取某个回答的评论信息
	 * @param ansid  答案的标识
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public CommentToSpecAnswerBeanList getCommentList(int ansid,boolean isRefresh) throws AppException {

		CommentToSpecAnswerBeanList list = null;
		String key = "commentslist_";
		//如果是刷新数据
		if(isRefresh){
			if(isNetworkConnected()){
				try {
					list = ApiClient.getCommentsByAnsid(this, ansid);
				} catch (AppException e) {
					if (list == null)
						throw e;
				}
			}else{
				list = new CommentToSpecAnswerBeanList();
			}

		}
		//如果不是刷新
		else{
			if (isNetworkConnected() && !isReadDataCache(key)) {
				try {
					list = ApiClient.getCommentsByAnsid(this, ansid);
				} catch (AppException e) {
					//list = (QuestionFromServerBeanList) readObject(key);
					if (list == null)
						throw e;
				}
			} else {
				list = (CommentToSpecAnswerBeanList) readObject(key);
				if (list == null)
					list = new CommentToSpecAnswerBeanList();
			}
		}

		return list;

	}


	/**
	 * 通过cid 和 tname 获取某课程中课程公告信息
	 * @param cid
	 * @param tname
	 * @return
	 * @throws AppException
	 */
	public List<CourseboardBean>  getCrsBoardsBycidAndTname(String cid,String tname) throws AppException {

		return ApiClient.findCrsBrdsBycisTnm(this,cid,tname);
	}




	/**
	 * 获取用户登录之后 在云信server端获取的 token account name
	 * @return
	 */
	public static String getRegisterToken(AppContext appContext,String sid)throws AppException{

		try{
			//从服务器端获取数据
			String result = ApiClient.UserRegister(appContext, sid);

			Log.e("TTTT", result);


			String token = null;
			String accid = null;
			String name = null;

			StringBuffer sb_result = new StringBuffer();
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if("200".equals(code)){

					String inner_result = jobj.getString("result");
					JSONObject jsonObj = new JSONObject(inner_result);

					//得到token
					token = jsonObj.getString("token");
					name = jsonObj.getString("name");
					accid = jsonObj.getString("accid");

				}else{
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);

				}

			}catch (JSONException e){
				e.printStackTrace();
				return null;
			}

			sb_result.append(token+"_"+name+"_"+accid);
			// 返回结果
			return sb_result.toString();

		}catch (Exception e){
			e.printStackTrace();
			throw  AppException.http(e);
		}
	}


	/**
	 * 将用户加入到对应课程中的讨论组当中
	 * @return
	 */
	public static void addUserTocrsTeam(AppContext appContext,String sid)throws AppException{

		try{
			//从服务器端获取数据
			String result = ApiClient.AddUserToCrsTeam(appContext, sid);

			Log.e("TTTT", result);


			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if("200".equals(code)){
					return;
				}else{
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			}catch (JSONException e){
				e.printStackTrace();
				return ;

			}
		}catch (Exception e){
			e.printStackTrace();
			throw AppException.http(e);
		}
	}



	/**
	 * 将用户收藏问答的信息保存起来
	 * @return
	 */
	public static void saveUserCollect(AppContext appContext,String sid,int ansid)throws AppException{

		try{
			//从服务器端获取数据
			String result = ApiClient.SaveUserClooectInfo(appContext, sid, ansid);
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if("200".equals(code)){
					return;
				}else{
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			}catch (JSONException e){
				e.printStackTrace();
				return ;
			}
		}catch (Exception e){
			e.printStackTrace();
			throw AppException.http(e);
		}
	}


	/**
	 * 根据用户id 查找其收藏的问答的信息
	 * @return
	 */
	public static List<CollectInfoBean> findUserCollectList(AppContext appContext,String sid)throws AppException {

		try {
			//从服务器端获取数据
			String result = ApiClient.FindUserCollectInfoList(appContext, sid);
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if ("200".equals(code)) {

					// 返回解析之后的 CollectInfoBean 的集合
					return (CollectInfoBeanList.parse(StringUtils.toJSONArray(jobj.getString("result")))).getList();

				} else {
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				throw new Exception("解析出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

		//如果出错了 返回null
		return null;
	}


	/**
	 * 根据ansid 查找answer的详细信息
	 * @return
	 */
	public static AnswerDetailBean  findAnserDetail(AppContext appContext,int ansid )throws AppException {

		try {
			//从服务器端获取数据
			String result = ApiClient.FindAnswerDetail(appContext, ansid);
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if ("200".equals(code)) {

					String inner_result = jobj.getString("result");

					JSONObject j =  new JSONObject(inner_result);

					//将json对象转换为 AnswerDetailBean对象
					AnswerDetailBean bean = new AnswerDetailBean();
					bean.setQuestionTitle(j.getString("questionTitle"));
					bean.setAnswerContent(j.getString("answerContent"));
					bean.setZanNum(j.getInt("zanNum"));
					bean.setCaiNum(j.getInt("caiNum"));
					bean.setUserName(j.getString("userName"));

					//返回AnswerDetailBean对象
					return bean;

				} else {
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			} catch (JSONException e) {
				e.printStackTrace();

				throw new Exception("解析出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

		//如果出错了 返回null
		return null;
	}


	/**
	 * 根据用户id 查找其回答过的所有问题
	 * @return
	 */
	public static List<MyAnsweredBean> FindUserAnswered(AppContext appContext,String sid)throws AppException {

		try {
			//从服务器端获取数据
			String result = ApiClient.FindUserAnswered(appContext, sid);
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if ("200".equals(code)) {

					// 返回解析之后的 CollectInfoBean 的集合
					return (MyAnsweredBeanList.parse(StringUtils.toJSONArray(jobj.getString("result")))).getList();

				} else {
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				throw new Exception("解析出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

		//如果出错了 返回null
		return null;
	}


	/**
	 * 查看用户对某个回答是否点赞  或者  点踩
	 * @param appContext
	 * @param sid
	 * @param ansid
	 * @return
	 * @throws AppException
	 */
	public static int FindIsAnswerVoltOrVoltdown(AppContext appContext,String sid,int ansid)
			throws AppException {

		try {
			//从服务器端获取数据
			String result = ApiClient.FindIsAnswerVoltedOrVoltdown(appContext, sid, ansid);
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if ("200".equals(code)) {

					String inner_result = jobj.getString("result");

					//返回 从服务器中的到的结果
					return Integer.valueOf(inner_result);

				} else {
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				throw new Exception("解析出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

		//如果出错了 返回null
		return 0;
	}


	/**
	 *保存用户  取消 点赞 和 点踩的信息
	 * @param appContext
	 * @param sid
	 * @param ansid
	 * @param isVolt
	 * @return
	 * @throws AppException
	 */
	public static void SaveUserCancelVoltOrVoltdown(AppContext appContext,String sid,int ansid,int isVolt)
			throws AppException {

		try {
			//从服务器端获取数据
			String result = ApiClient.saveUserCancelVoltOrVoltdown(appContext, sid, ansid, isVolt);
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if ("200".equals(code)) {
					//返回
					return;

				} else {
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				throw new Exception("解析出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

		//如果出错了 返回null
		return ;
	}


	/**
	 * 通过 sid 查找用户发布过的所有问题
	 * @param appContext
	 * @param sid
	 * @return  返回的是 MyQuestionBean的集合
	 * @throws AppException
	 */
	public static List<MyQuestionBean> FindUserQuestioned(AppContext appContext,String sid)throws AppException {

		try {
			//从服务器端获取数据
			String result = ApiClient.FindUserQuestioned(appContext, sid);
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if ("200".equals(code)) {

					// 返回解析之后的 CollectInfoBean 的集合
					return (MyQuestionBeanList.parse(StringUtils.toJSONArray(jobj.getString("result")))).getList();

				} else {
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				throw new Exception("解析出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

		//如果出错了 返回null
		return null;
	}


	/**
	 * 根据sid 查找用户的一些基本信息  比如发布了多少问题 提出了多少问题
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static UserInfoBean FindUserCntInfo(AppContext appContext,String sid )throws AppException {

		try {
			//从服务器端获取数据
			String result = ApiClient.FindUserCntInfo(appContext, sid);
			try {

				JSONObject jobj = new JSONObject(result);
				String code = jobj.getString("code");
				//如果获取成功
				if ("200".equals(code)) {

					String inner_result = jobj.getString("result");

					JSONObject j =  new JSONObject(inner_result);

					//将json对象转换为 AnswerDetailBean对象
					UserInfoBean bean = new UserInfoBean();
					bean.setUserName(j.getString("userName"));
					bean.setSid(j.getString("sid"));
					bean.setAbilityVlu(j.getLong("abilityVlu"));
					bean.setAnswerCnt(j.getLong("answerCnt"));
					bean.setAttentionCnt(j.getLong("attentionCnt"));
					bean.setCollectCnt(j.getLong("collectCnt"));
					bean.setQuestionedCnt(j.getLong("questionedCnt"));
					bean.setUerLevel(j.getString("uerLevel"));
					bean.setImgUrl(j.getString("imgUrl"));

					//返回AnswerDetailBean对象
					return bean;

				} else {
					String inner_result = jobj.getString("msg");
					UIHelper.ToastMessage(appContext.application, inner_result, 0);
				}

			} catch (JSONException e) {
				e.printStackTrace();

				throw new Exception("解析出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

		//如果出错了 返回null
		return null;
	}


	/**
	 * 保存用户评论信息
	 * @param appContext
	 * @param commentContent
	 * @param ansid
	 * @param sid
	 * @throws AppException
	 */
	public static void SaveUserCommentInfo(AppContext appContext,String commentContent,int ansid,String sid)throws AppException {

		try {
			//从服务器端获取数据
			String result = ApiClient.SaveUserCommentInfo(appContext, commentContent, ansid, sid);

			JSONObject jobj = new JSONObject(result);

			String code = jobj.getString("code");

			//如果获取成功 直接返回
			if ("200".equals(code)){
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

	}


	/**
	 * 删除用户的 评论信息
	 * @param appContext
	 * @throws AppException
	 */
	public static void DeleteUserCommentInfo(AppContext appContext,int commentid)throws AppException {

		try {
			//从服务器
			String result = ApiClient.DeleteUserCommentInfo(appContext, commentid);

			JSONObject jobj = new JSONObject(result);

			String code = jobj.getString("code");

			//如果获取成功 直接返回
			if ("200".equals(code)){
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

	}


	/**
	 *   更新用户的头像的 imgUrl
	 * @param appContext
	 * @param sid
	 * @param imgUrl
	 * @throws AppException
	 */
	public static void UpdateUserHeadUrl(AppContext appContext,String sid,String imgUrl )throws AppException {

		try {
			//从服务器
			String result = ApiClient.UploadUserHeadUrl(appContext,sid,imgUrl);

			JSONObject jobj = new JSONObject(result);

			String code = jobj.getString("code");

			//如果获取成功 直接返回
			if ("200".equals(code)){
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}

	}











	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(application).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(application).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(application).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(application).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(application).remove(key);
	}

}
