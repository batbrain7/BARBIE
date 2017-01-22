package com.example.mohitkumar.isgw;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Dell on 1/21/2017.
 */

public class SplashScreen extends Activity implements TextToSpeech.OnInitListener{

    TextView textView;

    String[] arr = {"Hello","Hello this","Hello this is","Hello this is Barbie","Hello this is Barbie your","Hello this is Barbie your virtual",
            "Hello this is Barbie your virtual home ","Hello this is Barbie your virtual home Assistant"};

    String[] ar =  {"Hello","this","is","barbie","your","virtual","AI","assistant"};

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splashscreen);

//        tts = new TextToSpeech(this,this);

        textView = (TextView)findViewById(R.id.text3);

//        Thread myThread=new Thread(){
//            @Override
//            public void run() {
//                try {
//                    sleep(3000);

             //   new Animate().execute();
        //speakOut();

        for(int i = 0;i<arr.length;i++) {
            textView.setText(arr[i]);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
                        Intent intent=new Intent(getApplicationContext(),SpeechToText.class);
                        startActivity(intent);
                        finish();

               // } catch (InterruptedException e) {
                 //   e.printStackTrace();
              //  }
            //}
        //};
        //myThread.start();

    }

    private void speakOut() {
        for(int i =0;i<ar.length;i++) {
            tts.speak(ar[i], TextToSpeech.QUEUE_FLUSH, null);
            textView.setText(arr[i]);

        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("TTS", "This Language is not supported");
            } else {
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class Animate extends AsyncTask<Void,String,String>{

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values[0]);
            textView.setText(values[0]);
        }


        @Override
        protected String doInBackground(Void... params) {

            for(int i=0;i<arr.length;i++) {
                tts.speak(ar[i],TextToSpeech.QUEUE_FLUSH,null);
                publishProgress(arr[i]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
