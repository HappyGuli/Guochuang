package api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import common.StringUtils;
import course.netdata.AnswerToSpecQuestionBeanList;
import course.netdata.CollectInfoBeanList;
import course.netdata.CommentToSpecAnswerBeanList;
import course.netdata.CourseboardBean;
import course.netdata.CourseboardBeanList;
import course.netdata.QuesitonInSpecificCourseBeanList;
import course.netdata.StudentInfoInClassBeanList;
import widget.AppContext;
import widget.AppException;


/**
 * Created by happypaul on 16/1/28.
 */
public class ApiClient {


    public static final String UTF_8 = "UTF-8";
    public static final String DESC = "descend";
    public static final String ASC = "ascend";

    private final static int TIMEOUT_CONNECTION = 8000;
    private final static int TIMEOUT_SOCKET = 8000;
    private final static int RETRY_TIME = 2;

    private static String appCookie;
    private static String appUserAgent;


    public static String getCookie(AppContext appContext) {
        if (appCookie == null || appCookie == "") {
            appCookie = appContext.getProperty("cookie");

        }
        return appCookie;
    }

    private static String getUserAgent(AppContext appContext) {
        if (appUserAgent == null || appUserAgent == "") {
            StringBuilder ua = new StringBuilder("APP");
            ua.append('/' + appContext.getPackageInfo().versionName + '_'
                    + appContext.getPackageInfo().versionCode);// App版本
            ua.append("/Android");// 手机系统平台
            ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
            ua.append("/" + android.os.Build.MODEL); // 手机型号
            ua.append("/" + appContext.getAppId());// 客户端唯一标识
            appUserAgent = ua.toString();
        }
        return appUserAgent;
    }

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        httpClient.getParams().setCookiePolicy(
                CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());
        // 设置 连接超时时间
        httpClient.getHttpConnectionManager().getParams()
                .setConnectionTimeout(TIMEOUT_CONNECTION);
        // 设置 读数据超时时间
        httpClient.getHttpConnectionManager().getParams()
                .setSoTimeout(TIMEOUT_SOCKET);
        // 设置 字符集
        httpClient.getParams().setContentCharset("GBk");
        return httpClient;
    }

    private static GetMethod getHttpGet(String url, String cookie,
                                        String userAgent) {
        GetMethod httpGet = new GetMethod(url);
        // 设置 请求超时时间
        // cookie=" CYTY%5FERP=realname=002500010103&W%5Fno=0025000101&userID=11&username=002500010103";
        httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpGet.setRequestHeader("Host", URLs.HOST);
        httpGet.setRequestHeader("Connection", "Keep-Alive");
        httpGet.setRequestHeader("Cookie", cookie);
        httpGet.setRequestHeader("User-Agent", userAgent);
        return httpGet;
    }

    private static PostMethod getHttpPost(String url, String cookie,
                                          String userAgent) {
        PostMethod httpPost = new PostMethod(url);
        // 设置 请求超时时间
        httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpPost.setRequestHeader("Host", URLs.HOST);
        httpPost.setRequestHeader("Connection", "Keep-Alive");
        httpPost.setRequestHeader("Cookie", cookie);
        httpPost.setRequestHeader("User-Agent", userAgent);
        httpPost.getParams().setParameter(
                HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");


        return httpPost;
    }


    /**
     * get请求URL
     *
     * @param url
     * @throws AppException
     */
    private static String http_get(AppContext appContext, String url)
            throws AppException {
        // System.out.println("get_url==> "+url);
        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        GetMethod httpGet = null;
        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, cookie, userAgent);
                int statusCode = httpClient.executeMethod(httpGet);
                //代表没有成功的返回
                if (statusCode != 200) {
                    throw AppException.http(statusCode);
                }
                responseBody = httpGet.getResponseBodyAsString();
                // System.out.println("XMLDATA=====>"+responseBody);
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                // e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }



    /**
     * 发送JSONArray String 给服务器，  返回的是服务器端返回的数据
     * @throws AppException
     */
    public static String  saveJSONArray(AppContext appContext,String str_url,String str_jsonArray) throws AppException{

        // 访问的地址
        String finalUrl = str_url;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(finalUrl, cookie, userAgent);

                //让jsonArray  string 通过Post的entity发送过去

                StringRequestEntity entity = new StringRequestEntity(str_jsonArray);
                httpPost.setRequestEntity(entity);


                int statusCode = httpClient.executeMethod(httpPost);

                //代表没有成功的返回
                if (statusCode != 200) {
                    throw AppException.http(statusCode);
                }

                responseBody = httpPost.getResponseBodyAsString();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                // e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }



    /**
     * 获取问题数据列表
     *
     * @return
     * @throws AppException
     */
    public static QuesitonInSpecificCourseBeanList getQuestionsByCid(AppContext appContext,String str_cid)
            throws AppException {
        String newUrl = URLs.QUESTIONS_LIST+str_cid;

        try {

            Log.e("TTTT", newUrl);
            return QuesitonInSpecificCourseBeanList.parse(StringUtils.toJSONArray(http_get(appContext,
                    newUrl)));
        } catch (Exception e) {

            System.out.println(e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }


    /**
     * 通过qid 返回对应的答案列表
     * @param appContext
     * @param qid
     * @return
     * @throws AppException
     */
    public static AnswerToSpecQuestionBeanList getAnswersByQid(AppContext appContext,int qid)
            throws AppException {

        String newUrl = URLs.ANSWERS_LIST+qid;

        try {

            System.out.println("获取答案列表：" + newUrl);
            return AnswerToSpecQuestionBeanList.parse(StringUtils.toJSONArray(http_get(appContext,
                    newUrl)));
        } catch (Exception e) {

            System.out.println(e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }

    }


    /**
     * 保存用户的课表信息
     *
     */
    public static String  saveUserCourseTable(AppContext appContext,String str_jsonArray)
            throws  AppException{
        return saveJSONArray(appContext, URLs.SAVE_USER_COURSETABLE,str_jsonArray);
    }



    /**
     * 签到是需要的到老师对应的学生信息列表
     * @param appContext
     * @return
     * @throws AppException
     */
    public static StudentInfoInClassBeanList getStudentsByTname(AppContext appContext, String cid,String tname) throws AppException {

        String newUrl = URLs.TEACHER_STUDENT_INFO+"?cid="+cid;//+"&tname="+tname;

        try {

            System.out.println("获取学生信息列表：" + newUrl);
            return StudentInfoInClassBeanList.parse(StringUtils.toJSONArray(http_get(appContext,
                    newUrl)));

        } catch (Exception e) {

            System.out.println(e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }


    }

    /**
     * 获取某个回答对应的评论信息
     * @param appContext
     * @param ansid  答案的标示
     * @return
     * @throws AppException
     */
    public static CommentToSpecAnswerBeanList getCommentsByAnsid(AppContext appContext,int ansid)
            throws AppException{

        String newUrl = URLs.COMMENTS_LIST+ansid;

        try {

            System.out.println("获取答案列表：" + newUrl);
            return CommentToSpecAnswerBeanList.parse(StringUtils.toJSONArray(http_get(appContext,
                    newUrl)));
        } catch (Exception e) {

            System.out.println(e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }


    /**
     * 保存用户的评论信息
     * @param appContext
     * @param sid
     * @param ansid
     * @param content
     * @throws AppException
     */
    public static void saveUserComment(AppContext appContext,String sid,int ansid,String content)
            throws AppException{

        String parameter = "ansid="+ansid+"&sid="+sid+"&content="+content;
        String finalUrl = URLs.SAVECOMMENTS_LIST+parameter;
        //打印 调试
        Log.e("TTTT",finalUrl);
        try{
            http_get(appContext, finalUrl);
        }catch (Exception e){
            System.out.println(e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);

        }

    }


    /**
     * 保存用户基本信息
     * @throws AppException
     */
    public static String  saveUserInfo(AppContext appContext,String sid, String sname, String password, String phoneid, String role,
                                       String title, String schoolid) throws AppException{

        // 访问的地址
        String finalUrl = URLs.SAVEUSERINFO;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(finalUrl, cookie, userAgent);

                //添加参数
                httpPost.addParameter("sid",sid);
                httpPost.addParameter("sname",sname);
                httpPost.addParameter("password",password);
                httpPost.addParameter("phoneid",phoneid);
                httpPost.addParameter("role",role);
                httpPost.addParameter("title",title);
                httpPost.addParameter("schoolid",schoolid);

                int statusCode = httpClient.executeMethod(httpPost);
                //代表没有成功的返回
                if (statusCode != 200) {
                    throw AppException.http(statusCode);
                }

                responseBody = httpPost.getResponseBodyAsString();
                 break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                // e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;

    }


    /**
     * 保存用户提交的提问信息
     * @throws AppException
     */
    public static String  saveUserQuestion(AppContext appContext,String sid,String cid,
                                           String qcontent,String qtitle) throws AppException{

        // 访问的地址
        String finalUrl = URLs.SAVEQUESTION;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(finalUrl, cookie, userAgent);

                //添加参数
                httpPost.addParameter("sid",sid);
                httpPost.addParameter("cid",cid);
                httpPost.addParameter("qtitle",qtitle);
                httpPost.addParameter("qcontent",qcontent);
                int statusCode = httpClient.executeMethod(httpPost);
                //代表没有成功的返回
                if (statusCode != 200) {
                    throw AppException.http(statusCode);
                }

                responseBody = httpPost.getResponseBodyAsString();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                // e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;

    }


    /**
     * 保存用户提交的提问信息
     * @throws AppException
     */
    public static String  saveUserAnswer(AppContext appContext,String sid,int qid,
                                           String anscontent) throws AppException{

        // 访问的地址
        String finalUrl = URLs.SAVEANSWER;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(finalUrl, cookie, userAgent);

                //添加参数
                httpPost.addParameter("sid",sid);
                httpPost.addParameter("qid",String.valueOf(qid));
                httpPost.addParameter("anscontent", anscontent);
                int statusCode = httpClient.executeMethod(httpPost);

                //代表没有成功的返回
                if (statusCode != 200) {
                    throw AppException.http(statusCode);
                }

                responseBody = httpPost.getResponseBodyAsString();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                // e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }

    /**
     * 保存用户的点赞和点踩的信息
     * @param appContext
     * @param sid
     * @param ansid
     * @param isVolt
     * @throws AppException
     */
    public static void saveUserVoltOrVoltdown(AppContext appContext,String sid,int ansid,int isVolt )
            throws  AppException{
        String parameter = "ansid="+ansid+"&sid="+sid+"&isVolt="+isVolt;
        String finalUrl = URLs.SAVEVOLT+parameter;
        //打印 调试
        Log.e("TTTT",finalUrl);
        try{
            http_get(appContext, finalUrl);
        }catch (Exception e){
            System.out.println(e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }

    }



    /**
     * 通过qid查找改问题对应的信息
     * @param appContext
     * @throws AppException
     */
    public static String  getQuestionDetailByqid(AppContext appContext,int qid )
            throws  AppException{
        String parameter =""+qid;

        String finalUrl = URLs.GETQUESTION+parameter;
        //打印 调试
        Log.e("TTTT",finalUrl);
        try{
            return http_get(appContext, finalUrl);
        }catch (Exception e){
            System.out.println(e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }

    }







    /**
     *
     * @param url
     * @return
     */
    public static Bitmap getNetBitmap(String url) throws AppException {
        // System.out.println("image_url==> "+url);
        HttpClient httpClient = null;
        GetMethod httpGet = null;
        Bitmap bitmap = null;
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, null, null);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                InputStream inStream = httpGet.getResponseBodyAsStream();
                bitmap = BitmapFactory.decodeStream(inStream);
                inStream.close();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        return bitmap;
    }


    /**
     * 通过tname 和 cid 查找课程对应的 课堂公告
     * @param appContext
     * @param cid
     * @param tname
     * @return
     * @throws AppException
     */
    public static List<CourseboardBean> findCrsBrdsBycisTnm(AppContext appContext,String cid,String tname)
            throws AppException{

        // 访问的地址
        String url  = URLs.COURSEBOARD_LIST;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;

        //从服务器 获取到的结果
        String responseBody = "";
        String result = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(url, cookie, userAgent);

                //添加参数
                httpPost.addParameter("cid",cid);
                httpPost.addParameter("tname",tname);

                int statusCode = httpClient.executeMethod(httpPost);

                //代表没有成功的返回
                if (statusCode != 200) {
                    throw AppException.http(statusCode);
                }

                //获取到 从服务器端获取的Stirng
                responseBody = httpPost.getResponseBodyAsString();


//                Log.e("TTTT",responseBody);

                //解析结果
                JSONObject json = new JSONObject(responseBody);
                String code = json.getString("code");
                //如果服务器端 发过来的string是正确的
                if("200".equals(code)){
                    result = json.getString("result");

                    return CourseboardBeanList.parse(StringUtils.toJSONArray(result)).getList();

                }else{
                    throw new JSONException("json 解析出现问题");
                }

            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                // e.printStackTrace();
                throw AppException.network(e);
            }catch (JSONException e){

                e.printStackTrace();
                //出现 json解析错误
                throw AppException.network(e);

            }finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);



        return null;
    }


    /**
     * 保存用户提交的提问信息
     */
    public static String UserRegister(AppContext appContext,String accid) throws AppException {

        // 访问的地址
        String finalUrl = URLs.REGISTER;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",accid);


            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();

        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;

    }


    /**
     * 将用户保存到 对应的讨论组当中去
     */
    public static String AddUserToCrsTeam(AppContext appContext,String sid) throws AppException {

        // 访问的地址
        String finalUrl = URLs.ADDEDTOTEAM;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);


            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();

        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;

    }


    /**
     * 将用户收藏问答的信息 保存到服务器端去
     */
    public static String SaveUserClooectInfo(AppContext appContext,String sid,int ans_id) throws AppException {

        // 访问的地址
        String finalUrl = URLs.SAVEUSERCOLLECT;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);
            httpPost.addParameter("ansid",String.valueOf(ans_id));

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();

        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;

    }


    /**
     * 通过qid 返回对应的答案列表
     * @param appContext
     * @return
     * @throws AppException
     */
    public static String FindUserCollectInfoList(AppContext appContext,String sid )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.FINDUSERCOLLECT;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }


    /**
     * 通过qid 返回对应的答案列表
     * @param appContext
     * @return
     * @throws AppException
     */
    public static String FindAnswerDetail(AppContext appContext,int  ansid )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.FINDANSWERDETAIL;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("ansid",String.valueOf(ansid));

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }



    /**
     * 通过sid 返回 用户回答过的所有问题
     * @param appContext
     * @return
     * @throws AppException
     */
    public static String FindUserAnswered(AppContext appContext,String sid )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.FINDMYANSWED;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }


     /**
     * 查看用户对某个答案是否 点过赞 或者点过踩
     * @param appContext
     * @param sid
     * @param ansid
     * @return
     * @throws AppException
     */
    public static String FindIsAnswerVoltedOrVoltdown(AppContext appContext,String sid,int ansid )
            throws AppException {


        // 访问的地址
        String url = URLs.FINDISANSWERVOLTED;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(url, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);
            httpPost.addParameter("ansid",String.valueOf(ansid));

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }


    /**
     * 保存用户的 取消点赞 和 点踩信息
     *
     * @param appContext
     * @param sid
     * @param ansid
     * @return
     * @throws AppException
     */
    public static String saveUserCancelVoltOrVoltdown(AppContext appContext,String sid,int ansid,int isVolt )
            throws AppException {


        // 访问的地址
        String url = URLs.SAVECANCELVOLTORVOLTDOWN;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(url, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);
            httpPost.addParameter("ansid",String.valueOf(ansid));
            httpPost.addParameter("isVolt",String.valueOf(isVolt));

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }


    /**
     * 通过用户id 查找用户 发布过的所有问题
     * @param appContext
     * @param sid
     * @return
     * @throws AppException
     */
    public static String FindUserQuestioned(AppContext appContext,String sid )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.FINDMYQUESTIONS;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }


    /**
     * 根据sid 查找用户的一些基本信息  比如发布了多少问题 提出了多少问题
     * @param appContext
     * @param sid
     * @return
     * @throws AppException
     */
    public static String FindUserCntInfo(AppContext appContext,String sid )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.FINDUSERCNTINFO;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }


    /**
     *  保存用户的 评论答案信息
     * @param appContext
     * @param sid
     * @return
     * @throws AppException
     */
    public static String SaveUserCommentInfo(AppContext appContext,String commentContent,int ansid,String sid )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.SAVEUSERCOMMENT;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",sid);
            httpPost.addParameter("ansid",String.valueOf(ansid));
            httpPost.addParameter("commentContent",commentContent);

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }



    /**
     *  删除用户的 评论答案信息
     * @param appContext
     * @return
     * @throws AppException
     */
    public static String DeleteUserCommentInfo(AppContext appContext,int commentid )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.DELETEUSERCOMMENT;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("commentid",String .valueOf(commentid));

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }


    /**
     * 更新用户的 imgUrl
     * @param appContext
     * @param sid  用户id
     * @param imgUrl  用户imgUrl
     * @return
     * @throws AppException
     */
    public static String UploadUserHeadUrl(AppContext appContext,String  sid,String imgUrl )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.UPDATEUSERHEADURL;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("sid",(sid));
            httpPost.addParameter("imgurl",imgUrl);

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }


    /**
     * 通过 关键词来查找 question
     * @param appContext
     * @param sid
     * @return
     * @throws AppException
     */
    public static String SearchInQuestion(AppContext appContext,String cid ,String keyWord )
            throws AppException {


        // 访问的地址
        String finalUrl = URLs.SEARCHINQUESTION;

        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";

        try {
            httpClient = getHttpClient();
            httpPost = getHttpPost(finalUrl, cookie, userAgent);

            //添加参数
            httpPost.addParameter("cid",cid);
            httpPost.addParameter("key",keyWord);

            int statusCode = httpClient.executeMethod(httpPost);
            //代表没有成功的返回
            if (statusCode != 200) {
                throw AppException.http(statusCode);
            }

            responseBody = httpPost.getResponseBodyAsString();
        } catch (HttpException e) {

            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw AppException.http(e);
        } catch (IOException e) {

            // 发生网络异常
            e.printStackTrace();
            throw AppException.network(e);
        } finally {
            // 释放连接
            httpPost.releaseConnection();
            httpClient = null;
        }

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        return responseBody;
    }
}
