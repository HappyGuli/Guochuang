package course.netdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import widget.AppException;

/**
 * Created by happypaul on 16/2/18.
 */
public class QuesitonInSpecificCourseBeanList extends Base {

    private int count;

    private List<QuesitonInSpecificCourseBean> list = new ArrayList<QuesitonInSpecificCourseBean>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<QuesitonInSpecificCourseBean> getList() {
        return list;
    }

    public void setList(List<QuesitonInSpecificCourseBean> list) {
        this.list = list;
    }

    public static QuesitonInSpecificCourseBeanList parse(JSONArray obj) throws IOException,
            AppException, JSONException {
        QuesitonInSpecificCourseBeanList questionlist = new QuesitonInSpecificCourseBeanList();

        if (null != obj) {
            questionlist.count = obj.length();

            //将json对象转换成为bean对象
            for (int i = 0; i < obj.length(); i++) {
                JSONObject questionJson = obj.getJSONObject(i);

                QuesitonInSpecificCourseBean question = new QuesitonInSpecificCourseBean();

                question.setZanNum(Integer.valueOf(questionJson.getString("zanNum")));
                question.setQuestionTitle(questionJson.getString("questioTitle"));
                question.setQuestionContent(questionJson.getString("questionContent"));
                question.setQuestionId(Integer.valueOf(questionJson.getString("qid")));
                question.setUserName(questionJson.getString("userName"));
                question.setUserImgurl(questionJson.getString("userImgUrl"));
                question.setSid(questionJson.getString("sid"));

                questionlist.list.add(question);
            }
        }
        return questionlist;
    }


}
