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
public class CommentToSpecAnswerBeanList extends Base{

    private int count;

    private List<CommentToSpecAnswerBean> list = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CommentToSpecAnswerBean> getList() {
        return list;
    }

    public void setList(List<CommentToSpecAnswerBean> list) {
        this.list = list;
    }

    public static CommentToSpecAnswerBeanList parse(JSONArray obj) throws IOException,
            AppException, JSONException {
        CommentToSpecAnswerBeanList commentlist = new CommentToSpecAnswerBeanList();

        if (null != obj) {
            commentlist.count = obj.length();

            //将json对象转换成为bean对象
            for (int i = 0; i < obj.length(); i++) {
                JSONObject commentJson = obj.getJSONObject(i);
                CommentToSpecAnswerBean comment = new CommentToSpecAnswerBean();

                comment.setContent(commentJson.getString("content"));
                comment.setUserImgUrl(commentJson.getString("userImgUrl"));
                comment.setDate(commentJson.getString("date"));
                comment.setUserName(commentJson.getString("userName"));

                commentlist.list.add(comment);
            }
        }
        return commentlist;
    }

}
