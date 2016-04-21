package hello.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.cqu.DemoCache;
import org.cqu.preference.Preferences;
import org.cqu.preference.UserPreferences;

import main.activity.MainActivity;
import widget.AppContext;
import widget.AppException;


/**
 * Created by happypaul on 16/3/19.
 */
public class LoginActivity  extends Activity {


    private static final String TAG = "CourseLoginActivity";

    private EditText account_et;
    private EditText pass_et;
    private Button btn_login;


    private String account;
    private String accid;
    private String name;
    private String token;
    private String sb_result;


    private AbortableFuture<LoginInfo> loginRequest;

    private AppContext appContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_in);

        appContext = new AppContext(getApplication());

//        DemoCache.setContext(getApplicationContext());

        initUI();
    }


    /*************************处理线程完成之后的事情***********************/

    android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                // 登录
                loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(accid, token));
                loginRequest.setCallback(new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        LogUtil.i(TAG, "login success");

                        onLoginDone();
                        DemoCache.setAccount(accid);
                        saveLoginInfo(accid, token);


                        Log.e("TTTT", accid);
                        Log.e("TTTT",token);


                        // 初始化消息提醒
                        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

                        // 初始化免打扰
                        NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                        // 构建缓存
                        DataCacheManager.buildDataCacheAsync();

                        // 进入主界面

                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();



                        // 进入主界面
                        MainActivity.start(LoginActivity.this, null);
                        finish();



//                        //跳转到主界面去
//                        Intent intent = new Intent();
//                        intent.setClass(CourseLoginActivity.this, CourseMainActivity.class);   //描述起点和目标
//                        startActivity(intent);

                    }

                    @Override
                    public void onFailed(int code) {

                        if (code == 302 || code == 404) {
                            Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        onLoginDone();
                    }
                });


            } else  if(msg.what == -1){

                Toast.makeText(LoginActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
            }
        }
    };







    /*************************初始化界面**********************************/
    private void initUI(){
        account_et = (EditText) this.findViewById(R.id.username_edit);
        pass_et = (EditText) this.findViewById(R.id.password_edit);
        btn_login = (Button) this.findViewById(R.id.signin_button);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login();

            }
        });


    }



    /**
     * ***************************************** 登录 **************************************
     */

    private void login() {


        // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
        // 在这里直接使用同步到云信服务器的帐号和token登录。
        // 这里为了简便起见，demo就直接使用了密码的md5作为token。
        // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
        account = account_et.getText().toString();
        //final String name = "b911f63c252a900cbf3ec2bfe78f44bc";//pass_et.getText().toString();
        name = pass_et.getText().toString();


        new Thread(new Runnable() {
            @Override
            public void run() {

                //获取token成功
                Message msg = new Message();
                try{
                    sb_result = AppContext.getRegisterToken(appContext,account);

                }catch (AppException e){
                    e.printStackTrace();
                    msg.what = -1;
                    mHandler.sendMessage(msg);
                }

                String[] results = sb_result.split("_");

                token = results[0];
                name = results[1];
                accid = results[2];



                if(token!=null) {
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }else{
                    msg.what = -1;
                    mHandler.sendMessage(msg);

                }

            }
        }).start();

    }


    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }


    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }


}
