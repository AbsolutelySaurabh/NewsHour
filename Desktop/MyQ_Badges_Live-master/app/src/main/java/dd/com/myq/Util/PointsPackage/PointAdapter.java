package dd.com.myq.Util.PointsPackage;

/**
 * Created by absolutelysaurabh on 8/6/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import dd.com.myq.R;

public class PointAdapter extends ArrayAdapter<Points> {


    public PointAdapter(Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView  == null){

            //   listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item,parent,false);

        }
        //Find the news at the given positiion in the list of the news
        Points currentPoints = getItem(position);

        int pts = currentPoints.getPoints();
        TextView titleView = (TextView)listItemView.findViewById(R.id.points);
        titleView.setText(pts);

        int correctanswer=currentPoints.getCorrectAnswers();

        int wronganswer=currentPoints.getWrongAnswers();

        String active_clients = Integer.toString(correctanswer);//////corrct ans
        String inactive_clients = Integer.toString(wronganswer);////////wrong ans

        plotGraph(active_clients, inactive_clients,listItemView);


        return listItemView;

    }


    public  void plotGraph(String ac, String inc, View view){

        float active_clients = Float.parseFloat(ac);
        float inactive_clients = Float.parseFloat(inc);

        float total = active_clients+inactive_clients;

        if(total==0){
            total=100;
        }
        else total=active_clients+inactive_clients;

        DecoView decoView = (DecoView) view.findViewById(R.id.chart);

        decoView.addSeries(new SeriesItem.Builder(Color.rgb(161, 2, 81))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(39f)
                .build());

        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.WHITE)
                .setRange(0, total, 0)
                .setLineWidth(29f)
                .setSpinDuration(1500)
                .build();

        int series1Index = decoView.addSeries(seriesItem1);

        decoView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        decoView.addEvent(new DecoEvent.Builder(active_clients).setIndex(series1Index).setDelay(4000).build());

    }

}
