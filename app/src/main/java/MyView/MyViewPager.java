package MyView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hanjun on 2016/4/4.
 */
public class MyViewPager extends ViewPager {

    private boolean isSecond;
    private boolean isFirst;
    private View viewparent;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public void setMyParent(View viewparent) {
        this.viewparent = viewparent;
        // TODO Auto-generated constructor stub
    }

    public MyViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        Log.i("LTAG", "MyViewPager dispatchTouchEvent");
        Log.i("LTAG", "MyViewPager dispatchTouchEvent default return" + super.dispatchTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        //当拦截触摸事件到达此位置的时候，返回true，
        //说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent

        if(isSecond){
                onTouchEvent(arg0);
                return super.onInterceptTouchEvent(arg0);
        }else{
            onTouchEvent(arg0);
            return super.onInterceptTouchEvent(arg0);
        }

    }

    /*
    * requestDisallowInterceptTouchEvent这个函数很关键的，这个函数可以请求父类控件不要干涉我的控件的触摸事情操作
    *
    * */
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {

        Log.i("LTAG", "MyViewPager onTouchEvent");
        Log.i("LTAG", "MyViewPager onTouchEvent return" + super.onTouchEvent(arg0));
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(arg0);
    }

    public void  changeState(boolean isSecond){
        this.isSecond = isSecond;

    }
    public void  changeFirstState(boolean isFirst){
        this.isFirst = isFirst;

    }


}
