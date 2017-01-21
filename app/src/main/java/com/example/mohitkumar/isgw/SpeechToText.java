package com.example.mohitkumar.isgw;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

/**
 * Created by Dell on 1/21/2017.
 */

public class SpeechToText extends AppCompatActivity {


    String sout;
    private static String TAG = MainActivity.class.getSimpleName();
    private String jsonResponse;
    String url1 = "https://api.api.ai/api/query?v=20150910&query=";
    String url2 = "&lang=en&sessionId=b6a6145c-4945-4681-bb51-1544ff061146&timezone=2017-01-21T05:05:13+0530' -H 'Authorization:Bearer 67565cd4b0a34c6c82ec141d969541be";
    String s;
    String ACCESS_TOKEN="67565cd4b0a34c6c82ec141d969541be";
    private TextView txtSpeechInput,res;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speechtotext);
        txtSpeechInput=(TextView)findViewById(R.id.txtSpeechInput);
        res=(TextView)findViewById(R.id.response);
        btnSpeak=(ImageButton)findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    final ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtSpeechInput.setText(result.get(0));
                   // for(int i=0;i<result.size()-1;i++){

                   //         s=result.get(i)+" ";

                    //}
                    //sout = "";
                    //for(int i=0;i<s.length();i++){
                      //  if(s.charAt(i)==' '){
                      //      sout+="%20";
                      //  }
                       // else{
                     //       sout+=s.charAt(i);
                    //    }
                  //  }
                     //finalStr=url1+sout+url2;


                    s = txtSpeechInput.getText().toString();

                    final AIConfiguration config = new AIConfiguration(ACCESS_TOKEN,
                            AIConfiguration.SupportedLanguages.English,
                            AIConfiguration.RecognitionEngine.System);

                    final AIDataService aiDataService = new AIDataService(config);

                    final AIRequest aiRequest = new AIRequest();
                    aiRequest.setQuery(s);

                    new AsyncTask<AIRequest, Void, AIResponse>() {
                        @Override
                        protected AIResponse doInBackground(AIRequest... requests) {
                            final AIRequest request = requests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(AIResponse aiResponse) {

                            if(aiResponse == null){
                                //Log.d("Result","NULL");
                            }
                            //if (aiResponse != null ) {
                           // Log.d("INHERE","Entered");
                                // process aiResponse here
                                Result result = aiResponse.getResult();

                            if(result != null) {
                                String s = result.getFulfillment().getSpeech().toString();

                               // Log.d("INHERE1",s);

                                //makeJsonObjectRequest();


                                res.setText(s);
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                            } else  {
                                onPostExecute( aiResponse);
                                res.setText("Please say that again!");
                            }
                            //}
                            //Log.d("INHERE3","NOW HERE");
                        }
                    }.execute(aiRequest);
                }
                break;
            }

        }

    }

    private void makeJsonObjectRequest() {

      final String finalStr=url1+sout+url2;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                finalStr,(JSONObject) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    //String name = response.getString("name");
                    //String email = response.getString("email");
                    //JSONObject phone = response.getJSONObject("phone");
                    //String home = phone.getString("home");
                    //String mobile = phone.getString("mobile");

                    jsonResponse = response.getJSONObject("fulfillment").getString("speech");

                    Log.d("Response",jsonResponse);

                    res.setText(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        MySingleton.getInstance(SpeechToText.this).addToRequestQueue(jsonObjReq);
        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(jsonObjReq);
    }



}

