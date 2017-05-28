package com.peregrin.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.peregrin.DBHelper;
import com.peregrin.R;
import com.peregrin.ServerInfo;
import com.peregrin.activities.ChatActivity;
import com.peregrin.activities.MainActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class Receiver extends Service {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private static Thread receiveThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
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

                        ContentValues cv = new ContentValues();

                        for (int i = 0; i < messages.size(); i++) {
                            String login = messages.get(i).get("sender_login");
                            String content = messages.get(i).get("content");

                            cv.put("sender_login", login);
                            cv.put("recipient_login", recipient);
                            cv.put("content", content);
                            db.insert("messages", null, cv);


                            Cursor cursor = db.query("chats_list", null, "interlocutor_login = ?",
                                    new String[]{login}, null, null, null, null);

                            if (!cursor.moveToFirst()) {

                                String[] AddRequest = new String[3];
                                AddRequest[0] = "FIND_USER";
                                AddRequest[1] = login;
                                AddRequest[2] = getSharedPreferences("user", MODE_PRIVATE).getString("phone", "");

                                outputStream.writeObject(AddRequest);

                                if (inputStream.readBoolean()) {
                                    String interlocutor_nickname = (String) inputStream.readObject();

                                    ContentValues cvAdd = new ContentValues();
                                    cvAdd.put("interlocutor_login", login);
                                    cvAdd.put("interlocutor_nickname", interlocutor_nickname);

                                    db.insert("chats_list", null, cv);

                                    Intent intent = new Intent(MainActivity.ACTION_UPDATE_CHATS_LIST);
                                    intent.putExtra("received", true);
                                    sendBroadcast(intent);
                                }
                            }

                            String nickname = cursor.getString(cursor.getColumnIndex("interlocutor_nickname"));
                            int id = cursor.getInt(cursor.getColumnIndex("id"));

                            cursor.close();

                            if (((ActivityStatusListener) getApplicationContext())
                                    .isChatActivityForeground() &&
                                    ChatActivity.getInterlocutorLogin().equals(login)) {
                                Intent intent = new Intent(ChatActivity.ACTION_UPDATE_CHAT);
                                intent.putExtra("received", true);
                                sendBroadcast(intent);
                            } else {
                                Context context = getApplicationContext();

                                Intent notificationIntent = new Intent(context, ChatActivity.class);
                                notificationIntent.putExtra("interlocutor_login", login);
                                notificationIntent.putExtra("interlocutor_nickname", nickname);

                                PendingIntent contentIntent = PendingIntent.getActivity(context,
                                        0, notificationIntent,
                                        PendingIntent.FLAG_CANCEL_CURRENT);

                                Resources res = context.getResources();
                                Notification.Builder builder = new Notification.Builder(context);

                                builder.setContentIntent(contentIntent)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                                        .setWhen(System.currentTimeMillis())
                                        .setAutoCancel(true)
                                        .setContentTitle(nickname)
                                        .setContentText(content);

                                Notification notification = builder.build();
                                notification.defaults = Notification.DEFAULT_ALL;

                                NotificationManager notificationManager = (NotificationManager) context
                                        .getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(id, notification);
                            }
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        Log.d("peregrin", e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!receiveThread.isAlive()) {
            receiveThread.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public static void stopThread() {
        receiveThread.interrupt();
    }
}
