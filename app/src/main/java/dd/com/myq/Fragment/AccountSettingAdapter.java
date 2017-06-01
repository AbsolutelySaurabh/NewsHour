package dd.com.myq.Fragment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

public class AccountSettingAdapter extends BaseAdapter {

    String [] option_names;
    Context context;
    private int selectedIndex;
    SessionManager sessionManager;
    private static LayoutInflater inflater=null;

    public AccountSettingAdapter(Context context, String[] option_names) {
        // TODO Auto-generated constructor stub
        context=context;
        this.option_names=option_names;
        selectedIndex = -1;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return option_names.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void moveToPosition(int position){

        switch (position){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                Toast.makeText(context, "Successfully logged out!", Toast.LENGTH_SHORT).show();
                sessionManager.logoutUser();
                break;
        }

    }

    public class Holder
    {
        TextView option_name;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        final View rowView;

        rowView = inflater.inflate(R.layout.account_setting_item, null);
        holder.option_name=(TextView) rowView.findViewById(R.id.option_text);
        holder.option_name.setText(option_names[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setSelectedIndex(position);
                moveToPosition(position);

            }
        });
        return rowView;
    }
}
