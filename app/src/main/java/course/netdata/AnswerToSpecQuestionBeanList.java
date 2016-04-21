package course.netdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import widget.AppException;

/**
 * Created by happypaul on 16/2/24.
 */
public class AnswerToSpecQuestionBeanList extends Base{


    private int count;

    private List<AnswerToSpecQuestionBean> list = new ArrayList<AnswerToSpecQuestionBean>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<AnswerToSpecQuestionBean> getList() {
        return list;
    }

    public void setList(List<AnswerToSpecQuestionBean> list) {
        this.list = list;
    }

    public static AnswerToSpecQuestionBeanList parse(JSONArray obj) throws IOException,
            AppException, JSONException {

        AnswerToSpecQuestionBeanList answerlist = new AnswerToSpecQuestionBeanList();

        if (null != obj) {
            answerlist.count = obj.length();

            //将json对象转换成为bean对象
            for (int i = 0; i < obj.length(); i++) {
                JSONObject answerJson = obj.getJSONObject(i);
                AnswerToSpecQuestionBean answer = new AnswerToSpecQuestionBean();

                answer.setZanNum(Integer.valueOf(answerJson.getString("zanNum")));
                answer.setAnswerContent(answerJson.getString("answerContent"));
                answer.setQid(answerJson.getInt("qid"));
                answer.setSid(answerJson.getString("sid"));
                answer.setUserName(answerJson.getString("userName"));
                answer.setUserImgUrl(answerJson.getString("userImgUrl"));
                answer.setAnsid(Integer.valueOf(answerJson.getString("ansid")));
                answer.setCaiNum(Integer.valueOf(answerJson.getString("caiNum")));

                answerlist.list.add(answer);
            }
        }
        return answerlist;
    }


}
