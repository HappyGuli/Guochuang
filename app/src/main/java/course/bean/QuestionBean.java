package course.bean;

/**
 * Created by happypaul on 16/1/21.
 */
public class QuestionBean {

    private int viewTimes;
    private String userName;
    private String questionTitle;
    private String questionContent;
    private int questionId;
    private int userImage;
    private int zanNumberes;

    public int getZanNumberes() {
        return zanNumberes;
    }

    public void setZanNumberes(int zanNumberes) {
        this.zanNumberes = zanNumberes;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getViewTimes() {
        return viewTimes;
    }

    public void setViewTimes(int viewTimes) {
        this.viewTimes = viewTimes;
    }
}
