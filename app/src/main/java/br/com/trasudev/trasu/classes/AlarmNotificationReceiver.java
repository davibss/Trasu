package br.com.trasudev.trasu.classes;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.entidades.TarefaIndividual;
import br.com.trasudev.trasu.entidades.Usuario;

import static br.com.trasudev.trasu.activitys.MainActivity.txtPontos;
import static br.com.trasudev.trasu.fragments.TarefaFragment.contexto;
import static br.com.trasudev.trasu.fragments.TarefaFragment.databaseReference;
import static br.com.trasudev.trasu.fragments.TarefaFragment.id_notify;
import static br.com.trasudev.trasu.fragments.TarefaFragment.id_user;
import static br.com.trasudev.trasu.fragments.TarefaFragment.titulo;
import static br.com.trasudev.trasu.fragments.TarefaFragment.prioridade;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    public static String id;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if ("FINALIZAR".equals(action)){
            databaseReference.child("usuario").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot obj: dataSnapshot.getChildren()){
                        Usuario u = obj.getValue(Usuario.class);
                        if (u.getUser_id().equals(id_user)) {
                            if (u.getTarefa_individual().values()!=null){
                                for (TarefaIndividual p : u.getTarefa_individual().values()){
                                    if (p.getTar_idnotify() == intent.getIntExtra("id_notification",0)){
                                        Intent myIntent = new Intent(context,AlarmNotificationReceiver.class);
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                                p.getTar_idnotify(),myIntent,0);
                                        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                                        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        manager.cancel(pendingIntent);
                                        notificationManager.cancel(p.getTar_idnotify());
                                        databaseReference.child("usuario").child(id_user).
                                                child("tarefa_individual").child(p.getTar_id()).
                                                child("tar_status").setValue(1);
                                        int soma = u.getUser_pontos() + 1;
                                        databaseReference.child("usuario").
                                                child(id_user).
                                                child("user_pontos").setValue(soma);
                                        Toast.makeText(context, "Tarefa finalizada", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            /*databaseReference.child("usuario").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    for (DataSnapshot obj: dataSnapshot.getChildren()){
                        Usuario u = obj.getValue(Usuario.class);
                        if (u.getUser_id().equals(id_user)) {
                            if (u.getTarefa_individual().values()!=null){
                                for (TarefaIndividual p : u.getTarefa_individual().values()){
                                    if (p.getTar_idnotify() == intent.getIntExtra("id_notification",0)){
                                        Intent myIntent = new Intent(context,AlarmNotificationReceiver.class);
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                                p.getTar_idnotify(),myIntent,0);
                                        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                                        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        manager.cancel(pendingIntent);
                                        notificationManager.cancel(p.getTar_idnotify());
                                        databaseReference.child("usuario").child(id_user).
                                                child("tarefa_individual").child(p.getTar_id()).
                                                child("tar_status").setValue(1);
                                        int soma = u.getUser_pontos() + 1;
                                        databaseReference.child("usuario").
                                                child(id_user).
                                                child("user_pontos").setValue(soma);
                                        Toast.makeText(context, "Tarefa finalizada", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
        }else if ("EXCLUIR".equals(action)){
            databaseReference.child("usuario").child(id_user).
                    child("tarefa_individual").
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            for (DataSnapshot obj: dataSnapshot.getChildren()){
                                TarefaIndividual p = obj.getValue(TarefaIndividual.class);
                                if (p.getTar_idnotify() == intent.getIntExtra("id_notification",0)){
                                    Intent myIntent = new Intent(context,AlarmNotificationReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                            p.getTar_idnotify(),myIntent,0);
                                    AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    manager.cancel(pendingIntent);
                                    notificationManager.cancel(p.getTar_idnotify());
                                    databaseReference.child("usuario").child(id_user).
                                            child("tarefa_individual").child(p.getTar_id())
                                            .removeValue();
                                    Toast.makeText(context, "Tarefa excluída", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }else{
            Bundle extras = new Bundle();
            extras.putInt("id_notification",id_notify);
            extras.putString("titulo",titulo);
            Intent finalizarReceive = new Intent();//attach all keys starting with wzrk_ to your notification extras
            finalizarReceive.putExtras(extras);
            finalizarReceive.setAction("FINALIZAR");//replace with your custom value
            PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 12345, finalizarReceive, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent excluirReceive = new Intent();//attach all keys starting with wzrk_ to your notification extras
            excluirReceive.putExtras(extras);
            excluirReceive.setAction("EXCLUIR");
            PendingIntent pendingIntentNo = PendingIntent.getBroadcast(context, 12345, excluirReceive, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(titulo)
                    .setContentText(contexto)
                    .addAction(R.drawable.ic_assignment_turned_in_black_24dp,"FINALIZAR",pendingIntentYes)
                    .addAction(R.drawable.ic_delete_black_24dp,"EXCLUIR",pendingIntentNo)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentInfo(prioridade);
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id_notify,builder.build());
        }
    }
}

