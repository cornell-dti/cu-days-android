package org.cornelldti.cudays;

import android.app.Application;
import android.content.Intent;

import net.danlew.android.joda.JodaTimeAndroid;

import org.cornelldti.cudays.util.Settings;

/**
 * The entry point into the app. Initialize all singletons, static variables, or anything that should only
 * run once when the app launches here.
 */
public class MainApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		JodaTimeAndroid.init(this);
		UserData.loadData(this);

		//launch initial settings
		if (Settings.isFirstLaunch(this))
		{
			Intent intent = new Intent(this, InitialActivity.class);
			startActivity(intent);
		}
	}
}
