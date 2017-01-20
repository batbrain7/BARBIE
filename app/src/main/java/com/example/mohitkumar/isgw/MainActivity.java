package com.example.mohitkumar.isgw;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener,TextToSpeech.OnInitListener{

    private AIService aiService;
    TextView textView;
    Button button;
    private TextToSpeech tts;
    private static final int CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button_mic);

        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);


        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},CODE);
            return;
        }

        tts = new TextToSpeech(this,this);
        textView = (TextView)findViewById(R.id.resulttext);
        final AIConfiguration config = new AIConfiguration("83fe62075c714a3b8edfcb47f3379ed8",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AIContext> contexts = new ArrayList<>();
                contexts.add(new AIContext("I want news on sex"));
                //contexts.add(new AIContext("secondContext"));
                RequestExtras requestExtras = new RequestExtras(contexts, null);
                aiService.startListening(requestExtras);
             //   aiService.startListening();
            }
        });

    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        String parameterString = "";

        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        textView.setText("Query:" + result.getResolvedQuery() +
                "\nAction: " + result.getAction() +
                "\nParameters: " + parameterString);

        speakOut();

    }

    @Override
    public void onError(AIError error) {
        textView.setText(error.toString());

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    private void speakOut() {
        String text = textView.getText().toString();
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CODE) {
            if (permissions[0] == Manifest.permission.RECORD_AUDIO) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  selectedImage = getImageUri(LocationPhoto.this,photos);
                    Toast.makeText(getApplicationContext(),"Enabled",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
