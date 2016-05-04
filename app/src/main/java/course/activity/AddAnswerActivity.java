package course.activity;

import api.URLs;
import course.customUI.PhotoEdittext;
import course.util.UploadUtil;
import hello.login.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.ApiClient;
import common.UIHelper;
import widget.AppContext;
import widget.AppException;

/**
 * Created by happypaul on 16/1/24.
 */
public class AddAnswerActivity extends ActionBarActivity implements UploadUtil.OnUploadProcessListener{


    private Toolbar mToolbar;

    //获取intent中的数据
    private Intent intent;
    private int qid;


    //通过sharedPreference来获取公共数据
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String self_sid;




    //button
    private ImageView iv_add_image;
    private ImageView iv_add_answer;
    private PhotoEdittext et_add_answer;


    //选择图片的时候需要
    private Uri photoUri;
    private String picPath;
    private Cursor cursor =null ;   // 防止cursor泄漏， 在finish()之前关闭

    private List<String> list_text; //在edittext中的text  包括了图片的地址
    private List<String> list_result; //将list_text中的图片的地址全部转换为在服务器上的地址
    private String answer_content;
    private ProgressDialog progressDialog;


    /**
     * 去上传文件
     */
    protected static final int SELECT_PIC_BY_PICK_PHOTO = 99;

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




    private AppContext appContext;



    //handler 的使用
    public android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 1) {

                progressDialog.dismiss();
                UIHelper.ToastMessage(AddAnswerActivity.this, "添加回答成功");
                //结束自己这个界面
                finish();

                //跳转到具体课程的界面
                Intent intent = new Intent(AddAnswerActivity.this, SpecificQuestionWithAnsActivity.class);
                intent.putExtra("questionid", qid);
                AddAnswerActivity.this.startActivity(intent);

            } else if (msg.what == 0) {
                UIHelper.ToastMessage(AddAnswerActivity.this, "服务器出错");
            }
        }
    };





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_answer);

        // //设置toolbar
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("添加回答");

        appContext = new AppContext(getApplication());

        iv_add_image= (ImageView)mToolbar.findViewById(R.id.btn_add_picture);
        iv_add_answer= (ImageView)mToolbar.findViewById(R.id.btn_add_answer);




        InitData();
        InitUI();

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


    private void InitData(){

        //从sharedPreference 中获取自己的学号信息
        sharedPreferences = getSharedPreferences("info",MODE_WORLD_READABLE);
        self_sid = sharedPreferences.getString("str_sid","20134942");
        intent = getIntent();
        if(intent!=null){

            Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
            qid = bundle.getInt("qid", 100);

        }else{
            //给出提示
            Toast.makeText(AddAnswerActivity.this, "intent is null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化 控件
     */
    private void InitUI(){

        et_add_answer = (PhotoEdittext)findViewById(R.id.et_answer_detail);
        //设置监听事件
        iv_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 选择 是通过拍照 还是 通过从相册中挑选照片
                final CharSequence[] items = {"拍照", "相册",};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAnswerActivity.this);
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




        //设置回答按钮 点击事件
        iv_add_answer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                answer_content = et_add_answer.getText().toString();

                if (answer_content == null || answer_content.equals("")) {

                    Toast.makeText(AddAnswerActivity.this, "请输入答案内容", Toast.LENGTH_SHORT).show();

                } else {
                    //先禁用按钮 防止重复条件申请
                    iv_add_answer.setEnabled(false);

                    Log.e("TTTT", et_add_answer.getmContentList().toString());

                    list_text = et_add_answer.getmContentList();

                    //提示handler可以上传图片了
                    handler.sendEmptyMessage(TO_UPLOAD_FILE);

                }


            }
        });

        //初始化 progressDialog
        progressDialog = new ProgressDialog(this);
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
     * 获取到照片地址之后 处理事务
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
        {
            doPhoto(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 在 activity中Destroy 的时候  关闭cursor 防止内存泄漏
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


        String[] pojo = {MediaStore.Images.Media.DATA};
        cursor = managedQuery(photoUri, pojo, null, null,null);

        if(cursor != null )
        {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);

            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            //cursor.close();
        }

        Log.e("TTTT", "imagePath = " + picPath);


        if(picPath != null && ( picPath.endsWith(".png") || picPath.endsWith(".PNG") ||picPath.endsWith(".jpg") ||picPath.endsWith(".JPG")  ))
        {
            //插入到 edittext中
            et_add_answer.insertBitmap(picPath);

        }else{
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 处理上传文件之后的逻辑处理
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
                    toUploadFile();
                    break;
                case UPLOAD_INIT_PROCESS:
                    break;
                case UPLOAD_IN_PROCESS:
                    break;
                case UPLOAD_FILE_DONE:

                    progressDialog.setMessage("正在上传文字...");

                    String result = msg.obj.toString();

                    switch (msg.arg1){
                        case UPLOAD_SUCCESS_CODE:
                            //提示上传成功
                            //Toast.makeText(AddAnswerActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            //的到转换过的服务器地址 和 文字
                            list_result = (List) msg.obj;
                            answer_content = list2String(list_result);

                            //现将文字上传到服务器中
                            new Thread() {
                                public void run() {
                                    Message msg = new Message();
                                    try {

                                        ApiClient.saveUserAnswer(appContext,self_sid,qid,answer_content);
                                        msg.what=1;

                                    } catch (AppException e){
                                        msg.what=0;
                                    }
                                    mHandler.sendMessage(msg);
                                }
                            }.start();

                            //输出 测试
                            Log.e("TTTT","  str_result = "+answer_content);
                            break;
                        case UPLOAD_FILE_NOT_EXISTS_CODE:
                            //提示文件不存在错误
                            Toast.makeText(AddAnswerActivity.this,"文件不存在",Toast.LENGTH_SHORT).show();
                            break;
                        case UPLOAD_SERVER_ERROR_CODE:
                            //提示服务器存在错误
                            Toast.makeText(AddAnswerActivity.this,"服务器出错",Toast.LENGTH_SHORT).show();
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
        uploadUtil.upLoadFiles(list_text,fileKey, URLs.UPLOADANSWERPIC,params,false);
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
        handler.sendMessage(msg);
    }

    @Override
    public void onUploadProcess(int uploadSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_IN_PROCESS;
        msg.arg1 = uploadSize;
        handler.sendMessage(msg);
    }


    @Override
    public void initUpload(int fileSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_INIT_PROCESS;
        msg.arg1 = fileSize;
        handler.sendMessage(msg);
    }


    /**
     *  工具函数 将 List<String> 转换为String
     * @param input
     * @return
     */
    private String list2String(List<String> input){

        StringBuffer sb = new StringBuffer();

        //遍历 List<String>
        for(int i=0; i<input.size(); i++){
            //将list中的string 一个一个append到String当中去
            sb.append(input.get(i));
        }

        //返回这个String
        return sb.toString();
    }


}



