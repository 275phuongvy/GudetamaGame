package com.examples.gudetama;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/*___________________________________________________________________
|
| Class: GatorService
|
| Description: Service to handle audio media playback.  Waits for Intents
|	from TexclamationIntentReceiver (a broadcast receiver that receives
|	intents from the OS).
|__________________________________________________________________*/
    public class GudetamaService extends MyIntentService
    {
//        boolean ui_onscreen;
        // Intent actions that can be handled.  Constants created here for convenience.
        // What actually defines the actions the service can handle are the <action> tags in the <intent-filters> tag for the service in AndroidManifest.xml
        public static final String ACTION_KILL_NOTIFICATION = "com.examples.gudetama.KILL_NOTIFICATION";
        public static final String ACTION_START	= "com.examples.gator.START";
        public static final String ACTION_FOOD = "com.examples.gudetama.FOOD";
//        public static final String ACTION_UI_ONSCREEN = "com.examples.gudetama.UI_ONSCREEN";
//        public static final String ACTION_UI_OFFSCREEN = "com.examples.gudetama.UI_OFFSCREEN";

        // The ID we use for the notification (the onscreen alert that appears at the notification
        // area at the top of the screen as an icon -- and as text as well if the user expands the
        // notification area).
        final int NOTIFICATION_ID = 1;

        //AudioManager mAudioManager;
        NotificationManager mNotificationManager;

        Notification mNotification = null;


        /*___________________________________________________________________
        |
        | Function: GatorService (Constructor)
        |__________________________________________________________________*/
        public GudetamaService ()
        {
            super ("GudetamaService");
        }

        /*___________________________________________________________________
        |
        | Function: onCreate
        |__________________________________________________________________*/
        @Override
        public void onCreate () {
            super.onCreate();
            // Get handles to system services
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //mAudioManager        = (AudioManager)        getSystemService (AUDIO_SERVICE);
            Assets.serviceIsProcessing = false;
//            ui_onscreen = true;
        }

        /*___________________________________________________________________
        |
        | Function: onHandleIntent
        |__________________________________________________________________*/
        @Override
        protected void onHandleIntent(Intent intent)
        {
            String action = intent.getAction ();
            //	Log.i (TAG, "onHandleIntent(): " + action);

            if (action.equals(ACTION_KILL_NOTIFICATION)) {
//                ui_onscreen = true;
                processStopNotify ();
            }
            else if (action.equals(ACTION_START)) {
                processStart ();
            }
            else if (action.equals(ACTION_FOOD)) {
//                ui_onscreen = true;
                processFood();
            }
//            else if (action.equals(ACTION_UI_OFFSCREEN)) {
//                ui_onscreen = false;
//            }
//            else if (action.equals(ACTION_UI_ONSCREEN)) {
//                ui_onscreen = true;
 //           }

        }

        /*___________________________________________________________________
        |
        | Function: onDestroy
        |__________________________________________________________________*/
        @Override
        public void onDestroy ()
        {
            // Service is being killed, so release resources used here, if any
            mNotificationManager = null;

            // Call super class version
            super.onDestroy();
        }

        /*___________________________________________________________________
    |
    | Function: processStart
    |__________________________________________________________________*/
        private void processStart ()
        {
            float currTime;

            // If service is already processing, then just exit
//            if (Assets.serviceIsProcessing) {
//                Log.i("ProjectLogging", "service is already processing");
//                return;
//            }

            // Busy wait until time when hungry is reached
            Assets.serviceIsProcessing = true;
            do {
                currTime = System.nanoTime() / 1000000000f;
            } while (currTime < Assets.timeWhenHungry & Assets.gudetama_hungry);
            Assets.serviceIsProcessing = false;

            // Show a notification - this method worked in Android pre-Lolipop - no longer works in Lolipop
            //PendingIntent pi = PendingIntent.getActivity (getApplicationContext(), 0, new Intent(getApplicationContext(), AppGator.class), PendingIntent.FLAG_UPDATE_CURRENT);
            //mNotification.setLatestEventInfo (getApplicationContext(), "Gator", "Feed me!", pi);
            //mNotificationManager.notify (NOTIFICATION_ID, mNotification);

            // Prepare intent which is triggered if the notification is selected
            Intent intent = new Intent(this, MainActivity.class);
            //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Build the notification
            //  for the addAction re-use the same intent to keep the example short
            Notification n = new Notification.Builder(this)
                    .setContentTitle("Gudetama")
                    .setContentText("Feed me!")
                    .setSmallIcon(R.drawable.icon)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .build();
            // Launch the notification
            mNotificationManager.notify(NOTIFICATION_ID, n);
        }

        /*___________________________________________________________________
        |
        | Function: processStopNotify
        |__________________________________________________________________*/
        private void processStopNotify ()
        {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }

        //Funtion: processFood
        private void processFood() {
            Assets.state = Assets.GameState.beforeEating;
        }

        // Not using but needed so stub this out (return null)
        @Override
        public IBinder onBind(Intent intent) { return null; }
    }



