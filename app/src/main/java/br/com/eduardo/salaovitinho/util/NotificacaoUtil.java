package br.com.eduardo.salaovitinho.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationManagerCompat;

import br.com.eduardo.salaovitinho.R;

/**
 * Created by eduardo.vasconcelos on 30/10/2017.
 */

public class NotificacaoUtil {

    @TargetApi(21)
    public static void geraNotificacaoSimples(Context context, Intent it, String titulo, String mensagem, int id) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        Notification.Builder notification = new Notification.Builder(context);
        notification.setDefaults(Notification.DEFAULT_ALL);
        notification.setContentTitle(titulo);
        notification.setContentText(mensagem);
        notification.setSmallIcon(R.mipmap.ic_info_icon);
        notification.setAutoCancel(true); //Auto cancela a notificacao ao clicar nela.
        notification.setVisibility(Notification.VISIBILITY_PUBLIC);
        notification.setColor(Color.WHITE);
        notification.setContentIntent(pendingIntent);

        manager.notify(id, notification.build());
    }

    @TargetApi(21)
    public static void geraNotificacaoSimplesPause(Context context, Intent it, String titulo, String mensagem, int id) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        Notification.Builder notification = new Notification.Builder(context);
        notification.setDefaults(Notification.DEFAULT_ALL);
        notification.setContentTitle(titulo);
        notification.setContentText(mensagem);
        notification.setSmallIcon(R.mipmap.ic_info_icon);
        notification.setContentIntent(pendingIntent);
        notification.setColor(Color.WHITE);
        notification.setAutoCancel(true); //Auto cancela a notificacao ao clicar nela.
        notification.setFullScreenIntent(pendingIntent, false);

        manager.notify(id, notification.build());
    }

    public static void cancelaNotificacao(Context context, int id) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.cancel(id);
    }

    public static void cancelaTodasNotificacoes(Context context) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.cancelAll();
    }
}
