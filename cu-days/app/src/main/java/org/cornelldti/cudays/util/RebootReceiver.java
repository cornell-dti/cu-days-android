package org.cornelldti.cudays.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RebootReceiver extends BroadcastReceiver
{
	/**
	 * Runs when device is rebooted. Recreates all notifications.
	 * @param context
	 * @param intent Ignored.
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Notifications.scheduleForEvents(context);
	}
}
