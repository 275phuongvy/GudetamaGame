package com.examples.gudetama;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Cast state to type int
        if (Assets.state == Assets.GameState.Starting)
            Assets.theState = 0;  // 0 represents the state GameStateStarting;
        else if (Assets.state == Assets.GameState.EggState)
            Assets.theState = 1;
        else if (Assets.state == Assets.GameState.beforeEating)
            Assets.theState = 2;
        else if (Assets.state == Assets.GameState.afterEating)
            Assets.theState = 3;
        else if (Assets.state == Assets.GameState.feelingFull)
            Assets.theState = 4;
        else if (Assets.state == Assets.GameState.AdultState)
            Assets.theState = 5;

        if (Assets.theState ==0)
            Assets.state = Assets.GameState.Starting;
        else if (Assets.theState ==1)
            Assets.state = Assets.GameState.EggState;
        else if (Assets.theState == 2)
            Assets.state = Assets.GameState.beforeEating;
        else if (Assets.theState == 3)
            Assets.state = Assets.GameState.afterEating;
        else if (Assets.theState == 4)
            Assets.state = Assets.GameState.feelingFull;
        else if (Assets.theState == 5)
            Assets.state = Assets.GameState.AdultState;


        // Disable the title
        //requestWindowFeature (Window.FEATURE_NO_TITLE);  // use the styles.xml file to set no title bar
        // Make full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new MyView(this));

        //SoundCloud
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Assets.soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        else {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            Assets.soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .build();

        }
        Assets.sound_crack = Assets.soundPool.load(this, R.raw.cracking, 1);
        Assets.sound_happy = Assets.soundPool.load(this, R.raw.happy, 1);
        Assets.sound_hungry = Assets.soundPool.load(this, R.raw.hungry, 1);
    }
    public class MyView extends View {
        Bitmap bmp;
        int x, y;
        Rect prefsButton;
        Rect foodButton;
        boolean prefsClicked;
        boolean foodClicked;
        boolean initialized;

        public MyView(Context context) {
            super(context);
            bmp = null;
            Assets.food = null;
            Assets.prefs = null;
            Assets.totalHappyTime = 0;
            Assets.count = 0;
            x=0;
            y=0;
            prefsClicked = false;
            foodClicked = false;
            initialized = false;
            Assets.gudetama_eating = false;
//            Assets.state = Assets.GameState.Starting;
            Assets.happy_sound_playing = false;
            Assets.egg_sound_playing = false;
            Assets.hungry_sound_playing = false;
            Assets.happytime_added = false;
            Assets.happytime_zero = false;
        }

        //Load background
        private void loadBackground (Canvas canvas, int resId) {
            // Load background
            Bitmap bmp = BitmapFactory.decodeResource (this.getResources(), resId);
            // Scale it to fill entire canvas
            Assets.background = Bitmap.createScaledBitmap (bmp, canvas.getWidth(), canvas.getHeight(), false);
            // Delete the original
            bmp = null;
        }

        @Override
        protected void onDraw(Canvas canvas) {
        //    float currentTime = System.nanoTime() / 1000000000f;

            switch (Assets.state) {
                case Starting:
                    Assets.gameTimer = System.nanoTime() / 1000000000f;
                    Assets.state = Assets.GameState.EggState;
                    invalidate();
                    break;
                case EggState:
                    loadBackground (canvas, R.drawable.background);
                    // Draw the background screen
                    canvas.drawBitmap (Assets.background, 0, 0, null);
                    if (!Assets.egg_sound_playing) {
                        Assets.soundPool.play(Assets.sound_crack, 1, 1, 1, -1, 1);
                        Assets.egg_sound_playing = true;
                    }
                    //Draw Gudetama Egg
                    Assets.egg = BitmapFactory.decodeResource(getResources(), R.drawable.egg1);
                    x = (x + 3) % canvas.getWidth();
                    y = (y + 3) % canvas.getHeight();
                    canvas.drawBitmap(Assets.egg, x/2, y/2, null);

                    float currentTime = System.nanoTime() / 1000000000f;
                    if (currentTime - Assets.gameTimer >= 10) {
                        // Goto next state
                        Assets.egg = null;
                        Assets.soundPool.stop(Assets.sound_crack);
                        Assets.state = Assets.GameState.AdultState;
                        Assets.totalHappyTime = Assets.gameTimer + 20;
                    }
                    invalidate();
                    break;
                case beforeEating:
                    //Load bitmap
                    canvas.drawBitmap (Assets.background, 0, 0, null);
                    Assets.eatTimer = System.nanoTime() / 1000000000f;
                    Assets.count++;
                    if (Assets.count >= 6) {
                        if (!Assets.gudetama_hungry) {
                            Assets.state = Assets.GameState.feelingFull;
                        }
                        else {
                            Assets.count = 0;
                            Assets.state = Assets.GameState.afterEating;
                        }
                    }
                    else
                        Assets.state = Assets.GameState.afterEating;
                    invalidate();
                    break;
                case afterEating:
                    //Load bitmap
                    canvas.drawBitmap (Assets.background, 0, 0, null);
                    Assets.gudetama_eating = true;

                    if (!Assets.happytime_added) {
                        Assets.totalHappyTime = Assets.totalHappyTime + 20;

                        Assets.happytime_added = true;
                    }

                    //Draw gudetama eating
//                    if (Assets.count <6) {
                        long foodMove = System.currentTimeMillis() / 100 % 10;
                        if (foodMove % 2 == 0) {
                            Assets.eating1 = BitmapFactory.decodeResource(getResources(), R.drawable.eating1);
                            canvas.drawBitmap(Assets.eating1, canvas.getWidth() / 2 - Assets.eating1.getWidth() / 2, canvas.getHeight() / 2 - Assets.eating1.getHeight() / 2, null);
                        } else {
                            Assets.eating2 = BitmapFactory.decodeResource(getResources(), R.drawable.eating2);
                            canvas.drawBitmap(Assets.eating2, canvas.getWidth() / 2 - Assets.eating2.getWidth() / 2, canvas.getHeight() / 2 - Assets.eating2.getHeight() / 2, null);
                        }

                    float curTime = System.nanoTime() / 1000000000f;
                    if (curTime - Assets.eatTimer >= 5) {
                        Assets.gudetama_eating = false;
                        Assets.happytime_added = false;
                        Assets.state = Assets.GameState.AdultState;
                    }
                    invalidate();
                    break;

                case feelingFull:
                        Assets.gudetama_eating = false;
                        Toast.makeText(MainActivity.this, "Oops, Gudetama is already full!", Toast.LENGTH_SHORT).show();
                        Assets.state = Assets.GameState.AdultState;

                case AdultState:
                    //Load Bitmap
                    canvas.drawBitmap (Assets.background, 0, 0, null);
                    //Prefs button
                    if (Assets.prefs == null)
                        Assets.prefs = BitmapFactory.decodeResource(getResources(), R.drawable.prefs);
                    prefsButton = new Rect(canvas.getWidth() - 1 - Assets.prefs.getWidth(), 1, canvas.getWidth() - 1, 1 + Assets.prefs.getHeight());
                    canvas.drawBitmap(Assets.prefs, null, prefsButton, null);
                    if (prefsClicked) {
                        prefsClicked = false;
                        startActivity(new Intent(MainActivity.this, PrefsActivity.class));
                    }

                    //Food button
                    if (Assets.food == null)
                        Assets.food = BitmapFactory.decodeResource(getResources(), R.drawable.food);
                    foodButton = new Rect(1, 1, 1 + Assets.food.getWidth(), 1 + Assets.food.getHeight());
                    if (!Assets.gudetama_eating)
                        canvas.drawBitmap(Assets.food, null, foodButton, null);
                    if (foodClicked & !Assets.gudetama_eating) {
                        foodClicked = false;
                        Intent intent = new Intent(MainActivity.this, GudetamaService.class);
                        intent.setAction(GudetamaService.ACTION_FOOD);
                        startService(intent);
                    }
                    else
                        foodClicked = false;

                    //Draw Happy and hungry Gudetama
                    float hungryTime = System.nanoTime() / 1000000000f;
                    if (!Assets.gudetama_eating) {
                        if (hungryTime <= Assets.totalHappyTime) {
                            //Gudetama Happy
                            Assets.gudetama_hungry = false;
                            // stop playing hungry sound
                            Assets.soundPool.pause(Assets.sound_hungry);
                            Assets.hungry_sound_playing = false;
                            // start playing happy sound
                            if (!Assets.happy_sound_playing) {
                                Assets.soundPool.play(Assets.sound_happy, 1, 1, 1, 0, 1);
                                Assets.happy_sound_playing = true;
                            }

                            x = (x + 4) % canvas.getWidth();
                            y = (y + 4) % canvas.getHeight();

                            long happyMove = System.currentTimeMillis() / 100 %10;
                            if (happyMove % 2 == 0) {
                                Assets.gudetama1 = BitmapFactory.decodeResource(getResources(), R.drawable.walking1);
                                canvas.drawBitmap(Assets.gudetama1, x / 2, y / 2, null);

                            } else {
                                Assets.gudetama2 = BitmapFactory.decodeResource(getResources(), R.drawable.walking2);
                                canvas.drawBitmap(Assets.gudetama2, x / 2, y / 2, null);
                            }
                        }
                        else {
                            //Gudetama Hungry
                            if (!Assets.happytime_zero) {
                                Assets.totalHappyTime -=20;
                                Assets.happytime_zero = true;
                            }
                            Assets.gudetama_hungry = true;
                            // stop playing happy sound
                            Assets.soundPool.pause(Assets.sound_happy);
                            Assets.happy_sound_playing = false;
                            // start playing hungry sound
                            if (!Assets.hungry_sound_playing) {
                                Assets.soundPool.play(Assets.sound_hungry, 1, 1, 1, 0, 1);
                                Assets.hungry_sound_playing = true;
                            }
                            x = (x + 1) % canvas.getWidth();
                            y = (y + 1) % canvas.getHeight();
                            long hungryMove = System.currentTimeMillis() / 100 % 10;
                            if (hungryMove % 2 == 0) {
                                Assets.gudetama1 = BitmapFactory.decodeResource(getResources(), R.drawable.walking1);
                                canvas.drawBitmap(Assets.gudetama1, x / 2, y / 2, null);
                            } else {
                                Assets.gudetama2 = BitmapFactory.decodeResource(getResources(), R.drawable.walking2);
                                canvas.drawBitmap(Assets.gudetama2, x / 2, y / 2, null);
                            }
                        }
                    }
                    invalidate();
                    break;
                }
            }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // On click set flag to switch to main (game) activity
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (prefsButton.contains((int)event.getX(), (int)event.getY())) {
                    prefsClicked = true;
                }

                if (foodButton.contains((int)event.getX(), (int)event.getY())) {
                    foodClicked = true;
                }
            }
            return super.onTouchEvent(event); // to indicate we have handled this event
        }

    }


    @Override
    protected void onResume () {

        //Assets.state = Assets.GameState.Starting;

        // Call super class version
        super.onResume ();

        Assets.mp = null;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Retrieve the state from the shared preferences
        Assets.theState = prefs.getInt("key_notify", 0);
        boolean b = prefs.getBoolean("key_music_enabled", true);
        if (b == true) {
            if (Assets.mp != null) {
                Assets.mp.release();
                Assets.mp = null;
            }
            Assets.mp = MediaPlayer.create(this, R.raw.theme);
            Assets.mp.setLooping(true);
            Assets.mp.start();
        }

        Assets.soundPool.stop(Assets.sound_happy);
        Assets.soundPool.stop(Assets.sound_hungry);
        Assets.soundPool.stop(Assets.sound_crack);

        // Kill the notification, if any
        Intent intent = new Intent(this, GudetamaService.class);
        intent.setAction(GudetamaService.ACTION_KILL_NOTIFICATION);
        startService(intent);


    }

    @Override
    protected void onPause () {
        super.onPause ();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key_notify", Assets.theState);
        editor.commit();
        //Retrieve the state from the shared preferences
        Assets.theState = prefs.getInt("key_notify", 0);


            if ( Assets.mp != null) {
                Assets.mp.stop();
                Assets.mp.release();
                Assets.mp = null;
            }

            Assets.soundPool.stop(Assets.sound_happy);
            Assets.soundPool.stop(Assets.sound_hungry);
            Assets.soundPool.stop(Assets.sound_crack);

        if (! isFinishing()) {
            // Allow the gator to get hungry in 5 seconds
            float currTime = System.nanoTime() / 1000000000f;
            Assets.timeWhenHungry = currTime + 5;

            // Use pre-Android 5.0 version?
            //if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            // do things the old way
            //}
            //else {
            // do things the Lolipop way
            //}

            // This is an implicit Intent - this worked in Android 4 and below, no longer works in Android 5
            //startService (new Intent(GatorService.ACTION_START));

            // This is an explicit Intent - use this to start a service in Android 5
            Intent intent = new Intent(this, GudetamaService.class);
            intent.setAction(GudetamaService.ACTION_START);
            startService(intent);
            }
        }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Application")
                .setMessage("Are you sure you want to close?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
