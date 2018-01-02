package com.mobillium.omnicrow;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by oguzhandongul on 01/02/2017.
 */

public class GenerateNotification {

    private static Context currentContext = null;

    private static String packageName = null;

    private static Resources contextResources = null;

    private static Class<?> notificationOpenedClass;

    private static boolean openerIsBroadcast;



    static void fromJsonPayload(Context inContext, boolean restoring, int notificationId, JSONObject pushModel) {

        setStatics(inContext);

        showNotification(notificationId, restoring, pushModel);

    }

    static void setStatics(Context inContext) {
        currentContext = inContext;
        packageName = currentContext.getPackageName();
        contextResources = currentContext.getResources();

//        notificationOpenedClass = ActivitySplash.class;

    }

    private static NotificationCompat.Builder getBaseNotificationCompatBuilder(JSONObject gcmBundle) {
        int notificationIcon = getSmallIconId(gcmBundle);

        int notificationDefaults = 0;


        String message = gcmBundle.optString("message");

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(currentContext).setAutoCancel(true)
                                                                                                .setSmallIcon(notificationIcon) // Small Icon required or notification doesn't display
                                                                                                .setContentTitle(gcmBundle.optString("title"))
                                                                                                .setStyle(new NotificationCompat.BigTextStyle().bigText(gcmBundle.optString("message")))
                                                                                                .setContentText(message)
                                                                                                .setTicker(message);


        try {
            notifBuilder.setLights(0xFFfa3c1a, 2000, 3000);
        } catch (Throwable t) {
            notificationDefaults |= Notification.DEFAULT_LIGHTS;
        } // Can throw if an old android support lib is used or parse error.


        Bitmap largeIcon = getLargeIcon(gcmBundle);
        if (largeIcon != null) {
            notifBuilder.setLargeIcon(largeIcon);
        }

        Bitmap bigPictureIcon = getBitmap(gcmBundle.optString("mediaUrl", null));
        if (bigPictureIcon != null) {
            notifBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bigPictureIcon)
                                                                          .setSummaryText(message));
        }

        if (isSoundEnabled(gcmBundle)) {
            Uri soundUri = getCustomSound(gcmBundle);
            if (soundUri != null) {
                notifBuilder.setSound(soundUri);
            } else {
                notificationDefaults |= Notification.DEFAULT_SOUND;
            }
        }

        notificationDefaults |= Notification.DEFAULT_VIBRATE;

        notifBuilder.setDefaults(notificationDefaults);

        return notifBuilder;
    }


    // Put the message into a notification and post it.
    static void showNotification(int notificationId, boolean restoring, JSONObject gcmBundle) {


//        Handler handler = new Handler(Looper.getMainLooper());
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                ApplicationClass.getEventBus()
//                                .post(new EventNewMessage());
//            }
//        });

        Random random = new Random();

//        String group = gcmBundle.optString("grp", null);
        String group = null;

        NotificationCompat.Builder notifBuilder = getBaseNotificationCompatBuilder(gcmBundle);

//        addNotificationActionButtons(gcmBundle, notifBuilder, notificationId, null);


//        try {
//            addBackgroundImage(gcmBundle, notifBuilder);
//        } catch (Throwable t) {
//            Log.d("ERROR", "Could not set background notification image!", t);
//        }

        if (group != null) {
            PendingIntent contentIntent = getNewActionPendingIntent(random.nextInt(),
                                                                    getNewBaseIntent(notificationId).putExtra("omnicrow_data", gcmBundle.toString())
                                                                                                    .putExtra("grp", group));
            notifBuilder.setContentIntent(contentIntent);
            PendingIntent deleteIntent = getNewActionPendingIntent(random.nextInt(), getNewBaseDeleteIntent(notificationId).putExtra("grp", group));
            notifBuilder.setDeleteIntent(deleteIntent);
            notifBuilder.setGroup(group);

//            createSummaryNotification(restoring, gcmBundle);
        } else {
            PendingIntent contentIntent = getNewActionPendingIntent(random.nextInt(), getNewBaseIntent(notificationId).putExtra("omnicrow_data", gcmBundle.toString()));
            notifBuilder.setContentIntent(contentIntent);
//            PendingIntent deleteIntent = getNewActionPendingIntent(random.nextInt(), getNewBaseDeleteIntent(notificationId));
//            notifBuilder.setDeleteIntent(deleteIntent);
        }

        // Keeps notification from playing sound + vibrating again
        if (restoring) {
            removeNotifyOptions(notifBuilder);
        }

        // NotificationManagerCompat does not auto omit the individual notification on the device when using
        //   stacked notifications on Android 4.2 and older
        // The benefits of calling notify for individual notifications in-addition to the summary above it is shows
        //   each notification in a stack on Android Wear and each one is actionable just like the Gmail app does per email.
        if (group == null || Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            NotificationManagerCompat.from(currentContext)
                                     .notify(notificationId, notifBuilder.build());
        }
    }


    private static void removeNotifyOptions(NotificationCompat.Builder builder) {
        builder.setDefaults(0)
               .setSound(null)
               .setVibrate(null)
               .setTicker(null);
    }

    private static PendingIntent getNewActionPendingIntent(int requestCode, Intent intent) {
        if (openerIsBroadcast) {
            return PendingIntent.getBroadcast(currentContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return PendingIntent.getActivity(currentContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Intent getNewBaseIntent(int notificationId) {
        Intent intent = new Intent(currentContext, notificationOpenedClass).putExtra("notificationId", notificationId);

        if (openerIsBroadcast) {
            return intent;
        }
        return intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    private static Intent getNewBaseDeleteIntent(int notificationId) {
        Intent intent = new Intent(currentContext, notificationOpenedClass).putExtra("notificationId", notificationId)
                                                                           .putExtra("dismissed", true);

        if (openerIsBroadcast) {
            return intent;
        }
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }

    // Keep 'throws Throwable' as 'push_bgimage_notif_layout' may not be available
    //    This maybe the case if a jar is used instead of an aar.

    private static boolean isValidResourceName(String name) {
        return (name != null && !name.matches("^[0-9]"));
    }

    private static Bitmap getLargeIcon(JSONObject gcmBundle) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return null;
        }

        Bitmap bitmap = getBitmap(gcmBundle.optString("mediaUrl"));
        if (bitmap == null) {
            bitmap = getBitmapFromAssetsOrResourceName("ic_launcher");
        }

        if (bitmap == null) {
            return null;
        }

        // Resize to prevent extra cropping and boarders.
        try {
            int systemLargeIconHeight = (int) contextResources.getDimension(android.R.dimen.notification_large_icon_height);
            int systemLargeIconWidth = (int) contextResources.getDimension(android.R.dimen.notification_large_icon_width);
            int bitmapHeight = bitmap.getHeight();
            int bitmapWidth = bitmap.getWidth();

            if (bitmapWidth > systemLargeIconWidth || bitmapHeight > systemLargeIconHeight) {
                int newWidth = systemLargeIconWidth, newHeight = systemLargeIconHeight;
                if (bitmapHeight > bitmapWidth) {
                    float ratio = (float) bitmapWidth / (float) bitmapHeight;
                    newWidth = (int) (newHeight * ratio);
                } else if (bitmapWidth > bitmapHeight) {
                    float ratio = (float) bitmapHeight / (float) bitmapWidth;
                    newHeight = (int) (newWidth * ratio);
                }

                return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            }
        } catch (Throwable t) {
        }

        return bitmap;
    }

    private static Bitmap getBitmapFromAssetsOrResourceName(String bitmapStr) {
        try {
            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream(currentContext.getAssets()
                                                                  .open(bitmapStr));
            } catch (Throwable t) {
            }

            if (bitmap != null) {
                return bitmap;
            }

            final List<String> image_extensions = Arrays.asList(".png", ".webp", ".jpg", ".gif", ".bmp");
            for (String extension : image_extensions) {
                try {
                    bitmap = BitmapFactory.decodeStream(currentContext.getAssets()
                                                                      .open(bitmapStr + extension));
                } catch (Throwable t) {
                }
                if (bitmap != null) {
                    return bitmap;
                }
            }

            int bitmapId = getResourceIcon(bitmapStr);
            if (bitmapId != 0) {
                return BitmapFactory.decodeResource(contextResources, bitmapId);
            }
        } catch (Throwable t) {
        }

        return null;
    }

    private static Bitmap getBitmapFromURL(String location) {
        try {
            return BitmapFactory.decodeStream(new URL(location).openConnection()
                                                               .getInputStream());
        } catch (Throwable t) {
        }

        return null;
    }

    private static Bitmap getBitmap(String name) {
        if (name == null) {
            return null;
        }
        if (name.startsWith("http://") || name.startsWith("https://")) {
            return getBitmapFromURL(name);
        }

        return getBitmapFromAssetsOrResourceName(name);
    }

    private static int getResourceIcon(String iconName) {
        if (!isValidResourceName(iconName)) {
            return 0;
        }

        int notificationIcon = getDrawableId(iconName);
        if (notificationIcon != 0) {
            return notificationIcon;
        }

        // Get system icon resource
        try {
            return android.R.drawable.class.getField(iconName)
                                           .getInt(null);
        } catch (Throwable t) {
        }

        return 0;
    }

    private static int getSmallIconId(JSONObject gcmBundle) {
        int notificationIcon = getResourceIcon("vitrinova_noti");
        if (notificationIcon != 0) {
            return notificationIcon;
        }

        notificationIcon = getDrawableId("img_logo_image");
        if (notificationIcon != 0) {
            return notificationIcon;
        }

        return android.R.drawable.ic_popup_reminder;
    }

    private static int getDrawableId(String name) {
        return contextResources.getIdentifier(name, "drawable", packageName);
    }


    private static boolean isSoundEnabled(JSONObject gcmBundle) {
        String sound = gcmBundle.optString("sound", null);
        if ("null".equals(sound) || "nil".equals(sound)) {
            return false;
        }
        return true;
    }

    private static Uri getCustomSound(JSONObject gcmBundle) {
        int soundId;
        String sound = gcmBundle.optString("sound", "");
        if (!TextUtils.isEmpty(sound) && sound.contains(".")) {
            String[] str = sound.split(".");
            sound = str.length < 1 ? "vitrinova" : str[0];
        }

        if (isValidResourceName(sound)) {
            soundId = contextResources.getIdentifier(sound, "raw", packageName);
            if (soundId != 0) {
                return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + soundId);
            }
        }

        soundId = contextResources.getIdentifier("vitrinova", "raw", packageName);
        if (soundId != 0) {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + soundId);
        }

        return null;
    }


    static String getManifestMeta(Context context, String metaName) {
        try {
            ApplicationInfo ai = context.getPackageManager()
                                        .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(metaName);
        } catch (Throwable t) {
            Log.d("ERROR", "", t);
        }

        return null;
    }

}
