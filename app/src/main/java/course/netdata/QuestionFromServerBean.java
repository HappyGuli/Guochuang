package course.netdata;

import java.io.Serializable;

/**
 * Created by happypaul on 16/1/28.
 */
public class QuestionFromServerBean implements Serializable{

    private String title;
    private String content;
    private int qid;
    private int zanNum;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getZanNum() {
        return zanNum;
    }

    public void setZanNum(int zanNum) {
        this.zanNum = zanNum;
    }
}
