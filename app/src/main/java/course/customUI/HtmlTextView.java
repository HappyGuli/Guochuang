package course.customUI;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;

import course.util.HtmlTagHandler;
import course.util.LinkMovementMethodExt;
import course.util.LocalImageGetter;
import course.util.UrlImageGetter;

/**
 * 当前类注释: TextView 实现图文混排
 * 项目名：FastDevTest
 * 包名：com.jwenfeng.fastdev.view.htmltextview
 * 作者：jinwenfeng on 16/1/27 11:15
 * 邮箱：823546371@qq.com
 * QQ： 823546371
 * 公司：南京穆尊信息科技有限公司
 * © 2016 jinwenfeng
 * ©版权所有，未经允许不得传播
 */
public class HtmlTextView extends TextView {


    public HtmlTextView(Context context) {
        super(context);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     *
     * @param is
     * @return
     */
    static private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";
    }


    /**
     * Parses String containing HTML to Android's Spannable format and displays
     * it in this TextView.
     *
     * @param html String containing HTML, for example: "<b>Hello world!</b>"
     */
    public void setHtmlFromString(String html, boolean useLocalDrawables) {
        Html.ImageGetter imgGetter;
        if (useLocalDrawables) {
            imgGetter = new LocalImageGetter(getContext());
        } else {
            imgGetter = new UrlImageGetter(this, getContext());
        }
        // this uses Android's Html class for basic parsing, and HtmlTagHandler
        setText(Html.fromHtml(html, imgGetter, new HtmlTagHandler()));

        // make links work
        //setMovementMethod(LinkMovementMethodExt.getInstance());


        // make links and image work
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (what == 200) {
                    LinkMovementMethodExt.MessageSpan ms = (LinkMovementMethodExt.MessageSpan) msg.obj;
                    Object[] spans = (Object[]) ms.getObj();
                    for (Object span : spans) {
                        if (span instanceof ImageSpan) {
                            Log.d("TTTT", "点击了图片" + ((ImageSpan) span).getSource());
                            //处理自己的逻辑
                        }
                    }
                }
            }
        };

        setMovementMethod(LinkMovementMethodExt.getInstance(handler, ImageSpan.class));




        // no flickering when clicking textview for Android < 4, but overriders
        // color...
        //text.setTextColor(getResources().getColor(android.R.color.secondary_text_dark_nodisable));
    }

}
