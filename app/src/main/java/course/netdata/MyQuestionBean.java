package course.netdata;


/**
 * ���������û��� �ҵ����� ��item
 * @author happypaul
 *
 */
public class MyQuestionBean {
	
	private String questionTitle;  
	private String questionContent;
	private String date; //���ʵ�ʱ��
	private int qid; //�����id  �ڵ����ʱ����Ҫ�õ�
	
	
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
