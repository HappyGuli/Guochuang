package course.util;

/**
 * 当前类注释:HtmlEditText的bean
 * 项目名：FastDevTest
 * 包名：com.jwenfeng.fastdev.view.htmltextview
 * 作者：jinwenfeng on 16/1/27 10:55
 * 邮箱：823546371@qq.com
 * QQ： 823546371
 * 公司：南京穆尊信息科技有限公司
 * © 2016 jinwenfeng
 * ©版权所有，未经允许不得传播
 */
public class HtmlFile {

    private String localPath;
    private String urlPath;

    public HtmlFile() {
    }

    public HtmlFile(String localPath, String urlPath) {
        this.localPath = localPath;
        this.urlPath = urlPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    public String toString() {
        return "{" +
                "localPath='" + localPath + '\'' +
                ", urlPath='" + urlPath + '\'' +
                '}';
    }
}
