package br.com.eduardo.salaovitinho;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.com.eduardo.salaovitinho.banco.MensagemDB;
import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.model.Mensagem;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;
import br.com.eduardo.salaovitinho.util.SalaoVitinhoUtils;

import static br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants.MENSAGEM;

/**
 * Created by Eduardo on 14/03/2018.
 */

public class DetalheMensagemFragment extends Fragment {

    View view;
    Context context;
    String[] caminho;
    ScrollView scrollView;
    LinearLayout linearLayout;
    LinearLayout layoutMensagem;
    Button btnCriarMensagem;
    MensagemDB mensagemDB;
    String telefone;
    String mensagemParametro;
    String remetente;
    Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_mensageiro, viewGroup, false);
        context = view.getContext();

        bundle = getArguments();

        if (bundle != null) {
            telefone = bundle.getString("telefone");
            mensagemParametro = bundle.getString("mensagem");
            remetente = bundle.getString("remetente");
        }

        caminho = new String[] {MENSAGEM, SalaoVitinhoConstants.VITINHO, telefone};

        linearLayout = view.findViewById(R.id.linearLayoutMensageiro);
        mensagemDB = new MensagemDB(context);

        manipulaComponentesActivity();
        observerMensagemUsuario();
        scrollToDown();
        return view;
    }

    private void scrollToDown() {
        scrollView = view.findViewById(R.id.scrollMensagem);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void manipulaComponentesActivity() {
        Mensagem filtro = new Mensagem();
        filtro.setMensagem(mensagemParametro);
        filtro.setTelefone(telefone);
        filtro.setRemetente(remetente);
        final List<Mensagem> mensagems = mensagemDB.buscaMensagensTelefone(filtro);

        if (mensagems != null && mensagems.size() > 0) {
            for (Mensagem mensagem : mensagems) {
                if (!mensagem.isLido()) {
                    preencheComponenteMensagemUsuario(false, mensagem.getMensagem(), mensagem.getId());
                }
                else {
                    preencheComponenteMensagemUsuario(true, mensagem.getResposta(), mensagem.getId());
                }
            }
        }

        layoutMensagem = view.findViewById(R.id.linear_mmensagem);
        layoutMensagem.requestFocus();
        btnCriarMensagem = layoutMensagem.findViewById(R.id.btnCriarMensagem);
        btnCriarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mensagemMensageiro = (EditText) layoutMensagem.findViewById(R.id.editTextMensagem);

                if (mensagemMensageiro.getText().length() > 0) {
                    Mensagem mensagem = new Mensagem(remetente, "", telefone, true, mensagemMensageiro.getText().toString());
                    FirebaseUtils.getReferenceChild(caminho).setValue(mensagem);
                    mensagem.setMensagem(null);
                    mensagemDB.salvar(mensagem);
                    getFragmentManager().beginTransaction().detach(DetalheMensagemFragment.this)
                        .attach(DetalheMensagemFragment.this).commitAllowingStateLoss();
                    mensagemMensageiro.setText("");
                    scrollToDown();
                }
            }
        });
    }

    private void preencheComponenteMensagemUsuario(boolean isResposta, String mensagem, Long id) {
        View view;
        if (isResposta) {
            view = preencheTextViewMensagem(R.layout.layout_textview_received_include, R.id.textViewMsgResposta, mensagem, id);
        }
        else {
            view = preencheTextViewMensagem(R.layout.layout_textview_sent_include, R.id.textViewMsgMensageiro, mensagem, id);
        }

        linearLayout.addView(view);
    }

    @NonNull
    private View preencheTextViewMensagem(int idLayout, int idTextView, final String mensagem, final Long idMensagem) {
        View view;
        TextView textView;
        view = LayoutInflater.from(context).inflate(idLayout, null);
        textView = view.findViewById(idTextView);
        textView.setText(mensagem);
        textView.setId(linearLayout.getChildCount() + 1);

        final DialogInterface.OnClickListener botaoSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            mensagemDB.deletar(idMensagem);
            getFragmentManager().beginTransaction().detach(DetalheMensagemFragment.this)
                .attach(DetalheMensagemFragment.this).commit();
            }
        };

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SalaoVitinhoUtils.exibeDialogConfirmacao(context, "Confirma apagar a mensagem?", botaoSim, null, true);
                return false;
            }
        });
        return view;
    }

    private void observerMensagemUsuario() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Mensagem resultado = dataSnapshot.getValue(Mensagem.class);

                if (resultado != null) {
                    if (!resultado.isLido()) {
                        if (mensagemDB.buscaUltimaMensagemTelefone(resultado).size() == 0) {
                            mensagemDB.salvar(resultado);

                            if (getFragmentManager() != null) {
                                getFragmentManager().beginTransaction().detach(DetalheMensagemFragment.this)
                                        .attach(DetalheMensagemFragment.this).commitAllowingStateLoss();
                            }

                            FirebaseUtils.getReferenceChild(caminho).removeEventListener(this);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseUtils.getReferenceChild(caminho).addValueEventListener(listener);
    }
}