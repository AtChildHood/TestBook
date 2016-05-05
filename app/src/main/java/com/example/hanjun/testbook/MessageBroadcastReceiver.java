package com.example.hanjun.testbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MyView.RoundImageView;

/**
 * Created by hanjun on 2016/4/22.
 */

public class MessageBroadcastReceiver extends BroadcastReceiver {

    ScrollView scrollView;
    int childNumber;
    boolean isSelf;
    DatabaseHelper databaseHelper;
    @android.support.annotation.IdRes int id1 = 123;
    @android.support.annotation.IdRes int id2 = 124;
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");
        String message = intent.getStringExtra("message");
        JSONObject obj;

        databaseHelper = new DatabaseHelper(context);
        try {
            obj = new JSONObject(message);

            if(type.equals("connect")) {
                Toast.makeText(context, "创建连接成功", Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "user:" + obj.getString("user"), Toast.LENGTH_SHORT).show();
                //((CommunityActivity)context).ID = obj.getInt("id");
            }
            if(type.equals("receive")) {

                String otherUser="";
                String otherMessage="";
                String messagetype = obj.getString("messagetype");
                if(messagetype.equals("responseSend")){
                    if(obj.getBoolean("message")) {
                        otherUser = ((CommunityActivity)context).user;
                        otherMessage = ((CommunityActivity)context).message;
                        ((CommunityActivity)context).editText.setText("");
                        isSelf=true;
                        JSONObject itSelfMessage = new JSONObject();
                        itSelfMessage.put("otherUser",otherUser);
                        itSelfMessage.put("message",otherMessage);
                        itSelfMessage.put("messagetype","othermessage");
                        databaseHelper.insert(itSelfMessage.toString());
                    }else{
                        Toast.makeText(context, "消息发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "receivemessage:" + message, Toast.LENGTH_SHORT).show();
                    otherUser = obj.getString("otherUser");
                    otherMessage = obj.getString("message");
                    isSelf=false;
                    databaseHelper.insert(message);
                }

                LinearLayout content = (LinearLayout)((CommunityActivity)context)
                        .findViewById(R.id.content);
                childNumber=content.getChildCount();
                if(childNumber>=15){
                    content.removeViewAt(0);
                }

                RelativeLayout contentitem = new RelativeLayout(context);
                RoundImageView roundImageView = new RoundImageView(context);
                roundImageView.setImageResource(R.drawable.bird1);
                roundImageView.setId(id1);

                /**
                 * 记录控件的布局
                 * */
                LinearLayout.LayoutParams contentitemParams= new LinearLayout.LayoutParams(
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
                    textlayout.addRule(RelativeLayout.LEFT_OF, roundImageView.getId());

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

                scrollView = ((CommunityActivity)context).scrollView;
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }



        } catch (JSONException e) {
            // TODO Auto-generated catch block
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
