package course.netdata;

/**
 * Created by happypaul on 16/4/2.
 */
public class AnswerDetailBean {

    private String questionTitle;
    private String answerContent;
    private String userName;
    private int zanNum;
    private int caiNum;

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public int getCaiNum() {
        return caiNum;
    }

    public void setCaiNum(int caiNum) {
        this.caiNum = caiNum;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
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
