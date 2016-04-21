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
public class CollectInfoBeanList extends Base{


    private int count;

    private List<CollectInfoBean> list = new ArrayList<CollectInfoBean>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CollectInfoBean> getList() {
        return list;
    }

    public void setList(List<CollectInfoBean> list) {
        this.list = list;
    }

    public static CollectInfoBeanList parse(JSONArray obj) throws IOException,
            AppException, JSONException {

        CollectInfoBeanList list = new CollectInfoBeanList();

        if (null != obj) {
            list.count = obj.length();

            //将json对象转换成为bean对象
            for (int i = 0; i < obj.length(); i++) {
                JSONObject json = obj.getJSONObject(i);
                CollectInfoBean collect = new CollectInfoBean();

                collect.setZanNum(Integer.valueOf(json.getString("zanNum")));
                collect.setQid(json.getInt("qid"));
                collect.setAnsid(Integer.valueOf(json.getString("ansid")));
                collect.setAnswerImgUrl(json.getString("answerImgUrl"));
                collect.setQuestionTitle(json.getString("questionTitle"));
                collect.setAnsContent(json.getString("ansContent"));
                list.list.add(collect);
            }
        }
        return list;
    }


}
