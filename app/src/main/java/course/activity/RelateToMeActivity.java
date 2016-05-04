package course.activity;
import hello.login.R;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import course.bean.RelateToMeCommentsBean;
import course.bean.RelateToMeInvitedBean;
import course.bean.RelateToMeThankedBean;

/**
 * Created by happypaul on 16/1/24.
 */
public class RelateToMeActivity extends ActionBarActivity {


    //和 viewPager相关
    private ViewPager viewPager;//页卡内容
    private ImageView imageView;// 动画图片
    private ImageView imageView1,imageView2,imageView3;
    private List<View> views;// Tab页面列表
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;         // 动画图片宽度

    private View view1,view2,view3;  //各个页卡
    private List<ImageView> imageViews;// 保存代表每个view的textview


    //和recyclerView相关的东西
    private List<RelateToMeCommentsBean> commentsDatas;
    private List<RelateToMeThankedBean> thankedDatas;
    private List<RelateToMeInvitedBean> invitedDatas;

    private RecyclerView recyclerViewThanked;
    private RecyclerView recyclerViewComment;
    private RecyclerView recyclerViewInvited;
    private Toolbar mToolbar;


    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.relate_to_me);

        //设置toolbar
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("通知");



        InitViewPager();
        InitImageView();

        initData();
        //初始化recycler之类的东西
        initListAdapterV();


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

    public void initData(){

        commentsDatas = new ArrayList<RelateToMeCommentsBean>();
        invitedDatas = new ArrayList<RelateToMeInvitedBean>();
        thankedDatas = new ArrayList<RelateToMeThankedBean>();

        for(int i=0;i<1;i++){
            RelateToMeCommentsBean temC = new RelateToMeCommentsBean();
            RelateToMeThankedBean  temT = new RelateToMeThankedBean();
            RelateToMeInvitedBean temI = new RelateToMeInvitedBean();

            //获取当前时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            Date currDate = new Date(System.currentTimeMillis());
            String curD = simpleDateFormat.format(currDate);

            temC.setUserName("刘凯");
            temC.setDate(curD);
            temC.setAnswerId(i);
            temC.setUserIcon(R.mipmap.usericon1);


            temT.setUserName("张建国");
            temT.setDate(curD);
            temT.setAnswerId(i);
            temT.setUserIcon(R.mipmap.usericon1);


            temI.setUserName("谭阳军");
            temI.setDate(curD);
            temI.setQuestionId(i);
            temI.setUserIcon(R.mipmap.usericon1);

            commentsDatas.add(temC);
            thankedDatas.add(temT);
            invitedDatas.add(temI);


        }


    }

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    //和recyclerView相关的东西


    private void initListAdapterV() {

        Log.e("TTTT", "initListAdapter");

        //设置layoutManager
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(RelateToMeActivity.this));
        //设置adapter
        MyQuestionRecyclerViewAdapter adapter = new MyQuestionRecyclerViewAdapter(this, commentsDatas);
        recyclerViewComment.setAdapter(adapter);


        //设置layoutManager
        recyclerViewThanked.setLayoutManager(new LinearLayoutManager(RelateToMeActivity.this));
        recyclerViewThanked.setNestedScrollingEnabled(false);
        recyclerViewThanked.setHasFixedSize(false);

        //设置adapter
        MyQuestionRecyclerViewAdapterT adapterT = new MyQuestionRecyclerViewAdapterT(this, thankedDatas);
        recyclerViewThanked.setAdapter(adapterT);


        //设置layoutManager
        recyclerViewInvited.setLayoutManager(new LinearLayoutManager(RelateToMeActivity.this));
        recyclerViewInvited.setNestedScrollingEnabled(false);
        recyclerViewInvited.setHasFixedSize(false);

        //设置adapter
        MyQuestionRecyclerViewAdapterI adapterI = new MyQuestionRecyclerViewAdapterI(this, invitedDatas);
        recyclerViewInvited.setAdapter(adapterI);



    }


    private class MyQuestionRecyclerViewAdapter extends RecyclerView.Adapter<MyQuestionHolder> {

        private Context context;
        private List<RelateToMeCommentsBean> datas;

        //构造函数
        public MyQuestionRecyclerViewAdapter(Context context, List<RelateToMeCommentsBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
        }

        //决定根布局
        @Override
        public MyQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.item_relate_to_me,null);
            return new MyQuestionHolder(view);

        }

        //决定根数据
        @Override
        public void onBindViewHolder(MyQuestionHolder holder, int position) {
            holder.SetDataAndRefreshUI(datas.get(position));
        }

        @Override
        public int getItemCount() {

            return datas.size();
        }
    }

    //
    private class MyQuestionHolder extends RecyclerView.ViewHolder{

        private ImageView userImage;
        private TextView date;
        private TextView content;

        public MyQuestionHolder(View itemView) {
            super(itemView);
            //初始化孩子对象
            userImage = (ImageView) itemView.findViewById(R.id.iv_user_icon);
            date = (TextView) itemView.findViewById(R.id.tv_relate_to_me_date);
            content = (TextView) itemView.findViewById(R.id.tv_relate_to_me_content);

        }

        public void SetDataAndRefreshUI(RelateToMeCommentsBean bean){
            userImage.setImageResource(bean.getUserIcon());
            date.setText(bean.getDate());
            content.setText(bean.getUserName()+"评论了你的回答");
        }


    }


    private class MyQuestionRecyclerViewAdapterT extends RecyclerView.Adapter<MyQuestionHolderT> {

        private Context context;
        private List<RelateToMeThankedBean> datas;

        //构造函数
        public MyQuestionRecyclerViewAdapterT(Context context, List<RelateToMeThankedBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
        }

        //决定根布局
        @Override
        public MyQuestionHolderT onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.item_relate_to_me,null);
            return new MyQuestionHolderT(view);

        }

        //决定根数据
        @Override
        public void onBindViewHolder(MyQuestionHolderT holder, int position) {
            holder.SetDataAndRefreshUI(datas.get(position));
        }

        @Override
        public int getItemCount() {

            return datas.size();
        }
    }

    //
    private class MyQuestionHolderT extends RecyclerView.ViewHolder{

        private ImageView userImage;
        private TextView date;
        private TextView content;

        public MyQuestionHolderT(View itemView) {
            super(itemView);
            //初始化孩子对象
            userImage = (ImageView) itemView.findViewById(R.id.iv_user_icon);
            date = (TextView) itemView.findViewById(R.id.tv_relate_to_me_date);
            content = (TextView) itemView.findViewById(R.id.tv_relate_to_me_content);

        }

        public void SetDataAndRefreshUI(RelateToMeThankedBean bean){
            userImage.setImageResource(bean.getUserIcon());
            date.setText(bean.getDate());
            content.setText(bean.getUserName()+"赞了你的回答");
        }


    }


    private class MyQuestionRecyclerViewAdapterI extends RecyclerView.Adapter<MyQuestionHolderI> {

        private Context context;
        private List<RelateToMeInvitedBean> datas;

        //构造函数
        public MyQuestionRecyclerViewAdapterI(Context context, List<RelateToMeInvitedBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
        }

        //决定根布局
        @Override
        public MyQuestionHolderI onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.item_relate_to_me,null);
            return new MyQuestionHolderI(view);

        }

        //决定根数据
        @Override
        public void onBindViewHolder(MyQuestionHolderI holder, int position) {
            holder.SetDataAndRefreshUI(datas.get(position));
        }

        @Override
        public int getItemCount() {

            return datas.size();
        }
    }

    //
    private class MyQuestionHolderI extends RecyclerView.ViewHolder{

        private ImageView userImage;
        private TextView date;
        private TextView content;

        public MyQuestionHolderI(View itemView) {
            super(itemView);
            //初始化孩子对象
            userImage = (ImageView) itemView.findViewById(R.id.iv_user_icon);
            date = (TextView) itemView.findViewById(R.id.tv_relate_to_me_date);
            content = (TextView) itemView.findViewById(R.id.tv_relate_to_me_content);

        }

        public void SetDataAndRefreshUI(RelateToMeInvitedBean bean){
            userImage.setImageResource(bean.getUserIcon());
            date.setText(bean.getDate());
            content.setText(bean.getUserName()+"邀请你去回答问题");
        }


    }
    //和recyclerView相关的东西结束
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /**
     * 和 ViewPager相关的东西
     */
    private void InitViewPager() {
        viewPager=(ViewPager) findViewById(R.id.relate_to_me_view_pager);
        views=new ArrayList<View>();
        LayoutInflater inflater=getLayoutInflater();
        view1=inflater.inflate(R.layout.view_hold_relate_to_me_thanked, null);
        view2=inflater.inflate(R.layout.view_hold_relate_to_me_comments, null);
        view3=inflater.inflate(R.layout.view_hold_relate_to_me_invited, null);

        recyclerViewComment = (RecyclerView) view2.findViewById(R.id.recycler_relate_to_me_comments);
        recyclerViewThanked = (RecyclerView) view1.findViewById(R.id.recycler_relate_to_me_thanked);
        recyclerViewInvited = (RecyclerView) view3.findViewById(R.id.recycler_relate_to_me_invited);

        views.add(view1);
        views.add(view2);
        views.add(view3);

        InitTextView();

        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     *  初始化头标
     */

    private void InitTextView() {
        imageView1 = (ImageView) findViewById(R.id.iv_relate_thanked);
        imageView2 = (ImageView) findViewById(R.id.iv_relate_comments);
        imageView3 = (ImageView) findViewById(R.id.iv_relate_invited);

        imageViews = new ArrayList<ImageView>(); //初始化
        imageViews.add(imageView1);
        imageViews.add(imageView2);
        imageViews.add(imageView3);

        imageView1.setOnClickListener(new MyOnClickListener(0));
        imageView2.setOnClickListener(new MyOnClickListener(1));
        imageView3.setOnClickListener(new MyOnClickListener(2));
    }


    /**
     *  初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据
     */

    private void InitImageView() {
        imageView= (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.add_answer_cursor).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = screenW/3;// 计算偏移量

        //打印数据输出
        Log.e("TTTT","screenW = "+String.valueOf(offset)+" screenW ="+String.valueOf(screenW));

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset/2-bmpW/2, 0);
        imageView.setImageMatrix(matrix);// 设置动画初始位置

    }


    /**
     *
     * 头标点击监听 3 */
    private class MyOnClickListener implements View.OnClickListener {
        private int index=0;
        public MyOnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }

    }


    /**
     * ViewPagerDaapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)   {
            container.removeView(mListViews.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return  mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
    }

    //设计动画
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int arg0) {


        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {


        }

        public void onPageSelected(int arg0) {


            //打印数据输出
            Log.e("TTTT","one*currIndex = "+String.valueOf(bmpW*currIndex/3)+" one*arg0 ="+String.valueOf(bmpW*arg0/3));

            //Toast.makeText(RelateToMeActivity.this,"page changed",Toast.LENGTH_SHORT).show();
            Animation animation = new TranslateAnimation(360*currIndex, 360*arg0, 0, 0);//显然这个比较简洁，只有一行代码。
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);
        }

    }

    //ViewPager相关的东西  结束
    //////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////

}
