package course.activity;

import api.ApiClient;
import api.URLs;
import common.BitmapManager;
import common.StringUtils;
import common.UIHelper;
import course.netdata.CollectInfoBean;
import course.netdata.UserInfoBean;
import course.util.FileUtil;
import course.util.UploadUtil;
import  hello.login.R;
import widget.AppContext;
import widget.AppException;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by happypaul on 16/1/27.
 */
public class SettingActivity extends ActionBarActivity implements UploadUtil.OnUploadProcessListener{


    private Toolbar mToolbar;


    //展示 一些数量
    private TextView  tv_attention_cnt, tv_collect_cnt,tv_answer_cnt,tv_question_cnt;
    //展示用户一些基本信息
    private TextView  tv_user_name, tv_user_id,tv_user_level,tv_ability_vlu;

    //用户图片
    private ImageView iv_user_icon ;



    //可以点击的
    private TextView tv_attentio,tv_collect;

    //ReletiviLayout
    private RelativeLayout rl_user_ability,rl_user_level,rl_user_quetsion,rl_user_answer;
    private TextView  tv_about_us,tv_about_software;


    //要上传相片的uri
    private Uri photoUri;
    private String picPath;
    private Cursor cursor;   // 防止cursor泄漏， 在finish()之前关闭
    private ProgressDialog progressDialog ;
    private List<String> list_files ;  //用来保存需要上传到服务器的图片的地址
    private List<String> list_result_imgurl; // 用来保存图片在服务器上的地址

    /**
     * 去上传文件
     */
    protected static final int SELECT_PIC_BY_PICK_PHOTO = 99;

    /**
     * 去上传文件
     */
    protected static final int SELECTED_PHOTO_ZOOM = 77;


    /**
     * 去上传文件
     */
    protected static final int SELECT_PIC_BY_TACK_PHOTO = 88;
    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_FILE = 1;
    /**
     * 上传文件响应
     */
    protected static final int UPLOAD_FILE_DONE = 2;  //
    /**
     * 上传初始化
     */
    private static final int UPLOAD_INIT_PROCESS = 4;
    /**
     * 上传中
     */
    private static final int UPLOAD_IN_PROCESS = 5;
    /***
     * 上传成功
     */
    public static final int UPLOAD_SUCCESS_CODE = 11;
    /**
     * 文件不存在
     */
    public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 22;
    /**
     * 服务器出错
     */
    public static final int UPLOAD_SERVER_ERROR_CODE = 33;





    //获取用户的 账户信息
    private SharedPreferences sharedPreferences;
    private String self_sid;
    private AppContext appContext;

    //保存用户的基本信息
    private UserInfoBean bean ;

    //bitmapManager 用来加载用户头像的
    private BitmapManager bitmapManager;

    // 从服务器中 获取到用户的基本信息 然后展示出来
    private Handler mHandler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                // 获取到从服务器中获取到的数据
                bean = (UserInfoBean) msg.obj;

                //获取到用户的 ImgUrl
                String   imgURL = bean.getImgUrl();

                Log.e("TTTT", imgURL);


                // 如果用户更改过图像 那么就设置为用户更改后的图像
                if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {

                    iv_user_icon.setImageResource(R.mipmap.usericon1);
                } else {

                    if (!imgURL.contains("http")) {
                        imgURL = URLs.HTTP + URLs.HOST + "/" + imgURL;
                    }
                    //从服务器获取图片 并且展示在iv_user_icon 上面
                    bitmapManager.loadBitmap(imgURL,iv_user_icon);
                }

                // 将从服务器中获取的数据给展示出来
                showDataFromServer();

            } else if(msg.what == 2){

                // 将 progressDialog 消失
                progressDialog.dismiss();
                //  更新用户的头像成功
                Toast.makeText(SettingActivity.this, "更新用户头像成功", Toast.LENGTH_SHORT).show();


            }else  if(msg.what == -1){
                Toast.makeText(SettingActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * 处理上传文件之后的逻辑处理
     */
    private Handler headerhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOAD_FILE_DONE:

                    progressDialog.setMessage("正在上传文字...");

                    //list中保存的 是图片在服务器中的地址
                    list_result_imgurl = (List)msg.obj;

                    switch (msg.arg1){
                        case UPLOAD_SUCCESS_CODE:

                            //现将文字上传到服务器中
                            new Thread() {
                                public void run() {
                                    Message msg = new Message();
                                    try {
                                        // 调用 AppContext中的方法进行更新
                                        AppContext.UpdateUserHeadUrl(appContext, self_sid, list_result_imgurl.get(0));
                                        msg.what=2;

                                    } catch (AppException e){
                                        msg.what=-1;
                                    }
                                    mHandler.sendMessage(msg);
                                }
                            }.start();


                            break;
                        case UPLOAD_FILE_NOT_EXISTS_CODE:
                            //提示文件不存在错误
                            Toast.makeText(SettingActivity.this,"文件不存在",Toast.LENGTH_SHORT).show();
                            break;
                        case UPLOAD_SERVER_ERROR_CODE:
                            //提示服务器存在错误
                            Toast.makeText(SettingActivity.this,"服务器出错",Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }


                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting);

        //设置toolbar
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设置/个人信息");


        //初始化appContext
        appContext = new AppContext(getApplication());

        //从sharedPreference中获取 用户的账户信息
        getSidFromSharedPreference();

        //从服务器中获取数据
        getDateFromServer();

        //初始化 控件
        InitUI();
    }


    /**
     * activity 生命周期结束的时候 进行调用的函数 在结束的时候 销毁cursor
     * 防止内存泄漏
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //在关闭之前 将cursor关闭 防止泄漏
        //如果cursor 不为空 那么关闭cursor
        if(cursor!=null){
            cursor.close();
        }
    }

    /**
     * 从服务器中获取数据
     */
    public void  getDateFromServer(){

        new  Thread(new Runnable() {
            @Override
            public void run() {

                Message  msg = new Message();
                try{

                    // 从服务器中 获取用户基本信息的 bean
                    UserInfoBean bean = AppContext.FindUserCntInfo(appContext,self_sid);
                    msg.what =1;
                    msg.obj = bean;

                }catch (Exception e){

                    //打印出 错误详细信息
                    e.printStackTrace();
                    msg.what = -1;

                }
                //发送信息 给 UI
                mHandler.sendMessage(msg);

            }
        }).start();

    }


    /**
     * 查找一些控件
     */
    private void  InitUI(){


        //初始化 progressDialog
        progressDialog = new ProgressDialog(SettingActivity.this);


        //展示用户的图片
        iv_user_icon = (ImageView) this.findViewById(R.id.iv_user_icon);


        //展示 一些数量
        tv_attention_cnt = (TextView)this.findViewById(R.id.tv_my_attention_cnt);
        tv_collect_cnt = (TextView)this.findViewById(R.id.tv_my_collect_cnt);
        tv_answer_cnt = (TextView)this.findViewById(R.id.tv_my_answer_cnt);
        tv_question_cnt = (TextView)this.findViewById(R.id.tv_my_question_cnt);


        //展示用户一些基本信息
        tv_user_name = (TextView)this.findViewById(R.id.tv_user_name);
        tv_user_id = (TextView)this.findViewById(R.id.tv_user_id);
        tv_user_level = (TextView)this.findViewById(R.id.tv_user_level);
        tv_ability_vlu = (TextView)this.findViewById(R.id.tv_user_ability_value);



        //需要设置点击事件的
        tv_attentio = (TextView)this.findViewById(R.id.tv_my_attention);
        tv_collect = (TextView)this.findViewById(R.id.tv_my_collect);


        rl_user_ability = (RelativeLayout)this.findViewById(R.id.rl_user_my_ability);
        rl_user_quetsion = (RelativeLayout)this.findViewById(R.id.rl_user_my_question);
        rl_user_answer = (RelativeLayout)this.findViewById(R.id.rl_my_user_answer);
        rl_user_level = (RelativeLayout)this.findViewById(R.id.rl_user_my_level);






        // 关于我们 关于软件
        tv_about_software = (TextView)this.findViewById(R.id.tv_about_soft_ware);
        tv_about_us = (TextView)this.findViewById(R.id.tv_about_us);

        tv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到与我相关的界面
                Intent i = new Intent(SettingActivity.this, MyCollectedActivity.class);
                SettingActivity.this.startActivity(i);

            }
        });

        rl_user_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(SettingActivity.this, MyAnswerActivity.class);
                SettingActivity.this.startActivity(ii);

            }
        });

        rl_user_quetsion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到与我相关的界面
                Intent iii = new Intent(SettingActivity.this, MyQuestionActivity.class);
                SettingActivity.this.startActivity(iii);

            }
        });



        //给图像设置点击图片 用户可以设置自己的图像
        iv_user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 选择 是通过拍照 还是 通过从相册中挑选照片
                final CharSequence[] items = {"拍照", "相册",};
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("选择图片");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //如果选择的是拍照
                        if (item == 0) {
                            takePhoto();

                        } else if (item == 1) {
                            //如果选择的是从相册中选择
                            pickPhoto();

                        }

                    }
                });
                AlertDialog alert = builder.create(); /**/
                alert.show();

            }
        });


        sharedPreferences = getSharedPreferences("info", MODE_WORLD_READABLE);
        /*******从sharedPreferences中获得 用户的头像在手机中的位置信息********/
        String tem_pic = sharedPreferences.getString("picpath","0000");
        if(tem_pic!="0000"){
            //设置用户头像
            Bitmap bm = BitmapFactory.decodeFile(tem_pic);
            iv_user_icon.setImageBitmap(bm);
        }


        //初始化 用来加载用户头像的 bitmapManager
        bitmapManager = new BitmapManager();

    }


    /**
     * 将从服务器上获取到的数据给展示出来
     */
    private  void  showDataFromServer(){

        //展示 一些数量
        tv_attention_cnt.setText(String.valueOf(bean.getAttentionCnt()));
        tv_collect_cnt.setText(String.valueOf(bean.getCollectCnt()));
        tv_answer_cnt.setText(String.valueOf(bean.getAnswerCnt()));
        tv_question_cnt.setText(String.valueOf(bean.getQuestionedCnt()));

        //展示用户一些基本信息
        tv_user_name.setText(String.valueOf(bean.getUserName()));
        tv_user_id.setText("学号：" + String.valueOf(bean.getSid()));
        tv_user_level.setText(String.valueOf(bean.getUerLevel()));
        tv_ability_vlu.setText(String.valueOf(bean.getAbilityVlu()));

    }


    /******从sharedPreference中获取 用户的账户信息*****/
    public  void getSidFromSharedPreference(){
        sharedPreferences = getSharedPreferences("info", MODE_WORLD_READABLE);
        /*******从sharedPreferences中获得 用户的学号信息********/
        self_sid = sharedPreferences.getString("str_sid","20134942");

    }


    /******从sharedPreference中获取 用户的账户信息*****/
    public  void saveUserImgPathToSharedPreference(String picPath){
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("picpath", picPath);
        //提交数据
        editor.commit();

    }






    //设置 actionBar中的返回键的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }


    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED))
        {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Log.e("imagePath", photoUri.toString());

            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            /**-----------------*/
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }else{
            Toast.makeText(this,"内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }




     /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        /*
        * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
        * yourself_sdk_path/docs/reference/android/content/Intent.html
        * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
        * 是直接调本地库的，小马不懂C C++ 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
        * 制做的了...吼吼
        */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, SELECTED_PHOTO_ZOOM);

     }


     /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    private void setPicToView(Intent picdata) {

        Bundle extras = picdata.getExtras();
        if (extras != null) {

            Bitmap photo = extras.getParcelable("data");

            //将图片保存在 安卓手机当中，并且但会在手机中的地址
            picPath = FileUtil.saveFile(SettingActivity.this, "temphead.jpg", photo);

            //输出测试
            Log.e("TTTT", "picPath" + picPath);


            // 保存到sharedPreference  用户头像在手机中的位子
            saveUserImgPathToSharedPreference(picPath);


            //设置用户头像为用户自己选择的头像
            iv_user_icon.setImageBitmap(photo);

            //
            list_files = new ArrayList<String>();

            // 将裁剪后的图片的地址 给 UploadUtil ，然后上传到服务器中
            list_files.add(UploadUtil.mBitmapTag_pre+picPath);

            // 上传图片给服务器
            toUploadFile();
        }
    }




    /**
     * 处理从其他activity中的到结果之后的事务处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case SELECT_PIC_BY_PICK_PHOTO:
                doPhoto(requestCode,data);

                break;
            // 如果是调用相机拍照时
            case SELECT_PIC_BY_TACK_PHOTO:
                doPhoto(requestCode,data);
                break;
            // 取得裁剪后的图片
            case SELECTED_PHOTO_ZOOM:
                /**
                 * 非空判断大家一定要验证，如果不验证的话，
                 * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                 * 当前功能时，会报NullException，小马只
                 * 在这个地方加下，大家可以根据不同情况在合适的
                 * 地方做判断处理类似情况
                 *
                 */
                if(data != null){
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    /**
     * 选择图片后，获取图片的路径  将获得的图片展示到 Edittext中
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode,Intent data)
    {
        if(requestCode == SELECT_PIC_BY_PICK_PHOTO )  //从相册取图片，有些手机有异常情况，请注意
        {
            if(data == null)
            {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri = data.getData();
            if(photoUri == null )
            {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
        }

        startPhotoZoom(photoUri);

    }



    /**
     * 上传文件
     */
    private void toUploadFile()
    {
        progressDialog.setMessage("正在上传图片...");
        progressDialog.show();
        String fileKey = "pic";
        UploadUtil uploadUtil = UploadUtil.getInstance();;
        uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态

        Map<String, String> params = new HashMap<String, String>();
        params.put("cqu", "18000");

        // 开始上传文件
        uploadUtil.upLoadFiles(list_files, fileKey, URLs.UPLOADHEADPIC, params,true);
    }



    /**
     * 上传服务器响应回调
     */
    @Override
    public void onUploadDone(int responseCode, List<String> message) {
        //progressDialog.dismiss();
        Message msg = Message.obtain();
        msg.what = UPLOAD_FILE_DONE;
        msg.arg1 = responseCode;
        msg.obj = message;
        headerhandler.sendMessage(msg);
    }

    @Override
    public void onUploadProcess(int uploadSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_IN_PROCESS;
        msg.arg1 = uploadSize;
        headerhandler.sendMessage(msg);
    }


    @Override
    public void initUpload(int fileSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_INIT_PROCESS;
        msg.arg1 = fileSize;
        headerhandler.sendMessage(msg);
    }

}
