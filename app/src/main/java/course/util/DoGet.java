package course.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import bean.Course;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class DoGet {

    static List<String> cookies=null;




    // 封装下面两个函数
    public static void getCourseTable(String stuNum,String stuPsw){
        try {
            System.out.println(getCookies(stuNum, stuPsw));
            System.out.println(getCourseTableHtml());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * 判断用户名和密码是否正确
     * 用户名密码正确 返回 true
     * @param str_username
     * @param str_psw
     * @return
     */
    public  static boolean isUserNameAndPsdRight(String str_username,String str_psw) throws Exception {

        String str_result = null;


        str_result =  getCookies(str_username, str_psw);

        if(str_result!=null){

            if(str_result.contains("账号或密码不正确！")){
                return false;
            }else{
                return true;
            }

        }else{
            return false;
        }

    }





    /**
     * Get Cookie 直接模拟登陆 获取到身份证
     * @return
     * @throws Exception 这里直接将异常抛出 到调用这个方法的地方进行处理
     */
    public static String getCookies(String stuNum,String stuPsw) throws Exception {

        URL localURL = new URL("http://202.202.1.176:8080/_data/index_login.aspx");
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
        //代表从网页里面获取输入
        httpURLConnection.setDoInput(true);
        //代表给网页里面输入数据
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        //得到输出流 给网页输出数据
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
        //因为该网站 在请求参数里面添加了对 学号和密码进行了md5码加密 
        String tem =MD5.getDiff(stuNum, stuPsw);
        out.write("&Sel_Type=STU&efdfdfuuyyuuckjg="+tem+"&txt_dsdsdsdjkjkjc="+stuNum+"&txt_dsdfdfgfouyy="+stuPsw);
        out.flush();
        out.close();


        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        if (httpURLConnection.getResponseCode() >= 300) {
            //如果请求出现异常
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }

        cookies= new ArrayList<String>();
        cookies = connection.getHeaderFields().get("Set-Cookie");

        try {
            inputStream = httpURLConnection.getInputStream();

            //这个gbk非常重要 花了快两个小时找出这个错误 gbk就是本文件的编码
            inputStreamReader = new InputStreamReader(inputStream,"GBK");
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine+"\n");
            }

        } finally {

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        //将获取到的html 转换成为string
        return resultBuffer.toString();
    }



    //得到course的集合
    public static List<Course> getCourseDaoFromTables(List<List<String>> tables){
        List<Course> courses = new ArrayList<Course>();
        List<String> table1,table2,table3;
        table1=tables.get(1);
        if(tables.size()>4){
            table2=tables.get(5);
        }
        else{
            table2=tables.get(3);
        }

        table3=tables.get(0);


        //专门用来获取 姓名的
        String nameString = table3.get(0);
        int startPosition = nameString.lastIndexOf("：");
        int endPosition = nameString.lastIndexOf("讲");
        System.out.println(startPosition+"  "+endPosition);
        String name = nameString.substring(startPosition+1, endPosition-2);
        System.out.println("name :"+name);

        //讲name以一个course实例 给传到客户端去
        Course nameCourse= new Course();
        nameCourse.setCourseNumber("test");
        nameCourse.setCourseName("test");
        nameCourse.setCourseCredit("test");
        nameCourse.setCourseTeacher(name);
        nameCourse.setCourseWeeks("test");
        nameCourse.setCourseTime("test");

        //保存姓名实体
        courses.add(nameCourse);

        for(int i=2;i<table1.size();i++){
            String a = table1.get(i);
            String[] ss= a.split("#");

            Course cs= new Course();
            cs.setCourseNumber(ss[1].split("]")[0].substring(1,ss[1].split("]")[0].length()-1));
            cs.setCourseName(ss[1].split("]")[1]);
            cs.setCourseCredit(ss[2]);
            cs.setCourseTeacher(ss[9]);
            cs.setCourseWeeks(ss[10]);
            cs.setCourseTime(ss[11]);
            if(ss.length<13){
                //有的课程 比如课程设计的 教室未定
                cs.setCourseRoom("暂未定");
            }else{
                cs.setCourseRoom(ss[12]);

            }
            courses.add(cs);

            System.out.println(cs.toString());

            cs=null;

        }


        for(int i=2;i<table2.size();i++){
            String a = table2.get(i);
            String[] ss= a.split("#");

            System.out.println(ss[10]);

            Course cs= new Course();
            cs.setCourseNumber(ss[1].split("]")[0].substring(1,ss[1].split("]")[0].length()-1));
            cs.setCourseName(ss[1].split("]")[1]);
            cs.setCourseCredit(ss[2]);
            cs.setCourseTeacher(ss[7]);
            cs.setCourseWeeks(ss[9]);
            cs.setCourseTime(ss[10]);
            cs.setCourseRoom(ss[11]);
            courses.add(cs);

            System.out.println(cs.toString());


            cs=null;
        }


        return courses;
    }


    /**
     * 解析网页
     */
    public static List<List<String>>   getCourseTableFromHtml(String a){
        //用来保存 所有的tables信息
        List<List<String>> list_tables = new ArrayList<List<String>>();
        Document doc = (Document) Jsoup.parseBodyFragment(a);
        Element body = ((org.jsoup.nodes.Document) doc).body();



        Elements tables = body.getElementsByTag("table");
        for (Element table : tables) {
            Elements trs = table.getElementsByTag("tr");
            //用来保存 某一个table里面的信息
            List<String> list_table = new ArrayList<String>();
            for (Element tr : trs) {
                StringBuffer bf=new StringBuffer();
                Elements tds = tr.getElementsByTag("td");
                for (Element td : tds) {
                    //添加#号 为了后面的好构建 course entity
                    if(td.text().equals("")){
                        //
                        bf.append(td.attr("hidevalue")+"#");
                        System.out.print(td.attr("hidevalue")+"#");

                    }else{
                        bf.append(td.text()+"#");
                        System.out.print(td.text()+"#");
                    }
                }
                //添加到list里面
                list_table.add(bf.toString());
                System.out.println("");
            }
            //将某一个table添加进去
            list_tables.add(list_table);
            System.out.println("");
        }

        return list_tables;
    }



    /**
     * 获取到身份证后，进入到课表页面 将里面的html数据爬取出来
     * @return
     */
    public static String getCourseTableHtml(){

        InputStream inputStream = null;
        //
        String URL_PATH="http://202.202.1.176:8080/znpk/Pri_StuSel_rpt.aspx";
        try {
            URL url = new URL(URL_PATH);
            if(url != null){
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setRequestMethod("GET");

                    //根据之前获得的cookie获取到正确的cookie参数
                    String Cookie="";
                    for(String cookie: cookies){
                        Cookie=Cookie+cookie.split(";",2)[0]+";";
                    }

                    //设置cookie
                    httpURLConnection.addRequestProperty("Cookie",Cookie);
                    httpURLConnection.setDoOutput(true);
                    OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream(), "utf-8");
                    //写入的参数在这里
                    out.write("px=1&rad=on&Sel_XNXQ=20151");
                    out.flush();
                    out.close();

                    // 得到连接的 结果码                       
                    int responsecode = httpURLConnection.getResponseCode();

                    //如果连接成功
                    if(responsecode == HttpURLConnection.HTTP_OK){
                        //将获取到的数据获取到
                        inputStream = httpURLConnection.getInputStream();
                        //这里的编码 非常的重要 花了快两个小时 找出这个错误
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"gbk");
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        StringBuffer resultBuffer = new StringBuffer();
                        String tempLine;
                        while ((tempLine = reader.readLine()) != null) {
                            resultBuffer.append(tempLine+"\n");
                        }
                        return resultBuffer.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }


}

