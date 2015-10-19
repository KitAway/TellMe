package com.example.d038395.tellme;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;


public class Listen2Topic extends Activity {

    Topic topic;
    ArrayList<Questions> questionsArrayList;
    Iterator<Questions> questionsIterator;
    TextToSpeech tts;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen2_topic);

        Intent intent = getIntent();
        int topicId = intent.getIntExtra("TopicId", 0);
        topic = Topic.getTopicList().get(topicId);
        questionsArrayList=topic.getQuestionList();
        questionsIterator=questionsArrayList.iterator();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    //Toast.makeText(Listen2Topic.this,"Success",Toast.LENGTH_SHORT).show();
                    tts.setLanguage(Locale.US);
                    if(questionsIterator.hasNext())
                        playQuestion(questionsIterator.next());
                }
            }
        });

        final VideoView videoView = (VideoView) findViewById(R.id.vv_question);
        Uri uri =Uri.parse("android.resource://" + getPackageName() +'/'+ R.raw.animation_speaking);
        videoView.setVideoURI(uri);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

    }


    private void playQuestion(final Questions questions){
        final TextView textView = (TextView) findViewById(R.id.tx_question);
        textView.setText(questions.getQuestion());
        final VideoView videoView = (VideoView) findViewById(R.id.vv_question);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                videoView.start();
            }

            @Override
            public void onDone(String utteranceId) {
                playAnswer(questions);
                videoView.pause();
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
        //Toast.makeText(this,questions.getQuestion(),Toast.LENGTH_SHORT).show();
//        tts.speak(questions.getQuestion(), TextToSpeech.QUEUE_ADD,
//                null, questions.getQuestion());
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            tts.speak(questions.getQuestion(),TextToSpeech.QUEUE_ADD,hashMap);
        } else {
            tts.speak(questions.getQuestion(), TextToSpeech.QUEUE_ADD,
                            null, questions.getQuestion());
        }
    }

    private void playAnswer(Questions questions){
        String path = questions.getPath();
        if(path==null){
            Toast.makeText(this,"No record",Toast.LENGTH_SHORT).show();
            finish();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(questions.getPath());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    if(questionsIterator.hasNext())
                        playQuestion(questionsIterator.next());
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            mediaPlayer.release();
            if(questionsIterator.hasNext())
                playQuestion(questionsIterator.next());
            Log.e("Listen2Topic","mediaplayer error");
        }
    }

    @Override
    protected void onDestroy() {
        if(tts!=null)
            tts.shutdown();
        if(mediaPlayer!=null)
            mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listen2_topic, menu);
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
