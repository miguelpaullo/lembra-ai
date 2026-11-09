package com.example.lembraai;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;

public class LembreteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Recupera as informações da tarefa enviadas pelo alarme
        String titulo = intent.getStringExtra("titulo");
        String descricao = intent.getStringExtra("descricao");

        // Verifica se o usuário autorizou as notificações
        if (Build.VERSION.SDK_INT >= 33 &&
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intentLista = new Intent(context, ListaActivity.class);

        PendingIntent abrirLista = PendingIntent.getActivity(
                context,
                0,
                intentLista,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Monta a notificação
        NotificationCompat.Builder notificacao =
                new NotificationCompat.Builder(context, "canal_lembretes")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Lembrete: " + titulo)
                        .setContentText(descricao)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(abrirLista)
                        .setAutoCancel(true);

        // Exibe a notificação
        NotificationManagerCompat.from(context).notify(
                (int) System.currentTimeMillis(),
                notificacao.build()
        );
    }
}