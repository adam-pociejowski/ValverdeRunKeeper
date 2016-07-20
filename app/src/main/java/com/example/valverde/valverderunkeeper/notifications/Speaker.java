package com.example.valverde.valverderunkeeper.notifications;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class Speaker {
    private static final Locale SPEAK_LANGUAGE = Locale.UK;
    private boolean isInitialized = false;
    private TextToSpeech tts;

    public Speaker(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    tts.setLanguage(SPEAK_LANGUAGE);
                    Log.i("Speaker", "Initialization success");
                    isInitialized = true;
                }
                else {
                    Log.e("Speaker", "Initialization failed");
                }
            }
        });
    }

    public void speak(String text) {
        if (isInitialized) {
            Log.i("Speaker", "speak: "+text);
            while (tts.isSpeaking());
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
        else {
            Log.e("Speaker", "Speaker isn't initialized yet");
        }

    }

    public void close() {
        tts.shutdown();
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
