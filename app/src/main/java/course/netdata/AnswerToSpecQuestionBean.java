package course.netdata;

import java.io.Serializable;

/**
 * Created by happypaul on 16/2/24.
 */
public class AnswerToSpecQuestionBean implements Serializable{

    private int zanNum;
    private int caiNum;
    private String userImgUrl;
    private String userName;
    private String answerContent;
    private int qid;
    private int ansid;
    private String sid;

    public int getCaiNum() {
        return caiNum;
    }

    public void setCaiNum(int caiNum) {
        this.caiNum = caiNum;
    }

    public int getAnsid() {
        return ansid;
    }

    public void setAnsid(int ansid) {
        this.ansid = ansid;
    }

    public int getZanNum() {
        return zanNum;
    }
    public void setZanNum(int zanNum) {
        this.zanNum = zanNum;
    }
    public String getUserImgUrl() {
        return userImgUrl;
    }
    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getAnswerContent() {
        return answerContent;
    }
    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }
    public int getQid() {
        return qid;
    }
    public void setQid(int qid) {
        this.qid = qid;
    }
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }


}
