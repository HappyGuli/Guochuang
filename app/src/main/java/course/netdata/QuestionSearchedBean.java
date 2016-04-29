package course.netdata;

public class QuestionSearchedBean extends Base{
	
	private String userName;
	private String questioTitle;
	private String questionContent;
	private String sid;
	private int qid;
	private long zanNum;
	private long ansCnt;
	
	
	
	public long getAnsCnt() {
		return ansCnt;
	}
	public void setAnsCnt(long ansCnt) {
		this.ansCnt = ansCnt;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getQuestioTitle() {
		return questioTitle;
	}
	public void setQuestioTitle(String questioTitle) {
		this.questioTitle = questioTitle;
	}
	public String getQuestionContent() {
		return questionContent;
	}
	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public int getQid() {
		return qid;
	}
	public void setQid(int qid) {
		this.qid = qid;
	}
	public long getZanNum() {
		return zanNum;
	}
	public void setZanNum(long zanNum) {
		this.zanNum = zanNum;
	}
	
	
	

}
