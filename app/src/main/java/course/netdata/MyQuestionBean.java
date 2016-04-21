package course.netdata;


/**
 * 用来保存用户的 我的提问 的item
 * @author happypaul
 *
 */
public class MyQuestionBean {

	private String questionTitle;
	private String questionContent;
	private String date; //提问的时间
	private int qid; //问题的id  在点击的时候需要用到


	public String getQuestionTitle() {
		return questionTitle;
	}
	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}
	public String getQuestionContent() {
		return questionContent;
	}
	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getQid() {
		return qid;
	}
	public void setQid(int qid) {
		this.qid = qid;
	}

}
