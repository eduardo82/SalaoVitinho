package br.com.eduardo.salaovitinho;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.com.eduardo.salaovitinho.adapter.AdapterHorario;
import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.formatter.DateFormatter;
import br.com.eduardo.salaovitinho.model.Agenda;
import br.com.eduardo.salaovitinho.model.Horario;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;
import br.com.eduardo.salaovitinho.util.SalaoVitinhoUtils;

import static br.com.eduardo.salaovitinho.R.drawable.round_button;

public class ReservarCancelarHorarioFragment extends Fragment {

    View view;
    Context context;
    final Locale locale = new Locale("pt", "BR");

    String[] horarios = new String[]{ "08:00h", "08:30h", "09:00h", "09:30h", "10:30h",
        "11:00h", "11:30h", "12:00h", "13:00h", "13:30h", "14:00h", "14:30h", "15:00h",
        "15:30h", "16:00h", "16:30h", "17:00h", "17:30h", "18:00h", "18:30h", "19:00h", "19:30h", "20:00h"};

    private EditText dataAgendamento;
    private ListView horariosListView;
    private EditText editTextNomeReservado;
    private Button btnAgendamento;
    private String diaAgendamento;
    private String horaAgendamento;
    private ArrayAdapter<String> adapterHorario;
    private ArrayList<String> horariosMarcados = new ArrayList<String>();
    private TextView horarioTxt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_reserva_horario, viewGroup, false);
        context = view.getContext();

        editTextNomeReservado = view.findViewById(R.id.editTextNomeReservado);
        editTextNomeReservado.requestFocus();
        diaAgendamento = getActivity().getIntent().getStringExtra("data_agendamento");
        verificaAgendaDia(SalaoVitinhoConstants.PROFISSIONAL, diaAgendamento);

        return view;
    }

    private void trataElementosTela() {
        horarioTxt = view.findViewById(R.id.horarioTxt);
        horarioTxt.setText("Horários disponíveis!");
        dataAgendamento = view.findViewById(R.id.dataAgendamentoEditText);
        dataAgendamento.setText(diaAgendamento);
        dataAgendamento.setEnabled(false);

        btnAgendamento = view.findViewById(R.id.buttonAgendamento);
        btnAgendamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraDialogConfirmacao();
            }
        });
    }

    private void verificaAgendaDia(final String profissional, final String diaAgendamento) {
        final String diaAgendamentoSemBarras = diaAgendamento.replace("/","_");

        final ValueEventListener lisneter = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Agenda agendaDia = dataSnapshot.getValue(Agenda.class);

                if (agendaDia != null) {
                    if (!agendaDia.getCancelada()) {
                        trataElementosTela();
                        if (!agendaDia.getHoraPadrao()) {
                            montaHorariosIntervaloHorarioAtendimento(agendaDia);
                        }
                        buscaDadosBanco(profissional, diaAgendamentoSemBarras);
                    }
                    else {
                        LinearLayout linearLayout = view.findViewById(R.id.reservar_cancelar_Agendamentos);
                        SalaoVitinhoUtils.insereMensagemLayout(context, linearLayout, "SUA AGENDA ESTÁ CANCELADA\n PARA O DIA!");
                    }
                }
                else {
                    FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDA, profissional, diaAgendamentoSemBarras).removeEventListener(this);
                    LinearLayout linearLayout = view.findViewById(R.id.reservar_cancelar_Agendamentos);
                    SalaoVitinhoUtils.insereMensagemLayout(context, linearLayout, "VOCÊ AINDA NÃO CRIOU A AGENDA PARA O DIA!");
                    linearLayout.setGravity(Gravity.CENTER);

                    Button botao =  new Button(context);
                    botao.setText("CRIAR AGENDA");
                    botao.setTextColor(Color.WHITE);
                    botao.setLayoutParams(new LinearLayout.LayoutParams(600, 150));
                    botao.setBackground(ContextCompat.getDrawable(context, round_button));
                    botao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.conteudo,
                                    new AgendaFragment()).commit();

                        }
                    });
                    linearLayout.addView(botao);
                }}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDA, profissional, diaAgendamentoSemBarras).addValueEventListener(lisneter);
    }

    private void montaHorariosIntervaloHorarioAtendimento(Agenda agenda) {
        List<String> horariosDisponiveis = new ArrayList<String>();
        if (agenda.getPrimeiraHoraInicio().compareTo(agenda.getPrimeiraHoraFim()) < 0 &&
            agenda.getSegundaHoraInicio().compareTo(agenda.getSegundaHoraFim()) < 0) {

            Calendar primeiraDataHoraInicio = retornaDataHoraAgendamento(agenda.getPrimeiraHoraInicio());
            Calendar primeiraDataHoraFim = retornaDataHoraAgendamento(agenda.getPrimeiraHoraFim());
            Calendar segundaDataHoraInicio = retornaDataHoraAgendamento(agenda.getSegundaHoraInicio());
            Calendar segundaDataHoraFim = retornaDataHoraAgendamento(agenda.getSegundaHoraFim());

            while (primeiraDataHoraInicio.before(primeiraDataHoraFim)) {
                adicionaHorarioDisponiveis(primeiraDataHoraInicio, horariosDisponiveis);
            }

            while (segundaDataHoraInicio.before(segundaDataHoraFim)) {
                adicionaHorarioDisponiveis(segundaDataHoraInicio, horariosDisponiveis);
            }
        }

        if (horariosDisponiveis.size() > 0) {
            horarios = null;
            horarios = horariosDisponiveis.toArray(new String[horariosDisponiveis.size()-1]);
        }
    }

    private Calendar retornaDataHoraAgendamento(String horaEscolhida) {

        Calendar diaHoraAtendimento = Calendar.getInstance(locale);
        String[] dataAgendada = diaAgendamento.split("/");
        diaHoraAtendimento.set(Integer.valueOf(dataAgendada[2]), Integer.valueOf(dataAgendada[1]) - 1, Integer.valueOf(dataAgendada[0]));
        diaHoraAtendimento.set(Calendar.HOUR_OF_DAY, Integer.valueOf(horaEscolhida.split(":")[0]));
        diaHoraAtendimento.set(Calendar.MINUTE, Integer.valueOf(horaEscolhida.split(":")[1]));
        diaHoraAtendimento.set(Calendar.SECOND, 0);

        return diaHoraAtendimento;
    }

    private void adicionaHorarioDisponiveis(Calendar horaAdicionar, List<String> vetorRecebedor) {
        String horaInicial = DateFormatter.formatHoraMinuto(horaAdicionar.getTime());
        vetorRecebedor.add(horaInicial + "h");
    }

    private void buscaDadosBanco(String profissional, String diaAgendamento) {
        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO,
                profissional, SalaoVitinhoConstants.NAO_ATENDIDO,
                diaAgendamento).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    horariosMarcados = new ArrayList<>();

                    for (DataSnapshot diaAgendamento : dataSnapshot.getChildren()) {
                        Horario horario = diaAgendamento.getValue(Horario.class);

                        if (!horario.isDisponivel()) {
                            horariosMarcados.add(diaAgendamento.getKey());
                        }
                    }
                }
                montaAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Erro ao tentar obter os dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostraDialogErro() {
        Toast.makeText(context, "Não foi possível realizar o agendamento.", Toast.LENGTH_LONG).show();
    }

    private void mostraAguardeConfirmacaoAgendamento() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Mensagem");
        dialog.setMessage("Agendamento realizado com sucesso!");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void montaAdapter() {
        List<String> listaHorarios = Arrays.asList(horarios);
        Collections.sort(listaHorarios);
        adapterHorario = new AdapterHorario(context, listaHorarios, horariosMarcados);
        horariosListView = view.findViewById(R.id.horariosListView);

        horariosListView.setAdapter(adapterHorario);
        horariosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                horaAgendamento = (String) horariosListView.getItemAtPosition(position);
            }
        });
    }

    private void mostraDialogConfirmacao() {
        final String diaAgendamentoSemBarra = diaAgendamento.replace("/", "_");
        if (horaAgendamento != null ) {
            String mensagem = "Confirma o(s) agendamento(s) selecionado(s)?";
            DialogInterface.OnClickListener acaoBotaoSim = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String nome = editTextNomeReservado.getText() != null ? editTextNomeReservado.getText().toString() : "RESERVADO";
                FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO, SalaoVitinhoConstants.PROFISSIONAL,
                        SalaoVitinhoConstants.NAO_ATENDIDO, diaAgendamentoSemBarra , horaAgendamento)
                    .setValue(new Horario(nome, "RESERVADO", diaAgendamentoSemBarra, horaAgendamento, false, true, false, true),
                            new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            editTextNomeReservado.setText("");
                            mostraAguardeConfirmacaoAgendamento();
                            buscaDadosBanco(SalaoVitinhoConstants.PROFISSIONAL, diaAgendamento);
                        }
                        else {
                            mostraDialogErro();
                        }
                        }
                    });
                }
            };

            SalaoVitinhoUtils.exibeDialogConfirmacao(context, mensagem, acaoBotaoSim, null, true);
        }
        else {
            Toast.makeText(context, "Escolha um horário para agendar.", Toast.LENGTH_LONG).show();
        }
    }
}
