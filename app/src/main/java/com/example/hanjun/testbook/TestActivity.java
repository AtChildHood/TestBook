package com.example.hanjun.testbook;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import MyAdapter.MyViewPageAdapter;
import MyView.MyRelativeLayout;

public class TestActivity extends AppCompatActivity {
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_test);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> list = new ArrayList<View>();
        View view = null, childView = null;
        ViewPager child_viewpager;
        TextView textview, parenttextview;
        List<View> childlist = null;
        MyRelativeLayout mylayout;
        for (int i = 0; i < 3; i++)
        {
            view = inflater.inflate( R.layout.child_viewpager_layout , null);
            list.add(view);

            //注入里层viewpager
            mylayout = (MyRelativeLayout) view.findViewById(R.id.mylayout);
            child_viewpager = (ViewPager) view.findViewById(R.id.child_viewpager);
            mylayout.setChild_viewpager(child_viewpager);

            parenttextview = (TextView) view.findViewById(R.id.textview);
            parenttextview.setText("viewpager：" + i);


            childlist = new ArrayList<View>();
            for (int j = 0; j < 3; j++)
            {
                childView = inflater.inflate(R.layout.child_viewpager_item ,
                    null);
                textview = (TextView) childView.findViewById(R.id.textview);
                textview.setText("view" + i + "：" + j);
                childlist.add(childView);

            }
            child_viewpager.setAdapter(new MyViewPageAdapter(childlist));
        }
        viewpager.setAdapter(new MyViewPageAdapter(list));
    }
}