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
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import api.URLs;
import hello.login.R;



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


        //防止下面的错误
        //fail reason:UIL doesn't support scheme(protocol) by default [/Experiment/useranswerpics/69373807_1461169361096.jpg]
        source = URLs.URL_API_HOST+source;

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(c);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);





        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.ic_launcher)   // 在下载过程中的图片
                .showImageForEmptyUri(R.drawable.about_logo)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.about_logo)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                //.cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                //.considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                //.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
                //设置图片加入缓存前，对bitmap进行设置
                //.preProcessor(BitmapProcessor preProcessor)
                //.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                //.displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .build();//构建完成

/**/


        //异步加载图片
        /*ImageLoader.getInstance().loadImage( source ,new SimpleImageLoadingListener() {

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


        ImageLoader.getInstance().loadImage(source, options,new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String s, View view) {

//                Log.e("TTTT","onLoadingStarted");
//
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = false;
//
//                Bitmap loadedImage  = BitmapFactory.decodeResource(c.getResources(), R.mipmap.ic_launcher, options);
//                //= new Bitmap(R.mipmap.ic_launcher);
//
//                // 计算缩放比例
//                float scaleWidth = ((float) width) / loadedImage.getWidth();
//                // 取得想要缩放的matrix参数
//                Matrix matrix = new Matrix();
//                matrix.postScale(scaleWidth, scaleWidth);
//                loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix,
//                        true);
//                urlDrawable.bitmap = loadedImage;
//                urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
//

                container.invalidate();
                container.setText(container.getText()); // 解决图文重叠

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {


                Log.e("TTTT", "onLoadingFailed");

                Log.e("TTTT", "fail reason:"+ failReason.getCause().getMessage());



//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = false;

                Bitmap loadedImage  = BitmapFactory.decodeResource(c.getResources(), R.drawable.about_logo);
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

            @Override
            public void onLoadingComplete(String s, View view, Bitmap loadedImage) {

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

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

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
