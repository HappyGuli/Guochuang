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
public class MyQuestionBeanList extends Base{


    private int count;

    private List<MyQuestionBean> list = new ArrayList<MyQuestionBean>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<MyQuestionBean> getList() {
        return list;
    }

    public void setList(List<MyQuestionBean> list) {
        this.list = list;
    }

    public static MyQuestionBeanList parse(JSONArray obj) throws IOException,
            AppException, JSONException {

        MyQuestionBeanList list = new MyQuestionBeanList();

        if (null != obj) {
            list.count = obj.length();

            //将json对象转换成为bean对象
            for (int i = 0; i < obj.length(); i++) {
                JSONObject json = obj.getJSONObject(i);
                MyQuestionBean bean = new MyQuestionBean();

                bean.setQid(json.getInt("qid"));
                bean.setQuestionTitle(json.getString("questionTitle"));
                bean.setQuestionContent(json.getString("questionContent"));
                bean.setDate(json.getString("date"));

                list.list.add(bean);
            }
        }
        return list;
    }


}
