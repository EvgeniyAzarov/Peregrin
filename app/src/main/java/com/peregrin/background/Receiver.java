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
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.peregrin.DBHelper;
import com.peregrin.R;
import com.peregrin.ServerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;


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

                        ResultSet messages = (ResultSet) inputStream.readObject();
                        outputStream.writeBoolean(true);
                        ContentValues cv = new ContentValues();

                        while (!messages.next()) {
                            cv.put("sender_login", messages.getString("sender_login"));
                            cv.put("recipient_login", messages.getString("recipient_login"));
                            cv.put("content", messages.getString("content"));
                            db.insert("messages", null, cv);
                        }

                        outputStream.writeBoolean(true);

                        Context context = getApplicationContext();

                        Intent notificationIntent = new Intent(context, Receiver.class);
                        PendingIntent contentIntent = PendingIntent.getService(context,
                                0, notificationIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT);

                        Resources res = context.getResources();
                        Notification.Builder builder = new Notification.Builder(context);

                        builder.setContentIntent(contentIntent)
                                .setSmallIcon(R.mipmap.ic_toolbar)
                                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher_round))
                                .setWhen(System.currentTimeMillis())
                                .setAutoCancel(true)
                                .setContentTitle("Liza")
                                .setContentText(messages.getString("content"));

                        Notification notification = builder.build();

                        NotificationManager notificationManager = (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(NOTIFY_ID, notification);

                    } catch (IOException | SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                db.close();
            }
        }).start();
        dbHelper.close();

        return super.onStartCommand(intent, flags, startId);
    }
}
