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

public class Levels extends AppCompatActivity {
    ListView simpleList;
    String[] countryList = {"Beginner", "Intermediate", "God of Coding"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Categories c = new Categories();
        simpleList = (ListView) findViewById(R.id.simpleListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview_levels, R.id.textView, countryList);
        simpleList.setAdapter(arrayAdapter);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("test", "" + position);

                switch (position) {
                    case 0:
                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                        intent.putExtra("level", "E");
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(getBaseContext(), HomeActivity.class);
                        intent1.putExtra("level", "M");
                        startActivity(intent1);
                        break;

                    case 2:
                        Intent intent2 = new Intent(getBaseContext(), HomeActivity.class);
                        intent2.putExtra("level", "H");
                        startActivity(intent2);
                        break;
                }

            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
