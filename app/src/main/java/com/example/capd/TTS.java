package com.example.capd;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;



public class TTS {

    public Context mContext;
    public TextToSpeech tts;

    // tts 생성
    public TTS(Context context){
        this.mContext = context;
        initTTS(this.mContext);
    }


    // tts 초기화
    public void initTTS(Context context){

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.KOREAN);
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                        Log.e("TTS", "지원되지 않는 언어");
                    }
                }
            }
        });
    }

    public void startTTS(String text){
        startTTS(text, 1.0f, 1.0f, false);
    }

    // tts 실행
    public void startTTS(String text, float speed, float pitch, Boolean is_auto_close){

        if (tts==null){
            initTTS(this.mContext);
        }

        tts.setPitch(pitch);
        tts.setSpeechRate(speed);
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);

        if (is_auto_close){
            close();
        }
    }

    // tts 종료
    public void close() {
        if (tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }


}
