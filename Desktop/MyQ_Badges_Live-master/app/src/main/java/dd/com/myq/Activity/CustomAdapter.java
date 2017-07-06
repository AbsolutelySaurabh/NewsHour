package dd.com.myq.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import dd.com.myq.R;

public class CustomAdapter extends BaseAdapter{

    ArrayList<String> result,result1,result2;
    Context context;
     ImageLoader imageLoader;
    ArrayList<String> imageId;
    DisplayImageOptions options;

    private static LayoutInflater inflater=null;
    public CustomAdapter(Badges mainActivity, ArrayList<String> prgmNameList, ArrayList<String> prgmNameList1, ArrayList<String> prgmImages) {
        // TODO Auto-generated constructor stub
        context=mainActivity;
        result=prgmNameList;
        result1=prgmNameList1;

        result2=prgmImages;

        imageLoader = ImageLoader.getInstance();

        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).showImageForEmptyUri(R.drawable.greyshield).build();


        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        TextView tv1;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.badge_programlist, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.tv1=(TextView) rowView.findViewById(R.id.textView2);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);

        holder.tv.setText(result.get(position));
        holder.tv1.setText(result1.get(position));
////////////////////////////////////////////////////////////////////////////////
        String url="http://myish.com:10011/images/badgeserver/"+result2.get(position);


        Log.d("url =",url);

        imageLoader.displayImage(url, holder.img, options);
///////////////////////////////////////////////////////////////////////////////


        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               // Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

}