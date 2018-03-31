package org.cornelldti.cudays;

import android.app.Application;
import android.content.Intent;

import org.cornelldti.cudays.models.CollegeType;
import org.cornelldti.cudays.models.StudentType;
import org.cornelldti.cudays.util.Settings;

import net.danlew.android.joda.JodaTimeAndroid;

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

		//if the user never filled out his info, he needs to do so.
		if(Settings.getStudentSavedType(this) == StudentType.NOTSET ||  Settings.getStudentSavedCollegeType(this) == CollegeType.NOTSET)
		{
			Intent intent = new Intent(this, InitialSettingsActivity.class);
			startActivity(intent);
		}
	}
}
