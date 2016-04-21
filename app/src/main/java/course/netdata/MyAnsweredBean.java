package course.netdata;


/**
 * @author happypaul
 *
 */
public class MyAnsweredBean {
	private String questionTitle;
	private int qid;
	private String ansContent;
	private int ansid;
	private String answerImgUrl;
	private int zanNum;
	public String getQuestionTitle() {
		return questionTitle;
	}
	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}
	public int getQid() {
		return qid;
	}
	public void setQid(int qid) {
		this.qid = qid;
	}
	public String getAnsContent() {
		return ansContent;
	}
	public void setAnsContent(String ansContent) {
		this.ansContent = ansContent;
	}
	public int getAnsid() {
		return ansid;
	}
	public void setAnsid(int ansid) {
		this.ansid = ansid;
	}
	public String getAnswerImgUrl() {
		return answerImgUrl;
	}
	public void setAnswerImgUrl(String answerImgUrl) {
		this.answerImgUrl = answerImgUrl;
	}
	public int getZanNum() {
		return zanNum;
	}
	public void setZanNum(int zanNum) {
		this.zanNum = zanNum;
	}

}
