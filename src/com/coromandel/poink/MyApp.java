package com.coromandel.poink;

import org.jraf.android.util.activitylifecyclecallbackscompat.ActivityLifecycleCallbacksCompat;
import org.jraf.android.util.activitylifecyclecallbackscompat.ApplicationHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.os.Bundle;


public class MyApp extends Application {



@SuppressLint("NewApi")
@Override
   public void onCreate() {
       super.onCreate();
       
       ApplicationHelper.registerActivityLifecycleCallbacks(this,new ActivityLifecycleCallbacksCompat(){
           @Override
           public void onActivityCreated(Activity activity, Bundle bundle) {
           }

       @Override
       public void onActivityStarted(Activity activity) {
       }

       @Override
       public void onActivityResumed(Activity activity) {
       }

       @Override
       public void onActivityPaused(Activity activity) {
       }

       @Override
       public void onActivityStopped(Activity activity) {
       }

       @Override
       public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
       }

       @Override
       public void onActivityDestroyed(Activity activity) {

               NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
               mNM.cancelAll();
               if(Globals.wakelock!=null && Globals.wakelock.isHeld())
               {
            	   Globals.wakelock.release();
               }

       }
   });
}


}