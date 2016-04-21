package course.bean;

/**
 * Created by happypaul on 16/1/20.
 * 用来保存同学帮当中recyclerView 的item 的
 *
 *
 */
public class HelpRecyDataBean {

    private int courseIcon;
    private String courseTitle;
    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getCourseIcon() {
        return courseIcon;
    }

    public void setCourseIcon(int courseIcon) {
        this.courseIcon = courseIcon;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
}
