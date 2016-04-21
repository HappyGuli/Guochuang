package course.bean;

/**
 * Created by happypaul on 16/3/12.
 *
 * 在上传用户的课表的时候使用的
 *
 *
 */
public class CourseJsonBean {

    private String  sid;
    private String courseName;
    private String cid;
    private String tname;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }
}
