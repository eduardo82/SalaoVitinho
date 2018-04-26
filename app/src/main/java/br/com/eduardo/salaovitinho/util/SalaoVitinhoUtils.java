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

    public static void insereMensagemLayout(Context context, LinearLayout linearLayout, String mensagemInserida) {
        linearLayout.removeAllViews();
        TextView et = new TextView(context);
        et.setText(mensagemInserida);
        et.setPadding(0, 100, 0, 100);
        et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        et.setTextSize(20);
        linearLayout.addView(et);
    }

    public static void exibeDialogInformacoesUsuario(final Context context, Telefone telefone) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_adiciona_telefone, null);

        final Switch autorizado = view.findViewById(R.id.switchAdicionarTelefone);
        final EditText telephone = view.findViewById(R.id.editTextTelefone);

        if (telefone != null) {
            autorizado.setChecked(telefone.isAutorizado());
            telephone.setText(telefone.getNumero());
        }

        autorizado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String valorComponente = isChecked ? "Autorizado" : "Negado";
                Toast.makeText(context, valorComponente, Toast.LENGTH_SHORT).show();
            }
        });

        BrPhoneNumberFormatter addLineNumberFormatter = new BrPhoneNumberFormatter(new WeakReference<>(telephone));
        telephone.addTextChangedListener(addLineNumberFormatter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Fechar", null);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = ((EditText) view.findViewById(R.id.editTextTelefone)).getText().toString();

                if (numero.length() > 0 && numero.matches(".(31.)\\s9[7-9][0-9]{3}-[0-9]{4}")) {
                    Telefone telefone = new Telefone();
                    telefone.setNumero(numero);
                    telefone.setAutorizado(autorizado.isChecked());
                    FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.FIREBASE_NODE_TELEFONES, numero).setValue(telefone);

                    dialog.dismiss();
                }
                else {
                    Toast.makeText(context, "O telefone deve ser válido e estar no formato (31) 99999-9999.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

