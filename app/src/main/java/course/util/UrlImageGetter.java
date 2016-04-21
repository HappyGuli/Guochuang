package course.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;

import hello.login.R;


/**
 * 当前类注释: UrlImageGetter 加载网络图片
 * 项目名：FastDevTest
 * 包名：com.jwenfeng.fastdev.view.htmltextview
 * 作者：jinwenfeng on 16/1/27 11:19
 * 邮箱：823546371@qq.com
 * QQ： 823546371
 * 公司：南京穆尊信息科技有限公司
 * © 2016 jinwenfeng
 * ©版权所有，未经允许不得传播
 */
public class UrlImageGetter implements Html.ImageGetter {

    Context c;
    TextView container;
    int width ;

    /**
     *
     * @param t
     * @param c
     */
    public UrlImageGetter(TextView t, Context c) {
        this.c = c;
        this.container = t;
        width = c.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public Drawable getDrawable(String source) {
        final UrlDrawable urlDrawable = new UrlDrawable();


        //输出测试
        Log.e("TTTT", "source" + source);

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(c);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);


/*
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.ic_launcher)   // 在下载过程中的图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                //.showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                //.cacheInMemory(true)//设置下载的图片是否缓存在内存中
                //.cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
               // .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
               // .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
                //设置图片加入缓存前，对bitmap进行设置
                //.preProcessor(BitmapProcessor preProcessor)
                //.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .build();//构建完成

*/


        //异步加载图片
       /* ImageLoader.getInstance().loadImage(c, source ,new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(Bitmap loadedImage) {
                super.onLoadingComplete(loadedImage);


                Log.e("TTTT", "onLoadingComplete");

                // 计算缩放比例
                float scaleWidth = ((float) width) / loadedImage.getWidth();
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix,
                        true);
                urlDrawable.bitmap = loadedImage;
                urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());


                container.invalidate();
                container.setText(container.getText()); // 解决图文重叠


            }


            //设置 加载失败的图片
            @Override
            public void onLoadingFailed(FailReason failReason) {
                super.onLoadingFailed(failReason);

                Log.e("TTTT", "onLoadingFailed");


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;

                Bitmap loadedImage  = BitmapFactory.decodeResource(c.getResources(), R.mipmap.ic_launcher, options);
                //= new Bitmap(R.mipmap.ic_launcher);

                // 计算缩放比例
                float scaleWidth = ((float) width) / loadedImage.getWidth();
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix,
                        true);
                urlDrawable.bitmap = loadedImage;
                urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());


                container.invalidate();
                container.setText(container.getText()); // 解决图文重叠

            }


            //设置 加载中的图片
            @Override
            public void onLoadingStarted() {
                super.onLoadingStarted();

                Log.e("TTTT","onLoadingStarted");

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;

                Bitmap loadedImage  = BitmapFactory.decodeResource(c.getResources(), R.mipmap.ic_launcher, options);
                //= new Bitmap(R.mipmap.ic_launcher);

                // 计算缩放比例
                float scaleWidth = ((float) width) / loadedImage.getWidth();
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix,
                        true);
                urlDrawable.bitmap = loadedImage;
                urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());


                container.invalidate();
                container.setText(container.getText()); // 解决图文重叠
            }





        });*/

        return urlDrawable;
    }

    @SuppressWarnings("deprecation")
    public class UrlDrawable extends BitmapDrawable {
        protected Bitmap bitmap;
        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }




    }
}
