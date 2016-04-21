package course.netdata;

import java.io.Serializable;

/**
 * Created by happypaul on 16/2/25.
 */
public class CommentToSpecAnswerBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private String userImgUrl;
    private String content;
    private String date;
    private String userName;

    public String getUserImgUrl() {
        return userImgUrl;
    }
    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }


}
