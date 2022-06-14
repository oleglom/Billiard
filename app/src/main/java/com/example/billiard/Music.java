package com.example.billiard;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Button;
import android.widget.SeekBar;

public class Music {
    int volume = 50;
    MediaPlayer mPlayer;
    Button playButton, pauseButton, stopButton;
    SeekBar volumeControl;
    AudioManager audioManager;
    MainActivity main;
    public Music(MainActivity main, AudioManager audio){
        this.audioManager = audio;
        this.main = main;
        mPlayer= MediaPlayer.create(main, R.raw.urma);
    }
    void hurt(){
        mPlayer.stop();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        mPlayer= MediaPlayer.create(main, R.raw.urma);
        mPlayer.start();
    }
    void sd(){
        mPlayer.stop();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        mPlayer= MediaPlayer.create(main, R.raw.soud);
        mPlayer.start();
    }
    void in_luze(){
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        mPlayer= MediaPlayer.create(main, R.raw.in_luze);
        mPlayer.start();
    }




}
