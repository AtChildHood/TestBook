package MyAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.example.hanjun.testbook.R;

import java.util.List;
import java.util.Map;



/**
 * Created by hanjun on 2016/4/4.
 */
public class MySimpleAdapter extends SimpleAdapter {

    LayoutInflater mInflater;
    List<Map<String,Object>> listItems;
    EditText editText;
    Context context;
    public MySimpleAdapter(Context context,
                           List<Map<String, Object>> data, int resource,
                           String[] from, int[] to,EditText editText) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
        mInflater = LayoutInflater.from(context);
        listItems = data;
        this.editText =editText;
        this.context =context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.childgridviewitem, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
            Log.i("", "创建view");
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            Log.i("","复用view");
        }

        viewHolder.imageView.setImageResource((int)listItems.get(position).get("imageview"));
        viewHolder.tag=(String)listItems.get(position).get("tag");
        return convertView;
    }

    public static class ViewHolder{
        public ImageView imageView;
        public String tag;
    }

}
