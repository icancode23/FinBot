package com.example.nipunarora.voiceactions;

// tts-Text to Speech

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FinBot extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextToSpeech tts;
    HashMap<String,String> replies;
    ImageButton spk;
    String current_query;
    TextView Reply;
    ImageView graph;
    private static final int SPEECH_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_bot);
        //toolbar disabled for now
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(Boolean.FALSE);
*/

        //********************************* Defining the variables ****************
        tts=(TextToSpeech)new TextToSpeech(this,this);
        replies=new HashMap<String, String>();
        replies.put("Salary","You were last paid on 22nd February 2017 with an amount of Rupees 50,000 By Amazon");
        replies.put("Spending","Here is the Breakup of your spending from the last 30 days.");
        replies.put("Homeloan","You have 85 percent chances of getting a loan of 70 lacs.Your loan percentage was calculated using behaviour score");
        replies.put("Atm","Your nearest ATM is Yes bank ATM,Nanak Pura which is 2.5 km away.Do you want me to guide you through GPS ?");
        spk=(ImageButton)findViewById(R.id.btnSpeak);
        Reply=(TextView)findViewById(R.id.txtSpeechInput);
        graph=(ImageView)findViewById(R.id.graphicalrepresentation);
        //********************** Defination End ************************************//


        //*********************** Add Onclick listeners ********************//
        spk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        });

    }
    /////************************************* onInit Text to speech*********************/
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //on compatible language you can facilitate your operations related to tts
                ;


            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }
    //************************************ end init **************************/

    //**************************************** I/O operations of Finbot ********************/
    public void listen() {
        Log.d( "MAIN ACTIVITY","displaySpeechRecognizer:Worked ");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi");//the code "hi" denotes hindi
        //disable the speaking button for now and enable when entire parsing and tts processing is done
        spk.setEnabled(Boolean.FALSE);
        flushDisplay();
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.

    private void speakOut(String text_to_be_spoken) {
        Reply.setText(text_to_be_spoken);
        tts.speak(text_to_be_spoken, TextToSpeech.QUEUE_FLUSH, null,null);
        //enable the speaking button
        spk.setEnabled(Boolean.TRUE);


    }
    private void flushDisplay()
    {
        Reply.setText("");
        graph.setImageDrawable(null);
    }
    //*********************** End of I/O Finbot ***************//

    /****************************** Parsing Input Output **************************/

    // Get result from Speech Recognition activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            current_query=spokenText;
            //Toast.makeText(this,"You Said "+spokenText,Toast.LENGTH_SHORT).show();
            Log.d("you said",spokenText);
            analyseUserInput(current_query);


            /*t2.setText(spokenText);*/


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //Analyse User input (NLP) Hard Coded for now******
    void analyseUserInput(String current_query)
    {

       if (current_query.equals("what was my spending for last month"))
       {
           speakOut(replies.get("Spending"));
           showGraphics(1);
       }
       else {
           if (current_query.equals( "when did I get last paid")) {
               speakOut(replies.get("Salary"));
           } else {
               if (current_query.equals( "what are my chances of getting a home loan of 70 lacs with my current financial status")) {
                   speakOut(replies.get("Homeloan"));
                   showGraphics(2);
               } else {
                   if (current_query.equals( "which is the nearest ATM to me")) {
                       speakOut(replies.get("Atm"));
                       showGraphics(3);
                   } else {
                       Toast.makeText(this, "Nothing Matched", Toast.LENGTH_SHORT).show();
                       spk.setEnabled(Boolean.TRUE);
                   }
               }
           }

       }

    }
    void showGraphics(int condition_code){
        //**************** show the suitable graphs ******************/
        //hard coded for now
        switch (condition_code)
        {
            case 1:graph.setImageResource(R.drawable.spendinggraph);
                break;
            case 2:graph.setImageResource(R.drawable.loan);
                break;
            case 3:graph.setImageResource(R.drawable.map);
                break;
            default:break;
        }

    }


    //************************* End of Parsing ********************/

    ///// we need to stop the text to speech at the onDestroy of The Activity
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


}
