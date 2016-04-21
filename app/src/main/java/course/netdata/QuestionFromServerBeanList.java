package course.netdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import widget.AppException;

/**
 * Created by happypaul on 16/1/29.
 */
public class QuestionFromServerBeanList extends Base{

    private int count;

    private List<QuestionFromServerBean> list = new ArrayList<QuestionFromServerBean>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<QuestionFromServerBean> getList() {
        return list;
    }

    public void setList(List<QuestionFromServerBean> list) {
        this.list = list;
    }


    public static QuestionFromServerBeanList parse(JSONArray obj) throws IOException,
            AppException, JSONException {
        QuestionFromServerBeanList questionlist = new QuestionFromServerBeanList();
        if (null != obj) {
            questionlist.count = obj.length();

            //将json对象转换成为bean对象
            for (int i = 0; i < obj.length(); i++) {
                JSONObject questionJson = obj.getJSONObject(i);
                QuestionFromServerBean question = new QuestionFromServerBean();
                question.setZanNum(Integer.valueOf(questionJson.getString("zanNum")));
                question.setTitle(questionJson.getString("questioTitle"));
                question.setContent(questionJson.getString("questionContent"));
                question.setQid(Integer.valueOf(questionJson.getString("qid")));

                questionlist.list.add(question);
            }
        }
        return questionlist;
    }



}
