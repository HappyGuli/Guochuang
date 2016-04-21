package api;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * 接口URL实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class URLs implements Serializable {

	//public final static String HOST = "10.0.2.2:8080";

	//public final static String HOST = "172.24.2.171:8080";
	public final static String HOST = "123.57.146.81:8080";
	public final static String HTTP = "http://";
	private final static String URL_SPLITTER = "/";

	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;


	public final static String QUESTIONS_LIST = URL_API_HOST+"/Experiment/android/findqsbycid.jsp?cid=";
	public final static String ANSWERS_LIST = URL_API_HOST+"/Experiment/android/findansbyqid.jsp?qid=";
	public final static String COMMENTS_LIST = URL_API_HOST+"/Experiment/android/findComment.jsp?ansid=";
	public final static String SAVECOMMENTS_LIST = URL_API_HOST+"/Experiment/android/saveComment.jsp?";
	public final static String SAVEVOLT = URL_API_HOST+"/Experiment/android/voltOrVoltdown.jsp?";
	public final static String SAVEQUESTION = URL_API_HOST+"/Experiment/android/saveUserQuestion.jsp";
	public final static String SAVEANSWER= URL_API_HOST+"/Experiment/android/saveUserAnswer.jsp";
	public final static String GETQUESTION= URL_API_HOST+"/Experiment/android/findQuestionByQid.jsp?qid=";
	public final static String TEACHER_STUDENT_INFO= URL_API_HOST+"Experiment/android/FidStdInfoByTname.jsp";
	public final static String SAVE_USER_COURSETABLE= URL_API_HOST+"Experiment/android/SaveUsrCrsTable.jsp";
	public final static String SAVEUSERINFO= URL_API_HOST+"Experiment/android/SaveUsrInfo.jsp";
	public final static String COURSEBOARD_LIST= URL_API_HOST+"Experiment/android/FindCourseBoards.jsp";
	public final static String REGISTER = URL_API_HOST+"/Experiment/android/findUserToken.jsp";
	public final static String ADDEDTOTEAM = URL_API_HOST+"/Experiment/android/addToCrspTeam.jsp";
	public final static String SAVEUSERCOLLECT = URL_API_HOST+"/Experiment/android/saveUserCollect.jsp";
	public final static String FINDUSERCOLLECT = URL_API_HOST+"/Experiment/android/findUserCollect.jsp";
	public final static String FINDANSWERDETAIL = URL_API_HOST+"/Experiment/android/findAnswerDetail.jsp";
	public final static String FINDMYANSWED = URL_API_HOST+"/Experiment/android/findUserAnswered.jsp";
	public final static String FINDISANSWERVOLTED = URL_API_HOST+"/Experiment/android/isVoltOrVoltdown.jsp";
	public final static String SAVECANCELVOLTORVOLTDOWN = URL_API_HOST+"/Experiment/android/cancelVltOrVltDown.jsp";
	public final static String FINDMYQUESTIONS = URL_API_HOST+"/Experiment/android/findMyQuesitons.jsp";
	public final static String FINDUSERCNTINFO = URL_API_HOST+"/Experiment/android/findUserntInfo.jsp";
	public final static String SAVEUSERCOMMENT = URL_API_HOST+"/Experiment/android/saveUserComment.jsp";
	public final static String DELETEUSERCOMMENT = URL_API_HOST+"/Experiment/android/deleteUserComment.jsp";
	public final static String UPLOADANSWERPIC = URL_API_HOST+"/Experiment/android/UploadHandleServlet";
	public final static String UPLOADHEADPIC = URL_API_HOST+"/Experiment/android/UploadHandleProfileServlet";
	public final static String UPDATEUSERHEADURL = URL_API_HOST+"/Experiment/android/UpDateUsrImg";


	private int objId;
	private String objKey = "";
	private int objType;

	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getObjKey() {
		return objKey;
	}
	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}
	public int getObjType() {
		return objType;
	}
	public void setObjType(int objType) {
		this.objType = objType;
	}

	/**
	 * 解析url获得objId
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjId(String path, String url_type){
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains(URL_SPLITTER)){
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		}else{
			objId = str;
		}
		return objId;
	}

	/**
	 * 解析url获得objKey
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type){
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains("?")){
			tmp = str.split("?");
			objKey = tmp[0];
		}else{
			objKey = str;
		}
		return objKey;
	}

	/**
	 * 对URL进行格式处理
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if(path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}
}
