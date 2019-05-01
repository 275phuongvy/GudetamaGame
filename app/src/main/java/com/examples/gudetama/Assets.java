package com.examples.gudetama;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Assets {
    static Bitmap background;
    static Bitmap gudetama1;
    static Bitmap gudetama2;
    static Bitmap eating1;
    static Bitmap eating2;
    static Bitmap prefs;
    static Bitmap food;
    static Bitmap egg;
    static volatile boolean serviceIsProcessing; // True when the service is processing a wait timer
    static boolean gudetama_eating;
    static float totalHappyTime;
    static boolean gudetama_hungry;
    static boolean happy_sound_playing;
    static boolean egg_sound_playing;
    static boolean hungry_sound_playing;
    static boolean happytime_added;
    static boolean happytime_zero;
    static float timeWhenHungry;
    static int count;


    //States of game
    enum GameState {
        Starting,
        EggState,
        beforeEating,
        afterEating,
        feelingFull,
        AdultState,
    };
    static GameState state; //current state of the game
    static float gameTimer; //in second
    static float eatTimer;
    static int theState;

    static MediaPlayer mp;

    static SoundPool soundPool;
    static int sound_crack;
    static int sound_happy;
    static int sound_hungry;



}
