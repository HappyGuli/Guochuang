package course.bean;


public class Course {
	private String courseNumber;
	private String courseName;
    private String courseCredit;
    private String courseTeacher;
    private String courseWeeks;
    private String courseTime;
    private String courseRoom;
    public String getCourseNumber() {
		return courseNumber;
	}
	public void setCourseNumber(String courseNumber) {
		this.courseNumber = courseNumber;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getCourseCredit() {
		return courseCredit;
	}
	public void setCourseCredit(String courseCredit) {
		this.courseCredit = courseCredit;
	}
	public String getCourseTeacher() {
		return courseTeacher;
	}
	public void setCourseTeacher(String courseTeacher) {
		this.courseTeacher = courseTeacher;
	}
	public String getCourseWeeks() {
		return courseWeeks;
	}
	public void setCourseWeeks(String courseWeeks) {
		this.courseWeeks = courseWeeks;
	}
	public String getCourseTime() {
		return courseTime;
	}
	public void setCourseTime(String courseTime) {
		this.courseTime = courseTime;
	}
	public String getCourseRoom() {
		return courseRoom;
	}
	public void setCourseRoom(String courseRoom) {
		this.courseRoom = courseRoom;
	}
	@Override
	public String toString() {
		return "Course [courseNumber=" + courseNumber + ", courseName=" + courseName + ", courseCredit=" + courseCredit
				+ ", courseTeacher=" + courseTeacher + ", courseWeeks=" + courseWeeks + ", courseTime=" + courseTime
				+ ", courseRoom=" + courseRoom + "]";
	}
	
	
    
    
}
