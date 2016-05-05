package MyView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by hanjun on 2016/4/3.
 */
public class SlidingMenu extends HorizontalScrollView{

    private boolean isSeted;
    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContext;
    private int myScreenWidth;
    private int mMenuRightPadding = 50;
    private int mMenuWidth;
    private boolean isOpen;


    public SlidingMenu(Context context,AttributeSet attrs){
        super(context, attrs);
        WindowManager myWindowManager= (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics myDisplay = new DisplayMetrics();
        myWindowManager.getDefaultDisplay().getMetrics(myDisplay);

        myScreenWidth = myDisplay.widthPixels;

        mMenuRightPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, context
                        .getResources().getDisplayMetrics());
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if(!isSeted){
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContext = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = myScreenWidth
                    - mMenuRightPadding;
            mContext.getLayoutParams().width = myScreenWidth;
            isSeted = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (changed)
        {
            this.scrollTo(mMenuWidth, 0);
        }
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("LTAG", "SlidingMenu onInterceptTouchEvent");
        Log.i("LTAG", "SlidingMenu onInterceptTouchEvent default return" + super.onInterceptTouchEvent(ev));
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        Log.i("LTAG", "SlidingMenu dispatchTouchEvent");
        Log.i("LTAG", "SlidingMenu dispatchTouchEvent default return" + super.dispatchTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent arg0) {
//        // TODO Auto-generated method stub
//        //当拦截触摸事件到达此位置的时候，返回true，
//        //说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent
//
//            onTouchEvent(arg0);
//            return super.onInterceptTouchEvent(arg0);
//    }


    public boolean onTouchEvent(MotionEvent ev){
        Log.i("LTAG", "SlidingMenu onTouchEvent");

        int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_UP:
                // 隐藏在左边的宽度
                int scrollX = getScrollX();
                if (scrollX >= mMenuWidth / 2)
                {
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                } else
                {
                    this.smoothScrollTo(0, 0);
                    isOpen = true;
                }
                Log.i("LTAG", "SlidingMenu onTouchEvent return" + true);
                return true;
        }
        Log.i("LTAG", "SlidingMenu onTouchEvent return" + super.onTouchEvent(ev));
        return super.onTouchEvent(ev);
    }

    public void showMenu(){
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }
    public void hideMenu(){
        this.smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
    }

}
