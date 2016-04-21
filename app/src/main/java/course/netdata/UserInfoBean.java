package course.netdata;


/**
 * 用来保存用户的 一些基本信息的
 * @author happypaul
 *
 */
public class UserInfoBean {

	private String sid;
	private String userName;
	private String imgUrl;
	private String uerLevel;
	private long attentionCnt;
	private long collectCnt;
	private long questionedCnt;
	private long abilityVlu;
	private long answerCnt;
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUerLevel() {
		return uerLevel;
	}
	public void setUerLevel(String uerLevel) {
		this.uerLevel = uerLevel;
	}
	public long getAttentionCnt() {
		return attentionCnt;
	}
	public void setAttentionCnt(long attentionCnt) {
		this.attentionCnt = attentionCnt;
	}
	public long getCollectCnt() {
		return collectCnt;
	}
	public void setCollectCnt(long collectCnt) {
		this.collectCnt = collectCnt;
	}
	public long getQuestionedCnt() {
		return questionedCnt;
	}
	public void setQuestionedCnt(long questionedCnt) {
		this.questionedCnt = questionedCnt;
	}
	public long getAbilityVlu() {
		return abilityVlu;
	}
	public void setAbilityVlu(long abilityVlu) {
		this.abilityVlu = abilityVlu;
	}
	public long getAnswerCnt() {
		return answerCnt;
	}
	public void setAnswerCnt(long answerCnt) {
		this.answerCnt = answerCnt;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
