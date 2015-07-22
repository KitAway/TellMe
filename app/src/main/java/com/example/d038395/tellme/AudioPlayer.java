package com.example.d038395.tellme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by d038395 on 2015-07-21.
 */
public class AudioPlayer {
    Activity activity;
    File audioFile;
    String title;

    public AudioPlayer(Activity activity, File audioFile,String title) {
        this.activity = activity;
        this.audioFile = audioFile;
        this.title=title;
    }

    public AudioPlayer(Activity activity, File audioFile) {
        this.activity = activity;
        this.audioFile = audioFile;
    }

    public AudioPlayer(Activity activity, String audioPath) {
        this.activity = activity;
        this.audioFile = new File(audioPath);
        this.title=null;
    }

    public AudioPlayer(Activity activity, String audioPath,String title) {
        this.activity = activity;
        this.audioFile = new File(audioPath);
        this.title=title;
    }

    private String formatMilliToHMS(int milli){
        int hour, minute,second;
        second=milli/1000;
        minute=second/60;
        second%=60;
        if (minute<60)
            return String.format("%02d:%02d",minute,second);
        hour=minute/60;
        minute%=60;
        return String.format("%d:%02d:%02d",hour,minute,second);
    }

    public void play(){
        final MediaPlayer mediaPlayer = new MediaPlayer();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog dialog=null;
        final View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_player, null);
        builder.setView(dialogView);
        final TextView tvStart = (TextView)dialogView.findViewById(R.id.text_start);
        TextView tvDuration = (TextView) dialogView.findViewById(R.id.text_duration);
        final SeekBar seekBar = (SeekBar)dialogView.findViewById(R.id.seekbar_player);
        TextView storyName = (TextView)dialogView.findViewById(R.id.text_filname);
        storyName.setText(title);
        final ImageButton imageButton = (ImageButton)dialogView.findViewById(R.id.btn_control);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageButton.setImageResource(R.drawable.ic_action_play);
                }
                else {
                    mediaPlayer.start();
                    imageButton.setImageResource(R.drawable.ic_action_pause);
                }

            }
        });
        class audioPlayTextUpdate extends AsyncTask<MediaPlayer,Integer,Void> {
            protected Void doInBackground(MediaPlayer... mps) {
                MediaPlayer mp = mps[0];
                while(mp!=null) {
                    if(mp.isPlaying())
                        publishProgress(mp.getCurrentPosition());
                }
                return null;
            }
            protected void onProgressUpdate(Integer... progress) {
                seekBar.setProgress(progress[0]);
                tvStart.setText(formatMilliToHMS(progress[0]));

            }
        }
        final AsyncTask<MediaPlayer,Integer,Void> asyncTask =new audioPlayTextUpdate();
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if(mp.getDuration()!=mp.getCurrentPosition()) {
                    mp.start();
                    imageButton.setImageResource(R.drawable.ic_action_pause);
                }
                else mp.seekTo(0);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageButton.setImageResource(R.drawable.ic_action_play);
                mediaPlayer.pause();
            }
        });
        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tvStart.setText(formatMilliToHMS(seekBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.pause();
                    imageButton.setImageResource(R.drawable.ic_action_play);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                }
            });
            mediaPlayer.prepare();
            int duration = mediaPlayer.getDuration();
            tvDuration.setText(formatMilliToHMS(duration));
            seekBar.setMax(duration);
            tvStart.setText(formatMilliToHMS(mediaPlayer.getCurrentPosition()));
            mediaPlayer.start();
            dialog=builder.create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    asyncTask.cancel(true);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            });
            dialog.show();
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mediaPlayer);
        } catch (IOException e) {
            Toast.makeText(activity,
                    "can't play the media", Toast.LENGTH_LONG).show();
            mediaPlayer.release();
            e.printStackTrace();
        }
    }
}
