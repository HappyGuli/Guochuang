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
public class StudentInfoInClassBeanList extends Base{

    private int count;

    private List< StudentInfoInClassBean> list = new ArrayList<StudentInfoInClassBean>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<StudentInfoInClassBean> getList() {
        return list;
    }

    public void setList(List<StudentInfoInClassBean> list) {
        this.list = list;
    }


    public static StudentInfoInClassBeanList parse(JSONArray obj) throws IOException,
            AppException, JSONException {
        StudentInfoInClassBeanList studentlist = new StudentInfoInClassBeanList();
        if (null != obj) {
            studentlist.count = obj.length();

            //将json对象转换成为bean对象
            for (int i = 0; i < obj.length(); i++) {
                JSONObject studentsJson = obj.getJSONObject(i);

                StudentInfoInClassBean student = new StudentInfoInClassBean();
                student.setSid(studentsJson.getString("sid"));
                student.setSname(studentsJson.getString("sname"));
                student.setPhoneid(studentsJson.getString("phoneid"));
                student.setChecked(false);
                studentlist.list.add(student);
            }
        }
        return studentlist;
    }



}
