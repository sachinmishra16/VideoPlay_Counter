package com.blueapple.videoplay_counter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.blueapple.videoplay_counter.Model.VideoTime;
import com.blueapple.videoplay_counter.R;


import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    MediaController mediaController;
    ProgressBar progressBar;

    TextView textView;
    MediaPlayer mp;
    int stopPosition;
    int running_time;
    int time,check_time;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        videoView = findViewById(R.id.videoViewid);
        progressBar = findViewById(R.id.progressBarid);
        mediaController=new MediaController(this);

        sharedPreferences=getSharedPreferences("pref",MODE_PRIVATE);
        editor=sharedPreferences.edit();



//        editor.clear();
//        editor.commit();
//

        String path = (Environment.getExternalStorageDirectory() + "/strike.mp4");




        if( savedInstanceState != null ) {
            stopPosition = savedInstanceState.getInt("video_position");
        }

        textView = findViewById(R.id.textViewid);
/*
        Log.d("path", path);

        Uri uri = Uri.parse(path);

        videoView.setVideoURI(uri);*/

        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.strike));
        videoView.setMediaController(mediaController);
        videoView.start();



/*

            final long time = videoView.getDuration();
*/

//            Toast.makeText(this, "" + videoView.getCurrentPosition(), Toast.LENGTH_SHORT).show();


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp)
            {

                final int duration = videoView.getDuration();

                Log.d("duration", String.valueOf(duration));
                Log.d("videoview_position", String.valueOf(videoView.getCurrentPosition()));


                    final Thread thread=new Thread(
                            new Runnable() {
                        @Override
                        public void run() {

                            do {
                                textView.post(new Runnable()
                                {
                                    public void run()
                                    {

                                        int time = (videoView.getCurrentPosition()) / 1000;

                                        check_time  =sharedPreferences.getInt("time_running",0);

                                        if (videoView.isPlaying()) {


                                            if (((videoView.getDuration() / 1000) + 10) <= check_time)
                                            {

                                                videoView.stopPlayback();
                                                textView.setText("time finished");

                                                //  Toast.makeText(MainActivity.this, "time finished", Toast.LENGTH_SHORT).show();

                                            }
                                            else
                                                {

                                                running_time = (check_time+ 1);

                                                editor.putInt("time_running", running_time);

                                                editor.commit();


                                                Log.d("time_saved", String.valueOf(check_time));


                                                Log.d("running_time", String.valueOf(running_time));

                                                textView.setText(time + "");

                                            }

                                           // databaseReference.setValue(videoTime);

                                        }


                                    }
                                });
                                try
                                {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                            while (videoView.getCurrentPosition() < duration);
                        }

                    });

                    thread.start();








                             /*   new Thread(new Runnable() {
                    public void run() {
                        do {
                            textView.post(new Runnable() {
                                public void run() {
                                    int time = (duration - videoView.getCurrentPosition()) / 1000;
                                    textView.setText(time + "");
                                }
                            });
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        while (videoView.getCurrentPosition() < duration);
                    }
                }).start();*/
            }

        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stopPosition = videoView.getCurrentPosition();
        videoView.pause();
        outState.putInt("video_position", stopPosition);
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d("Tag", "onResume");
        videoView.seekTo(stopPosition);
        videoView.start(); //Or use videoView.resume() if it doesn't work.
    }

}
