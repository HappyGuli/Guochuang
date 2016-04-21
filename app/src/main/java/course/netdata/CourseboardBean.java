package course.netdata;

/**
 * 代表 课堂中 老师发布的 课堂公告
 * @author happypaul
 *
 */
public class CourseboardBean {
	
	private String content;
	private String title;
	private String date;
	private String tname;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}

}
