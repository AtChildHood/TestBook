package com.example.hanjun.testbook;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MyAdapter.MySimpleAdapter;
import MyAdapter.MyViewPageAdapter;
import MyView.MyRelativeLayout;
import MyView.RoundImageView;

public class CommunityActivity extends AppCompatActivity {

    String message;
    EditText editText;
    TextView text;
    Button send;
    ScrollView scrollView;
    ImageView add;
    ImageView picture1;
    ImageView picture2;
    ImageView picture3;
    ImageView picture4;
    DatabaseHelper databaseHelper;
    String pwd;
    int oldHeight,NewHeight;
    ViewPager emotionPager;


    @android.support.annotation.IdRes int id1 = 123;
    @android.support.annotation.IdRes int id2 = 124;
    int ID=0;
    MessageBroadcastReceiver myReceiver;
    boolean first=true;
    String user;
    LinearLayout root;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("share", MODE_PRIVATE);
        user = sharedPreferences.getString("user", "");

        if(user.equals("")){
            Intent intent=new Intent(CommunityActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_community);
        getInputKeyboardHeight();
        Init();
        loadMessage();

    }

    public void getInputKeyboardHeight(){
        root = (LinearLayout) this.findViewById(R.id.root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                root.getWindowVisibleDisplayFrame(r);
                //r.top 是状态栏高度
                int screenHeight = root.getRootView().getHeight();
                int softHeight = screenHeight - (r.bottom - r.top);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputMethodManager.isActive()) {
                    editor.putInt("InputMethodOpen", softHeight);
                }else{
                    editor.putInt("InputMethodClose", softHeight);
                }

                editor.commit();
                Log.e("Keyboard Size", "Size: " + softHeight);

                //boolean visible = heightDiff > screenHeight / 3;
            }
        });
    }

    public void Init(){
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        editText = (EditText) findViewById(R.id.edit);
        text = (TextView) findViewById(R.id.text);
        send = (Button) findViewById(R.id.send);
        add = (ImageView)findViewById(R.id.add);
        picture1 = (ImageView) findViewById(R.id.picture1);
        picture2 = (ImageView) findViewById(R.id.picture2);
        picture3 = (ImageView) findViewById(R.id.picture3);
        picture4 = (ImageView) findViewById(R.id.picture4);
        MyClickListener myClickListener = new MyClickListener();
        send.setOnClickListener(myClickListener);
        add.setOnClickListener(myClickListener);
        picture1.setOnClickListener(myClickListener);
        picture2.setOnClickListener(myClickListener);
        picture3.setOnClickListener(myClickListener);
        picture4.setOnClickListener(myClickListener);
        databaseHelper = new DatabaseHelper(this);

        editText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    LinearLayout  emotion =(LinearLayout)findViewById(R.id.emotion);
                    emotion.setVisibility(View.GONE);
                    NewHeight=editText.getHeight();
                    Toast.makeText(CommunityActivity.this, "获得焦点", Toast.LENGTH_SHORT).show();
                } else {
                    oldHeight = editText.getHeight();
                    Toast.makeText(CommunityActivity.this, "失去焦点", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    public void loadMessage(){
        int testnumber = 0;
        Cursor cursor = databaseHelper.query();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int messageColumn = cursor.getColumnIndex("message");
            String message = cursor.getString(messageColumn);
            addMessageToUI(this, message);
            testnumber++;
        }
        Log.d("testnumber", testnumber + "");
        cursor.close();
        Log.d("UDP Demo","onCreate");
    }

    public void loadNotReceiveMessage(){
        int testnumber = 0;
        Cursor cursor = databaseHelper.queryNotReceiveMessageTable();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int messageColumn = cursor.getColumnIndex("message");
            String message = cursor.getString(messageColumn);
            addMessageToUI(this, message);
            testnumber++;
        }
        Log.d("testnumber", testnumber + "");
        cursor.close();
        databaseHelper.clearNotReceiveMessageTable();
    }



    @Override
    protected void onStart() {
        super.onStart();
        loadNotReceiveMessage();
        String ACTION = "HanJun.MessageNotice";
        myReceiver = new MessageBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(myReceiver, filter);
        Intent startServiceIntent2 = new Intent(this, MyService.class).putExtra("user",user).putExtra("pwd",pwd);
        startService(startServiceIntent2);
        Log.d("UDP Demo","onStart");

    }

    @Override
    protected void onResume() {

        //SEND_NOTIFICATION
        Intent intent = new Intent();
        // 设置Intent的Action属性
        intent.setAction("HanJun.DecideSendNotification");
        intent.putExtra("type", false);
        //发送广播
        sendBroadcast(intent);
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //SEND_NOTIFICATION
        Intent intent = new Intent();
        // 设置Intent的Action属性
        intent.setAction("HanJun.DecideSendNotification");
        intent.putExtra("type", true);
        //发送广播
        sendBroadcast(intent);
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyClickListener implements View.OnClickListener {
        public void onClick(View v) {
            if (v.getId() == R.id.send){

                JSONObject obj= new JSONObject();
                try {
                    obj.put("user",user);
                    obj.put("message",editText.getText().toString());
                    message = editText.getText().toString();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // 集合转为json

                Intent intent = new Intent();
                // 设置Intent的Action属性
                intent.setAction("HanJun.sendMessage");
                intent.putExtra("message", obj.toString());

                //发送广播
                sendBroadcast(intent);
            }
            if (v.getId() == R.id.add){

               // addHandle();

                addHandleAnother(v.getContext());
            }
            if(v.getId() == R.id.picture1||v.getId() == R.id.picture2||v.getId() == R.id.picture3||v.getId() == R.id.picture4){
                v.setDrawingCacheEnabled(true);
                Bitmap bitmap = v.getDrawingCache();
                SpannableString ss=getBitmapMime(bitmap,(String)v.getTag(),editText.getWidth());
                insertIntoEditText(ss);
            }
        }
    }


    public void addHandleAnother(Context context){
        RelativeLayout emotion2 = (RelativeLayout)findViewById(R.id.emotion2);
        if(emotion2.getVisibility()==View.GONE) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(CommunityActivity.this.getCurrentFocus().getWindowToken(), 0);

            int[] array = getAppHeightAndWidth();
            int height = getInputHeight();
            emotion2.getLayoutParams().height=height;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.f001, options);
            int pictureWidth = options.outWidth;
            int pictureHeight = options.outHeight;
            int veiticalSpace = 30;
            int horizontalSpace = 10;
            int rownumber =(int)px2dip(context,height) / (pictureHeight + (int)px2dip(context,veiticalSpace));
            int columnnumber =(int)px2dip(context, array[0]) / (pictureWidth + (int)px2dip(context,horizontalSpace));

            List<View> list = new ArrayList<>();
            List<View> childlist = new ArrayList<>();
            View childView;
            GridView childgridView;
            List<Map<String, Object>> listItems;

            emotionPager = (ViewPager) findViewById(R.id.emotionPager);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.emotionchild, null);

            list.add(view);

            MyRelativeLayout mylayout = (MyRelativeLayout) view.findViewById(R.id.mylayout);
            ViewPager child_emotionPager = (ViewPager) view.findViewById(R.id.child_emotionPager);
            mylayout.setChild_viewpager(child_emotionPager);

            int[] pictureId = getPictureId();
            int childpagernum = pictureId.length / (rownumber * columnnumber);
            for (int j = 0; j < childpagernum; j++) {
                listItems = getListItem(pictureId, j, rownumber * columnnumber);
                childView = inflater.inflate(R.layout.emotionchilditem, null);
                childgridView = (GridView) childView.findViewById(R.id.gridView);
                childgridView.setColumnWidth(array[0] / columnnumber-30);
                childgridView.setVerticalSpacing(veiticalSpace);
                MySimpleAdapter mySimpleAdapter = new MySimpleAdapter(context, listItems, R.layout.childgridviewitem
                        , new String[]{"imageview"}, new int[]{R.id.imageView},editText);
                childgridView.setAdapter(mySimpleAdapter);

                childgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                            long arg3) {
                        // TODO Auto-generated method stub
                        MySimpleAdapter.ViewHolder ViewHolder= (MySimpleAdapter.ViewHolder)arg1.getTag();
                        ImageView v= ViewHolder.imageView;
                        v.setDrawingCacheEnabled(true);
                        Bitmap bitmap = v.getDrawingCache();
                        SpannableString ss=getBitmapMime(bitmap,ViewHolder.tag,editText.getWidth());
                        insertIntoEditText(ss);
                    }
                });
                childlist.add(childView);
            }
            child_emotionPager.setAdapter(new MyViewPageAdapter(childlist));
            emotionPager.setAdapter(new MyViewPageAdapter(list));
            emotion2.setVisibility(View.VISIBLE);
        }else{
            emotion2.setVisibility(View.GONE);
        }
    }

    public float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    public int[] getPictureId(){
        int[] id=new int[103];
        for(int i=0;i<103;i++){
            id[i]=R.mipmap.f001+i;
        }
        return id;
    }

    public List<Map<String,Object>> getListItem(int[] resource,int index,int number){
        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = new HashMap<>();
        for(int i=0;i<number;i++){
            map.put("imageview",resource[index*number+i]);
            if(index*number+i>=0&&index*number+i<9){
                map.put("tag","/f00"+(index*number+i+1));
            }else if(index*number+i>=9&&index*number+i<99){
                map.put("tag","/f0"+(index*number+i+1));
            }else if(index*number+i>=99){
                map.put("tag","/f"+(index*number+i+1));
            }

            listItems.add(map);
            map = new HashMap<>();
        }
        return listItems;
    }

    public void addHandle(){
        LinearLayout emotion = (LinearLayout)findViewById(R.id.emotion);
        if(emotion.getVisibility()==View.GONE) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(CommunityActivity.this.getCurrentFocus().getWindowToken(), 0);

            TranslateAnimation  mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(500);
            emotion.startAnimation(mShowAction);
            emotion.setVisibility(View.VISIBLE);

            // 此处为失去焦点时的处理内容
        }else{

            emotion.setVisibility(View.GONE);
        }
    }




    public int getInputHeight(){
      int InputMethodOpen =  sharedPreferences.getInt("InputMethodOpen",0);
      int InputMethodClose =  sharedPreferences.getInt("InputMethodClose",0);
        int height=InputMethodOpen-InputMethodClose;
        if(InputMethodOpen!=0&&InputMethodClose!=0&&height>500){

        }else{
            height=750;
        }
        return height;
    }
/**
 *
 * 获取系统宽和高
 * */
    public int[] getAppHeightAndWidth(){
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new int[]{width,height};
    }

    /**
     * EditText中可以接收的图片(要转化为SpannableString)
     *
     * @param pic
     * @return SpannableString
     */
    private SpannableString getBitmapMime(Bitmap pic,String url,int mInsertedImgWidth) {
        int imgWidth = pic.getWidth();
        int imgHeight = pic.getHeight();
        // 只对大尺寸图片进行下面的压缩，小尺寸图片使用原图
        if (imgWidth >= mInsertedImgWidth) {
            float scale = (float) mInsertedImgWidth / imgWidth;
            Matrix mx = new Matrix();
            mx.setScale(scale, scale);
            pic = Bitmap.createBitmap(pic, 0, 0, imgWidth, imgHeight, mx, true);
        }

        SpannableString ss = new SpannableString(url);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private void insertIntoEditText(SpannableString ss) {
        // 先获取Edittext中原有的内容

        Editable et = editText.getText();
        int start = editText.getSelectionStart();
        // 设置ss要添加的位置
        et.insert(start, ss);
        // 把et添加到Edittext中
        editText.setText(et);
        // 设置Edittext光标在最后显示
        editText.setSelection(start + ss.length());
    }

    public void addMessageToUI(Context context,String message){

        boolean isSelf=false;
        try {
            JSONObject obj = new JSONObject(message);
            String  otherUser= obj.getString("otherUser");
            String otherMessage  = obj.getString("message");

            if(otherUser.equals(user)){
                isSelf = true;
            }

            LinearLayout content = (LinearLayout)findViewById(R.id.content);
            int childNumber = content.getChildCount();
            if (childNumber >= 15) {
                content.removeViewAt(0);
            }

            RelativeLayout contentitem = new RelativeLayout(context);
            RoundImageView roundImageView = new RoundImageView(context);
            roundImageView.setImageResource(R.drawable.bird1);
            roundImageView.setId(id1);

            /**
             * 记录控件的布局
             * */
            LinearLayout.LayoutParams contentitemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            /**
             * 记录文本的布局
             * */
            RelativeLayout.LayoutParams textlayout = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            /**
             * 记录用户名的布局
             * */
            RelativeLayout.LayoutParams userlayout = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            /**
             * 记录图片的布局
             * */
            RelativeLayout.LayoutParams roundImageViewlayout = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView text = new TextView(context);


            TextView Usertext = new TextView(context);
            Usertext.setId(id2);
            Usertext.setText(otherUser);
            SpannableString textcontentSpannableString = handleMessage(otherMessage, context);
            text.setText(textcontentSpannableString);

            textlayout.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            textlayout.addRule(RelativeLayout.BELOW, Usertext.getId());

            if (isSelf) {
                text.setBackgroundResource(R.drawable.balloon_r_selector);
                contentitemParams.gravity = Gravity.RIGHT;
                textlayout.addRule(RelativeLayout.LEFT_OF,roundImageView.getId());

                userlayout.addRule(RelativeLayout.LEFT_OF, roundImageView.getId());
                userlayout.addRule(RelativeLayout.ALIGN_RIGHT, text.getId());
                roundImageViewlayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            } else {
                text.setBackgroundResource(R.drawable.balloon_l_selector);
                textlayout.addRule(RelativeLayout.RIGHT_OF, roundImageView.getId());
                userlayout.addRule(RelativeLayout.RIGHT_OF, roundImageView.getId());
                userlayout.addRule(RelativeLayout.ALIGN_LEFT, text.getId());
                textlayout.addRule(RelativeLayout.END_OF, roundImageView.getId());

            }
            contentitem.addView(roundImageView, roundImageViewlayout);
            contentitem.addView(text, textlayout);
            contentitem.addView(Usertext, userlayout);
            content.addView(contentitem, contentitemParams);

            scrollView = ((CommunityActivity) context).scrollView;
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public SpannableString handleMessage(String message,Context context){
        SpannableString  builder = new SpannableString (message);
        Pattern mPattern = Pattern.compile("/[a-z][0-9]{3}");
        Matcher matcher = mPattern.matcher(message);
        String matchString;
        int pictureId;
        Bitmap bitmap;
        ImageSpan imageSpan;
        while (matcher.find()) {
            matchString = matcher.group();
            pictureId= getTagPicture(matchString);
            if(pictureId>=0){
                bitmap = BitmapFactory. decodeResource (context.getResources(),pictureId);
                imageSpan = new ImageSpan(context,bitmap);
                //bitmap.setBounds(0, 0, 25, 25);//这里设置图片的大小
                builder.setSpan(imageSpan, matcher.start(),
                        matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return builder;
    }

    public int getTagPicture(String matchString){
        int pictureId=-1;
        pictureId= R.mipmap.f001+getIndex(matchString)-1;
        return  pictureId;
    }


    public int getIndex(String index){
        int num=0;
        Pattern mPattern = Pattern.compile("[1-9][0-9]{0,2}");
        Matcher matcher = mPattern.matcher(index);
        String matchString;
        while (matcher.find()) {
            matchString = matcher.group();
            num = Integer.parseInt(matchString);
        }
        return num;
    }

}
