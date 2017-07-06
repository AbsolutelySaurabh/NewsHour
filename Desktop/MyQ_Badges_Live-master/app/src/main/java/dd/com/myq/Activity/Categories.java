package dd.com.myq.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import dd.com.myq.R;

public class Categories extends AppCompatActivity
{
    ListView simpleList;
    String countryList[] = {"PHP", "C","SOFTWARE ENGINEERING","ROBOTICS AND AI","C++","CLOUD COMPUTING"};

    @Override   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      setContentView(R.layout.activity_levels);
        setContentView(R.layout.activity_categories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Levels l=new Levels();
        simpleList = (ListView)findViewById(R.id.simpleListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview_categories, R.id.textView, countryList);
        simpleList.setAdapter(arrayAdapter);

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("test",""+position);


                switch (position) {
                    case 0:
                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                        intent.putExtra("category", "PHP");
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(getBaseContext(), HomeActivity.class);
                        intent1.putExtra("category", "C");
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent4 = new Intent(getBaseContext(), HomeActivity.class);
                        intent4.putExtra("category", "SOFTWARE ENGINEERING");
                        startActivity(intent4);
                        break;
//                    case 3:
//                        Intent intent2 = new Intent(getBaseContext(), HomeActivity.class);
//                        intent2.putExtra("category", "AUTOMATION SYSTEM");
//                        startActivity(intent2);
//                        break;
                    case 3:
                        Intent intent5 = new Intent(getBaseContext(), HomeActivity.class);
                        intent5.putExtra("category", "ROBOTICS AND AI");
                        startActivity(intent5);
                        break;
                    case 4:
                        Intent intent7 = new Intent(getBaseContext(), HomeActivity.class);
                        intent7.putExtra("category", "C++");
                        startActivity(intent7);
                        break;
                    case 5:
                        Intent intent8 = new Intent(getBaseContext(), HomeActivity.class);
                        intent8.putExtra("category", "CLOUD COMPUTING");
                        startActivity(intent8);
                        break;
                }
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }
}