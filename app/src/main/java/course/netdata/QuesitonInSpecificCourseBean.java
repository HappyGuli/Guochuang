package course.netdata;

import java.io.Serializable;

/**
 * Created by happypaul on 16/2/18.
 */
public class QuesitonInSpecificCourseBean  implements Serializable{

    private String UserImgurl;
    private int zanNum;
    private String userName;
    private String questionTitle;
    private String questionContent;
    private int questionId;
    private String sid;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getUserImgurl() {
        return UserImgurl;
    }

    public void setUserImgurl(String userImgurl) {
        UserImgurl = userImgurl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getZanNum() {
        return zanNum;
    }

    public void setZanNum(int zanNum) {
        this.zanNum = zanNum;
    }
}
