package com.flashlightwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class FlashlightWidget extends AppWidgetProvider {
	
	private boolean initializing = true;
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) 
	{
	    ComponentName thisWidget = new ComponentName(context,
	        FlashlightWidget.class);
	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	
	    // Build the intent to call the service
	    Intent intent = new Intent(context.getApplicationContext(),
	        FlashlightWidgetService.class);
	    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
	    intent.putExtra("initializing", initializing);
	    // Update the widgets via the service
	    context.startService(intent);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		initializing = intent.getBooleanExtra("initializing", true);
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}
}
