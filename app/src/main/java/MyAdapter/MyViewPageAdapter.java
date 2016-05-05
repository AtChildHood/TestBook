package MyAdapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by hanjun on 2016/4/3.
 */
public class MyViewPageAdapter extends PagerAdapter {

    List<View> viewLists;


    public MyViewPageAdapter(List<View> lists)
    {
        viewLists = lists;
        // this.titleList = titleList;
    }

    //获得size
    @Override
    public int getCount() {

        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0 == arg1;
    }

    //销毁Item
    @Override
    public void destroyItem(View view, int position, Object object)
    {
        ((ViewPager) view).removeView(viewLists.get(position));
    }

    //实例化Item
    @Override
    public Object instantiateItem(View view, int position)
    {
        ((ViewPager) view).addView(viewLists.get(position), 0);
        return viewLists.get(position);
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        // TODO Auto-generated method stub
//        return titleList.get(position);
//    }
}
