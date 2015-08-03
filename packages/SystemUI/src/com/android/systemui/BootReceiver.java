/*
 * Copyright (C) 2015 The DesolationROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

/**
 * Performs a number of miscellaneous, non-system-critical actions
 * after the system has finished booting.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "SystemUIBootReceiver";
    
    private static String WELCOME_BACK_NOTIFY = "welcome_back_notify" ;
    private static String FIRST_BOOT_NOTIFY = "first_boot_notify" ;
    private static String REBOOT_TITLE = "reboot_title";
	private int mFirstBoot;
	private int mWelcomeBack;
	private int mShowProcess;
	private String mContentTitle;
	private String[] mRebootTitles;
	private int mRebootTitleType;

    @Override
    public void onReceive(final Context context, Intent intent) {
	ContentResolver res = context.getContentResolver();
	mFirstBoot = Settings.System.getIntForUser(res, Settings.System.FIRST_BOOT_NOTIFY, 0, UserHandle.USER_CURRENT);
	mWelcomeBack = Settings.System.getInt(res, Settings.System.WELCOME_BACK_NOTIFY, 1);
	mShowProcess = Settings.Global.getInt(res, Settings.Global.SHOW_PROCESSES, 0);
	mRebootTitleType = Settings.System.getInt(res, Settings.System.REBOOT_TITLE, 1);
	mRebootTitles = context.getResources().getStringArray(R.array.reboot_title_entries);
        try {
            // Start the load average overlay, if activated
            if (mShowProcess != 0) {
                Intent loadavg = new Intent(context, com.android.systemui.LoadAverageService.class);
                context.startService(loadavg);
            }
        } catch (Exception e) {
            Log.e(TAG, "Can't start load average service");
        }
		mContentTitle = mRebootTitles[Integer.valueOf(mRebootTitleType)];
		if (mWelcomeBack != 0) {
			switch (mFirstBoot) {
				case 0:
					FirstBootNotify(context);
					Settings.System.putIntForUser(res, Settings.System.FIRST_BOOT_NOTIFY, 1, UserHandle.USER_CURRENT);
					Log.i(TAG, "Notified for first boot");
					break;
				case 1:
					WelcomeBackNotify(context, mContentTitle);
					Log.i(TAG, "Notified for returning boot");
					break;
			}
		} else {
			Log.i(TAG, "Welcome notifications disabled");
		}
    }
    
    public void FirstBootNotify(Context context) {
        Notification.Builder mBuilder = new Notification.Builder(context)
	        .setSmallIcon(R.drawable.first_boot_notify)
                .setAutoCancel(true)
                .setContentTitle("Welcome to DesolationROM")
                .setContentText("")
		.setStyle(new Notification.InboxStyle()
		.setBigContentTitle("Welcome to DesolationROM")
		.addLine("Build status: "+SystemProperties.get("rom.buildtype"))
		.addLine("Build date: "+SystemProperties.get("ro.build.date"))
		.addLine("Device: "+SystemProperties.get("ro.product.device")));
		final NotificationManager mNotificationManager =
			(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
		Handler h = new Handler();
		long c = 12000;
		h.postDelayed(new Runnable() {
			public void run() {
				mNotificationManager.cancel(1);
			}
		}, c);
    }
	
    public void WelcomeBackNotify(Context context, String contenttitle) {
        Notification.Builder mBuilder = new Notification.Builder(context)
	        .setSmallIcon(R.drawable.first_boot_notify)
                .setAutoCancel(true)
                .setContentTitle("Let's #GetDesolated. Build date: "+SystemProperties.get("ro.build.date"))
                .setContentText(contenttitle);
		final NotificationManager mNotificationManager =
			(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
		Handler h = new Handler();
		long c = 8000;
		h.postDelayed(new Runnable() {
			public void run() {
				mNotificationManager.cancel(1);
			}
		}, c);
    }
}
