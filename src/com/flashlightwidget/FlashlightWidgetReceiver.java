package com.flashlightwidget;

import com.flashlightwidget.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

public class FlashlightWidgetReceiver extends BroadcastReceiver 
{
	private static boolean isLightOn = false;
    private static Camera camera;

	@Override
	public void onReceive(Context context, Intent intent) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.flashlight_widget);

        if(isLightOn) {
                views.setImageViewResource(R.id.btnSwitch, R.drawable.off);
        } else {
                views.setImageViewResource(R.id.btnSwitch, R.drawable.on);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(new ComponentName(context, FlashlightWidget.class),
                                                                         views);

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
	
	private void NotifyFlashlight(Context context, boolean isLightOn)
	{
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int id = 45;
		if(isLightOn)
		{
			Intent receiver = new Intent(context, FlashlightWidgetReceiver.class);
	        receiver.setAction("COM_FLASHLIGHT");
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, 0);
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.icon)
			        .setContentTitle("Flashlight is on")
			        .setContentText("Touch to turn the flashlight off")
			        .setContentIntent(pendingIntent);
			
			manager.notify(id, mBuilder.build());
		}
		else
		{
			manager.cancel(id);
		}
	}
}
