package com.icandothisallday2020.ex60broadcastreceiverbooting;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

public class BootingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Booting Received", Toast.LENGTH_SHORT).show();

        String action=intent.getAction();
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            //android 10버전 이상은 직접 액티비티 실행 불가
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                //부팅을 듣고 Notification 을 만들고 Notification 을 클릭하여 액티비티 실행
                NotificationManager manager= (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder=null;
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    NotificationChannel channel=new NotificationChannel
                            ("ch01","channel #01",NotificationManager.IMPORTANCE_HIGH);
                    manager.createNotificationChannel(channel);
                    builder=new Notification.Builder(context,"ch01");

                    //사운드나 진동을 채널 객체에 설정
                    channel.setVibrationPattern(new long[]{0,2000,3000,2000});
                    Uri soundUri=Uri.parse("android.resource://"
                            +context.getPackageName()+"/"+R.raw.fireball);
                    channel.setSound(soundUri,new AudioAttributes.Builder().build());
                }else{
                    builder=new Notification.Builder(context,null);
                    //사운드 작업
                    Uri soundUri=Uri.parse("android.resource://"
                            +context.getPackageName()+"/"+R.raw.fireball);
                    builder.setSound(soundUri);
                }
                //상태바에 보이는 아이콘
                builder.setSmallIcon(R.drawable.ic_stat_name);
                //확장상태바의 설정
                builder.setContentTitle("부팅이 완료되었습니다.");
                builder.setContentText("Ex60의 MainActivity를 실행 할 수 있습니다.");
                //확장상태바의 알림을 클릭하여 MainActivity 를 실행하도록
                Intent i=new Intent(context,MainActivity.class);
                //바로실행되지 않기때문에 보류중인 인텐트로 변경
                PendingIntent pi=PendingIntent.getActivity(context,17,i,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                //똑같은 식별번호(17)로 알림이 오면 가장 최근의 것으로
                builder.setContentIntent(pi);//알림에 인텐트 적용
                builder.setAutoCancel(true);//누르면 알림없어지도록
                Notification notification=builder.build();
                manager.notify(17,notification);
                // 지울 때 필요한 식별 번호 manager.cancel(17);
            }else{
                //새로운 액티비티 실행
                Intent i= new Intent(context,MainActivity.class);
                //액티비티를 실행하는 TASK가 없었기 때문에 그냐 startActivity()하면
                //새로운 액티비티가 실행되지 않음--인텐트에 설정 추가
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
