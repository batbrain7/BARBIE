package com.example.mohitkumar.isgw;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Window;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Locale;

/**
 * Created by Dell on 1/21/2017.
 */

public class SplashScreen extends Activity {

    String uriPath = "android.resource://com.example.mohitkumar.isgw/" + R.drawable.barbie;
    VideoView videoview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        videoview = (VideoView) findViewById(R.id.video);


        Uri uri = Uri.parse(uriPath);
        videoview.setVideoURI(uri);

        videoview.setMediaController(new MediaController(this));

        videoview.start();

        Thread myThread=new Thread(){
            @Override
            public void run() {
                try {

                    sleep(3000);

                    Intent intent=new Intent(getApplicationContext(),SpeechToText.class);
                    startActivity(intent);
                    finish();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}