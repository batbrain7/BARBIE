package com.example.mohitkumar.isgw;

/**
 * Created by Dell on 1/21/2017.
 */

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class ChatBotActivity extends AppCompatActivity {

    String ACCESS_TOKEN="67565cd4b0a34c6c82ec141d969541be";


    String action1;
    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;
    String s,sres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = mEditTextMessage.getText().toString();
                //bot
                String response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    return;
                }


                s=message;

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

                            action1 = result.getStringParameter("action");
                            String appliance1 = result.getStringParameter("appliance");
                            String location = result.getStringParameter("location");

                             sres = result.getFulfillment().getSpeech().toString();

                            if(action1.equals("turn on"))
                            {
                                action1 = "on";
                            } else if(action1.equals("turn off")) {
                                action1 = "off";
                            }


                            String URL1 = "http://7d0e6594.ngrok.io/isgw/index.php?action=" + action1 + "&appliance="+appliance1+
                                    "&location=" + location;
                            Log.d("URL",URL1);


                       //     Toast.makeText(getApplicationContext(),"action=" + action1 + "&appliance="+appliance1+
                         //           "&location=" + location, Toast.LENGTH_LONG).show();

                            final RequestQueue requestQueue = Volley.newRequestQueue(ChatBotActivity.this);


                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL1, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("Success","Request made");
                                    requestQueue.stop();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                                    requestQueue.stop();
                                }
                            });

                            requestQueue.add(stringRequest);

                            sendMessage(message);
                            Log.d("RES",sres);
                            mimicOtherMessage(sres);
                            mEditTextMessage.setText("");
                            mListView.setSelection(mAdapter.getCount() - 1);


                            // Log.d("INHERE1",s);

                            //makeJsonObjectRequest();

                        } else  {
                            onPostExecute( aiResponse);

                        }
                        //}
                        //Log.d("INHERE3","NOW HERE");
                    }
                }.execute(aiRequest);


            }

        });
        //checking SD card availablility
        boolean a = isSDCARDAvailable();
        //receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/hari/bots/Hari");
        boolean b = jayDir.mkdirs();
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list("Hari")) {
                    File subdir = new File(jayDir.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    for (String file : assets.list("Hari/" + dir)) {
                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("Hari/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari";
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("Hari", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        String[] args = null;
        mainFunction(args);

    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(sres, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(sres, false, true);
        mAdapter.add(chatMessage);
    }
    //check SD card availability
    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true :false;
    }
    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    //Request and response of user and the bot
    public static void mainFunction (String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: "+request);
        System.out.println("Robot: " + response);
    }

}
