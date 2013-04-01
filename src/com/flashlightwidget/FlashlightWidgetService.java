package com.flashlightwidget;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

public class FlashlightWidgetService extends Service {

	private static boolean isLightOn = false;
    private static Camera camera;
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean initializing = intent.getBooleanExtra("initializing", false);
		Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] allWidgetIds = intent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        
        for (int widgetId : allWidgetIds) 
        {
        	RemoteViews views = new RemoteViews(context.getPackageName(),
        	          R.layout.flashlight_widget);
        	
        	if(isLightOn || initializing) {
                views.setImageViewResource(R.id.btnSwitch, R.drawable.off);
	        } else {
	            views.setImageViewResource(R.id.btnSwitch, R.drawable.on);
	        }
            views.setOnClickPendingIntent(R.id.btnSwitch, GetClickPendingIntent(context));
            appWidgetManager.updateAppWidget(widgetId, views);
		}
       
        if(!initializing)
        {
	        if (isLightOn) {
	            if (camera != null) {
	                    camera.stopPreview();
	                    camera.release();
	                    camera = null;
	            }
	            isLightOn = false;
	            NotifyFlashlight(context, isLightOn);
	        } else {
	                // Open the default i.e. the first rear facing camera.
	                camera = Camera.open();
	
	                if(camera == null) {
	                        Toast.makeText(context, R.string.no_camera, Toast.LENGTH_SHORT).show();
	                } else {
	                        // Set the torch flash mode
	                        Parameters param = camera.getParameters();
	                        param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
	                        try {
	                                camera.setParameters(param);
	                                camera.startPreview();
	                                isLightOn = true;
	                                NotifyFlashlight(context, isLightOn);
	                        } catch (Exception e) {
	                                Toast.makeText(context, R.string.no_flash, Toast.LENGTH_SHORT).show();
	                        }
	                }
	        }
        }
        
		stopSelf();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private PendingIntent GetClickPendingIntent(Context context)
	{
		ComponentName thisWidget = new ComponentName(context,
		        FlashlightWidget.class);
		    int[] allWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(thisWidget);

            Intent clickIntent = new Intent(context,
                FlashlightWidget.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                allWidgetIds);
            clickIntent.putExtra("initializing", false);
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
	}
	
	private void NotifyFlashlight(Context context, boolean isLightOn)
	{
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int id = 45;
		if(isLightOn)
		{
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.icon)
			        .setContentTitle("Flashlight is on")
			        .setContentText("Touch to turn the flashlight off")
			        .setContentIntent(GetClickPendingIntent(context));
			
			manager.notify(id, mBuilder.build());
		}
		else
		{
			manager.cancel(id);
		}
	}
}
