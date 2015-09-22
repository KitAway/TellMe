package com.example.d038395.tellme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TopicPanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_panel);

        if (Topic.firstRun){
            Topic first= new Topic("Unforgettable trip");
            Topic second = new Topic("First lesson in primary school");
            Topic third = new Topic("My childhood");

            first.addQuestion(new Questions("When was the trip?"));
            first.addQuestion(new Questions("Who did you go with?"));
            first.addQuestion(new Questions("Where did you go?"));
            first.addQuestion(new Questions("Could you tell us more details about this trip?"));

            second.addQuestion(new Questions("Who was the teacher?"));
            second.addQuestion(new Questions("What's the topic?"));
            second.addQuestion(new Questions("Could you tell us more details about this lesson?"));

            third.addQuestion(new Questions("Where did you stay when you were a child?"));
            third.addQuestion(new Questions("Could you tell us more details about your childhood?"));

            first.addTopic();
            second.addTopic();
            third.addTopic();
        }

        refreshListView();
        ListView listView = (ListView) findViewById(R.id.lv_topic);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContextMenu(view);
            }
        });
    }
    private void refreshListView(){
        ListView listView = (ListView)findViewById(R.id.lv_topic);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.topic_list, Topic.getTopicList());
        listView.setAdapter(arrayAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_topic_panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
