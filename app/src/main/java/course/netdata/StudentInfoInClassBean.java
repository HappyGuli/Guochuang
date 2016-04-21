package course.netdata;

import java.io.Serializable;

/**
 * Created by happypaul on 16/2/25.
 */
public class StudentInfoInClassBean implements Serializable{

    private static final long serialVersionUID = 1L;


    private String sname ;
    private String sid;
    private String phoneid;
    private boolean checked;

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean isChecked) {
        this.checked = isChecked;
    }

    public String getPhoneid() {
        return phoneid;
    }

    public void setPhoneid(String phoneid) {
        this.phoneid = phoneid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}
