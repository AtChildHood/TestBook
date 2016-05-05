package com.example.hanjun.testbook;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * Created by hanjun on 2016/4/22.
 */
public class MyService extends IntentService {

    int NOTIFICATION_FLAG = 1;
    String ip="182.254.215.198";
   // String ip = "10.132.23.101";
    private static final int TCP_PORT = 30006;
    /**UDP端口 */

    private static final int UDP_PORT = 9999;
    private static int UDP_NoticePORT = 8787;
    boolean SEND_NOTIFICATION=false;
    public  Boolean IsThreadDisable = false;//指示监听线程是否终止
    WifiManager manager;
    WifiManager.MulticastLock lock;
    DatagramSocket s;
    NotificationManager notifiermanager;

    MyBroadcastReceiver receiver;
    NoticeSend receiver1;
    sendMessage receiver2;
    boolean first=true;
    DatabaseHelper databaseHelper;

    String user;

    public MyService(){
        super("MyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // The service is being created
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        manager = (WifiManager)
                this.getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("wifilock");
        notifiermanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        /**
         * 用于唤醒service
         * */
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, filter);

        /**
         * 用于检测主界面是否出现在前台
         *
         * */
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("HanJun.DecideSendNotification");
        receiver1 = new NoticeSend();
        registerReceiver(receiver1, filter1);

        /**
         *主页面发送消息
         * */
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("HanJun.sendMessage");
        receiver2 = new sendMessage();
        registerReceiver(receiver2, filter2);

        /**
         *关闭intentservice
         * */

        databaseHelper = new DatabaseHelper(this);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        user=intent.getStringExtra("user");
        return START_STICKY;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if(first) {
                s = new DatagramSocket(UDP_NoticePORT);
                first = false;
                connect();
                getMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // The service is no longer used and is being destroyed
        unregisterReceiver(receiver);
        unregisterReceiver(receiver1);
        unregisterReceiver(receiver2);
   //     unregisterReceiver(receiver3);
    }


    public void connect() {

            BufferedWriter writer=null;
            BufferedReader reader=null;
            Socket socket=null;
            try
            {
                socket = new Socket(ip, TCP_PORT);
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                JSONObject obj = new JSONObject();
                obj.put("user",user);
                writer.write(URLEncoder.encode(obj.toString(),"UTF-8")+"\n");
                writer.flush();

            }catch(UnknownHostException e1)
            {
                // Toast.makeText(CommunityActivity.this, "无法连接到未知的主机名", Toast.LENGTH_SHORT).show();
                System.out.println("无法连接到未知的主机名");
            }catch(Exception e1)
            {
                e1.printStackTrace();
                // Toast.makeText(CommunityActivity.this, "出现无法识别的异常", Toast.LENGTH_SHORT).show();
                System.out.println("出现无法识别的异常");
            }
            try
            {
                String line;
                if ((line = reader.readLine()) != null) {
                    noticeApp("connect",line);
                    JSONObject j = new JSONObject();
                    j.put("test", "test");
                    j.put("user", user);
                    send(j.toString());
                }
                if (socket != null) {
                    socket.close();
                }
            } catch(Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

    }


    public void getMessage(){

                byte[] message = new byte[1024];
                try {
                    // 建立Socket连接

                    s.setBroadcast(true);
                    DatagramPacket datagramPacket = new DatagramPacket(message,message.length);
                    Log.d("UDP Demo", "接受任务创建");
                    while (!IsThreadDisable) {
                        Log.d("UDP Demo", "准备接受");
                        lock.acquire();
                        s.receive(datagramPacket);
                        String strMsg= URLDecoder.decode(new String(datagramPacket.getData(),"UTF-8").trim(),"UTF-8");
                        Log.d("UDP Demo",strMsg);
                        Log.d("UDP Demo", datagramPacket.getAddress()
                                .getHostAddress().toString()
                                + ":" +strMsg);

                        if(SEND_NOTIFICATION){
                            databaseHelper.insert(strMsg);
                            databaseHelper.insertNotReceiveMessageTable(strMsg);
                            notificationMethod(strMsg,notifiermanager);
                        }else{
                            noticeApp("receive", strMsg);
                        }
                        lock.release();
                        message = new byte[1024];
                        datagramPacket = new DatagramPacket(message,message.length);

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
    }

    public void send(String message) {
        try {
            InetAddress local = InetAddress.getByName(ip);
            message = new String(message.getBytes("UTF-8"));
            message = URLEncoder.encode(message, "UTF-8");
            int msg_length = message.length();
            byte[] messageByte = message.getBytes("UTF-8");
            DatagramPacket p = new DatagramPacket(messageByte, msg_length, local,UDP_PORT);
            s.send(p);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**用于向主界面发送广播通知收到信息
     *
     * */
    public void noticeApp(String type,String message){
        Intent intent = new Intent();
        // 设置Intent的Action属性
        intent.setAction("HanJun.MessageNotice");
        intent.putExtra("type", type);
        intent.putExtra("message", message);
        //发送广播
        sendBroadcast(intent);
    }

    public void notificationMethod(String message,NotificationManager notifiermanager){
//        if(NOTIFICATION_FLAG>1){
//            notifiermanager.cancel(NOTIFICATION_FLAG-1);
//        }
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0,
                new Intent(this, CommunityActivity.class), 0);

        Notification notify3 = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon_app)
                .setAutoCancel(true)
                .setTicker(message)
                .setContentIntent(pendingIntent2)
                .setContentTitle("新消息")
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .getNotification();
        notifiermanager.notify(NOTIFICATION_FLAG,notify3);
        //NOTIFICATION_FLAG++;
    }

}
