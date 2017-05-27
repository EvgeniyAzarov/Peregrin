package com.peregrin.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.peregrin.DBHelper;
import com.peregrin.R;
import com.peregrin.ServerInfo;
import com.peregrin.activities.ChatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class Receiver extends Service {

    private static final int NOTIFY_ID = 867549;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final DBHelper dbHelper = new DBHelper(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String recipient = getSharedPreferences("user", MODE_PRIVATE).getString("phone", null);

                while (!Thread.currentThread().isInterrupted()) {
                    try (
                            Socket socket = new Socket(ServerInfo.ADDRESS, ServerInfo.PORT);
                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())
                    ) {
                        String[] request = new String[2];
                        request[0] = "GET_MESSAGES";
                        request[1] = recipient;

                        outputStream.writeObject(request);

                        ArrayList<HashMap<String, String>> messages
                                = (ArrayList<HashMap<String, String>>) inputStream.readObject();
                        outputStream.writeBoolean(true);
                        ContentValues cv = new ContentValues();

                        for (int i = 0; i < messages.size(); i++) {
                            cv.put("sender_login", messages.get(i).get("sender_login"));
                            cv.put("recipient_login", messages.get(i).get("recipient_login"));
                            cv.put("content", messages.get(i).get("content"));
                            db.insert("messages", null, cv);
                        }

                        outputStream.writeBoolean(true);


                        if (((ActivityStatusListener) getApplicationContext())
                                .isChatActivityForeground()) {
                            Intent intent = new Intent(ChatActivity.ACTION_UPDATE_CHAT);
                            intent.putExtra("received", true);
                            sendBroadcast(intent);
                        } else {

                            Context context = getApplicationContext();

                            Intent notificationIntent = new Intent(context, ChatActivity.class);
                            PendingIntent contentIntent = PendingIntent.getService(Receiver.this,
                                    0, notificationIntent, 0);

                            Resources res = context.getResources();
                            Notification.Builder builder = new Notification.Builder(context);

                            builder.setContentIntent(contentIntent)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                                    .setWhen(System.currentTimeMillis())
                                    .setAutoCancel(true)
                                    .setContentTitle(messages.get(messages.size() - 1).get("sender_login"))
                                    .setContentText(messages.get(messages.size() - 1).get("content"));

                            Notification notification = builder.build();
                            notification.defaults = Notification.DEFAULT_ALL;

                            NotificationManager notificationManager = (NotificationManager) context
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(NOTIFY_ID, notification);
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        Log.d("peregrin", e.getMessage());
                    }
                }

                db.close();
            }
        }).start();
        dbHelper.close();


        return super.onStartCommand(intent, flags, startId);
    }
}
