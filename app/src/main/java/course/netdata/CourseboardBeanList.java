package course.netdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import widget.AppException;

/**
 * Created by happypaul on 16/2/25.
 */
public class CourseboardBeanList extends Base{

    private int count;

    private List<CourseboardBean> list = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CourseboardBean> getList() {
        return list;
    }

    public void setList(List<CourseboardBean> list) {
        this.list = list;
    }

    public static CourseboardBeanList parse(JSONArray obj) throws IOException,
            AppException, JSONException {
        CourseboardBeanList crsbrds = new CourseboardBeanList();

        if (null != obj) {
            crsbrds.count = obj.length();

            //将json对象转换成为bean对象
            for (int i = 0; i < obj.length(); i++) {
                JSONObject json = obj.getJSONObject(i);
                CourseboardBean courseboard = new CourseboardBean();

                courseboard.setContent(json.getString("content"));
                courseboard.setDate(json.getString("date"));
                courseboard.setTname(json.getString("tname"));
                courseboard.setTitle(json.getString("title"));
                crsbrds.list.add(courseboard);
            }
        }
        return crsbrds;
    }

}
