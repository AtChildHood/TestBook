package com.example.hanjun.testbook;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MyAdapter.MyViewPageAdapter;
import MyView.MyViewPager;

public class Second extends Fragment{

    private MyViewPager viewPager;
    private List<View> lists = new ArrayList<View>();
    private MyViewPageAdapter adapter;
    private ImageView cursor;
    private int bmpw = 0; // 游标宽度
    private int offset = 0;// // 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private TextView booklist;
    private TextView comment;
    private TextView topic;
    private TextView recommend;
    private boolean isFirst;
    GridView booklistView;

    List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.secondfragment,container,false);

        booklist = (TextView)root.findViewById(R.id.booklist);
        comment = (TextView)root.findViewById(R.id.comment);
        topic = (TextView)root.findViewById(R.id.topic);
        recommend = (TextView)root.findViewById(R.id.recommend);

        myOnClickListener ClickListener = new myOnClickListener();
        booklist.setOnClickListener(ClickListener);
        comment.setOnClickListener(ClickListener);
        topic.setOnClickListener(ClickListener);
        recommend.setOnClickListener(ClickListener);

        View booklistLayout = inflater.inflate(R.layout.booklist,null);
        View commentLayout = inflater.inflate(R.layout.comment,null);
        View topicLayout = inflater.inflate(R.layout.topic,null);
        View recommend = inflater.inflate(R.layout.recommend,null);


        booklistView = (GridView)booklistLayout.findViewById(R.id.booklistView);

        for(int i=0;i<30;i++){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("filename", R.drawable.bird1);
            listItems.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),listItems,R.layout.item1,new String[]{"filename"},new int[]{R.id.imageView1});
        booklistView.setAdapter(simpleAdapter);


        lists.add(booklistLayout);
        lists.add(commentLayout);
        lists.add(topicLayout);
        lists.add(recommend);

        viewPager = (MyViewPager)root.findViewById(R.id.viewPager);
        cursor = (ImageView)root.findViewById(R.id.cursor);

        viewPager.setMyParent(getActivity().findViewById(R.id.horizontalScrollView));

        //初始化指示器位置
        initCursorPos();
        adapter = new MyViewPageAdapter(lists);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new MyPageChangeListener());



        return root;
    }

    //初始化指示器位置
    public void initCursorPos() {
        // 初始化动画

        bmpw = BitmapFactory.decodeResource(getResources(), R.drawable.line)
                .getWidth();// 获取图片宽度

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / lists.size() - bmpw) / 2;// 计算偏移量

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }



    private class myOnClickListener implements ImageView.OnClickListener{
        public void onClick(View v){
            switch(v.getId()){
                case R.id.recommend:
                    viewPager.setCurrentItem(0, true);
                    break;
                case R.id.booklist:
                    viewPager.setCurrentItem(1, true);
                    break;
                case R.id.comment:
                    viewPager.setCurrentItem(2, true);
                    break;
                case R.id.topic:
                    viewPager.setCurrentItem(3, true);
                    break;
                default:
                    break;
            }
        }

    }

    //页面改变监听器
    public class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpw;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;// 页卡1 -> 页卡3 偏移量
        int third = one * 3;// 页卡1 -> 页卡4 偏移量

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    isFirst=true;
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }else if(currIndex == 3){
                        animation = new TranslateAnimation(third, 0, 0, 0);
                    }
                    break;
                case 1:
                    isFirst=false;
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    } else if (currIndex == 3) {
                        animation = new TranslateAnimation(third, one, 0, 0);
                    }
                    break;
                case 2:
                    isFirst=false;
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    } else if (currIndex == 3) {
                        animation = new TranslateAnimation(third,two, 0, 0);
                    }
                    break;
                case 3:
                    isFirst=false;
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, third, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, third, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two,third, 0, 0);
                    }
                    break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
            viewPager.changeFirstState(isFirst);
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }




    public void onAttach(Activity activity){
        super.onAttach(activity);
        Toast.makeText(activity, "activity",Toast.LENGTH_SHORT).show();
    }


    public void onDetach(Activity activity){
        super.onDetach();
        Toast.makeText(activity, "��activity��ɾ��",Toast.LENGTH_SHORT).show();
    }
}
