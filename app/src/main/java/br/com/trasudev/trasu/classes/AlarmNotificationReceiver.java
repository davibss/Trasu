package br.com.trasudev.trasu.classes;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import br.com.trasudev.trasu.R;

import static br.com.trasudev.trasu.fragments.TarefaFragment.contexto;
import static br.com.trasudev.trasu.fragments.TarefaFragment.titulo;
import static br.com.trasudev.trasu.fragments.TarefaFragment.prioridade;

public class AlarmNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contexto))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(titulo)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentInfo(prioridade);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random rand = new Random();
        notificationManager.notify(rand.nextInt(),builder.build());
    }
}

