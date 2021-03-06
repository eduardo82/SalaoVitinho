package br.com.eduardo.salaovitinho.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import br.com.eduardo.salaovitinho.R;
import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.formatter.BrPhoneNumberFormatter;
import br.com.eduardo.salaovitinho.model.Telefone;

/**
 * Created by Eduardo on 30/11/2017.
 */

public class SalaoVitinhoUtils {


    public static void exibeDialogConfirmacao(Context context,
                                       String mensagem,
                                       DialogInterface.OnClickListener acaoBotaoSim,
                                       DialogInterface.OnClickListener acaoBotaoNao,
                                       boolean exibeBotaoNao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(SalaoVitinhoConstants.CONFIRMACAO);
        builder.setIcon(R.mipmap.ic_ok_icon);
        builder.setMessage(mensagem);
        builder.setPositiveButton(SalaoVitinhoConstants.SIM, acaoBotaoSim);

        if (exibeBotaoNao) {
            builder.setNegativeButton(SalaoVitinhoConstants.NAO, acaoBotaoNao);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void exibeDialogConfirmacao(Context context,
                                              String mensagem,
                                              DialogInterface.OnClickListener acaoBotaoSim) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(SalaoVitinhoConstants.CONFIRMACAO);
        builder.setIcon(R.mipmap.ic_ok_icon);
        builder.setMessage(mensagem);
        builder.setPositiveButton(SalaoVitinhoConstants.SIM, acaoBotaoSim);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void exibeDialogConfirmacao(Context context,
                                              String titulo,
                                              String mensagem,
                                              DialogInterface.OnClickListener acaoBotaoSim) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);
        builder.setIcon(R.mipmap.ic_ok_icon);
        builder.setMessage(mensagem);
        builder.setPositiveButton("OK", acaoBotaoSim);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void insereMensagemLayout(Context context, LinearLayout linearLayout, String mensagemInserida) {
        linearLayout.removeAllViews();
        TextView et = new TextView(context);
        et.setText(mensagemInserida);
        et.setPadding(0, 100, 0, 100);
        et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        et.setTextSize(20);
        linearLayout.addView(et);
    }


}

