package br.com.eduardo.salaovitinho;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InicioBotoesFragment extends Fragment {
    View view;
    Context context;

    private Button solicitacoesBotao;
    private Button marcadosBotao;
    private Button reservarCancelarBotao;
    private Button agendaBotao;
    private Button telefonesBotao;
    private Button sairBotao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_botoes_tela_inicial, viewGroup, false);
        context = view.getContext();

        solicitacoesBotao = view.findViewById(R.id.solicitacoesBotao);
        marcadosBotao = view.findViewById(R.id.marcadosBotao);
        reservarCancelarBotao = view.findViewById(R.id.reservarBotao);
        agendaBotao = view.findViewById(R.id.agendaBotao);
        telefonesBotao = view.findViewById(R.id.telefonesBotao);
        sairBotao = view.findViewById(R.id.sairBotao);

        trataAcaoClickBotoes();

        return view;
    }

    private void trataAcaoClickBotoes() {
        final FragmentManager fragmentManager = getFragmentManager();

        reservarCancelarBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new AutorizadorFragment()).commit();
            }
        });

        solicitacoesBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new AutorizadorFragment()).commit();
            }
        });

        marcadosBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new VisualizarAgendamentosFragment()).commit();
            }
        });

        agendaBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new AgendaFragment()).commit();
            }
        });

        telefonesBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new TelefoneFragment()).commit();
            }
        });

        sairBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finishAffinity();
            }
        });
    }

}
