package course.netdata;

import java.io.Serializable;


/**
 * 某个答案对应的评论
 */
public class CommentToSpecAnswerBean implements Serializable{

        private static final long serialVersionUID = 1L;

        private String userImgUrl;
        private String content;
        private String date;
        private String userName;
        private int commentid;



        public int getCommentid() {
            return commentid;
        }
        public void setCommentid(int commentid) {
            this.commentid = commentid;
        }
        public static long getSerialversionuid() {
            return serialVersionUID;
        }
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
