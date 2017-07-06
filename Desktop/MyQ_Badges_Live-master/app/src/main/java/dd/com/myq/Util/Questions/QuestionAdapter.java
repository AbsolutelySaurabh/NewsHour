package dd.com.myq.Util.Questions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import dd.com.myq.R;

/**
 * Created by absolutelysaurabh on 6/6/17.
 */

public class QuestionAdapter extends ArrayAdapter<Question> {

    Context mContext;

    private ArrayList<String> al;

    SwipeFlingAdapterView flingContainer;

    private ArrayAdapter<String> arrayAdapter;
    private int i;


    public QuestionAdapter(@NonNull Context context, List<Question> questionList) {
        super(context, 0, questionList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView  == null){

            //Inflate is a system service that creates a View out of an XML layout
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home,parent,false);

        }
        Question currentQuestion = getItem(position);

        String text = currentQuestion.getText();

        flingContainer = (SwipeFlingAdapterView) listItemView.findViewById(R.id.frame);

        al = new ArrayList<>();

        al.add("hey");

        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item, R.id.helloText, al);

        flingContainer.setAdapter(arrayAdapter);


        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                makeToast(getContext(), "Left!");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                makeToast(getContext(), "Right!");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();

            }


        });


        return listItemView;
    }



    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.right)
    public void right() {
        /**
         * Trigger the right event manually.
         */
        flingContainer.getTopCardListener().selectRight();
    }

    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }


}

