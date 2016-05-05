package com.example.hanjun.testbook;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import MyView.SlidingMenu;

public class MainActivity extends AppCompatActivity {

    private boolean isMenuOpen;
    private boolean isSecondFragmentShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImageView set = (ImageView)findViewById(R.id.imageView0);
        ImageView head = (ImageView)findViewById(R.id.head);
        TextView HomePage = (TextView)findViewById(R.id.textView10);
        TextView Community = (TextView)findViewById(R.id.textView11);



        getFragmentManager().beginTransaction().add(R.id.container, new FirstFragment()).commit();
        myOnClickListener ClickListener = new myOnClickListener();
        HomePage.setOnClickListener(ClickListener);
        Community.setOnClickListener(ClickListener);
        set.setOnClickListener(ClickListener);
        head.setOnClickListener(ClickListener);

    }

    private class myOnClickListener implements ImageView.OnClickListener{
        public void onClick(View v){
            FragmentManager fragmentManager = getFragmentManager();
            Fragment replace;
            switch(v.getId()){
                case R.id.imageView0:
                    SlidingMenu slidingMenu  = (SlidingMenu)findViewById(R.id.horizontalScrollView);

                    if(isMenuOpen==false) {
                        slidingMenu.showMenu();
                        isMenuOpen = true;
                    }else{
                        slidingMenu.hideMenu();
                        isMenuOpen=false;
                        if(!isMenuOpen&&isSecondFragmentShow){
                            //((MyViewPager)findViewById(R.id.viewPager)).changeFirstState(true);
                        }
                    }
                    return;
                case R.id.textView11:
                    replace = new FirstFragment();
                    isSecondFragmentShow=false;
                    break;
                case R.id.textView10:
                    replace = new Second();
                    isSecondFragmentShow=true;
                    break;
                default:
                    replace=null;
            }
            fragmentManager.beginTransaction().replace(R.id.container,replace).commit();

        }

    }

}
