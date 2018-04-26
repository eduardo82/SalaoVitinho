package br.com.eduardo.salaovitinho;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import br.com.eduardo.salaovitinho.adapter.ClientesAgendadosAdapter;
import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.formatter.DateFormatter;
import br.com.eduardo.salaovitinho.model.Horario;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;
import br.com.eduardo.salaovitinho.util.SalaoVitinhoUtils;

/**
 * Created by Eduardo on 10/03/2018.
 */
public class VisualizarAgendamentosFragment extends Fragment {

    Context context;
    View view;
    ListView listViewClientesAgendados;
    TextView textViewAgendamentosDia;
    ArrayList<Horario> listaClientesAgendados = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat(SalaoVitinhoConstants.FORMAT_DATA);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_visualizar_agendamentos, viewGroup, false);
        context = view.getContext();

        textViewAgendamentosDia = (TextView) view.findViewById(R.id.textViewAgendamentosDia);
        String texto = textViewAgendamentosDia.getText().toString() + " (" + DateFormatter.getDiaAtualBarras() + ")";
        textViewAgendamentosDia.setText(texto);

        montaListaClientes();
        salvaAgendamentosDiaAnteriorEmAtendidos();

        return view;
    }

    private void salvaAgendamentosDiaAnteriorEmAtendidos() {
        final String diaAnteriorAgendamento = DateFormatter.getDiaAnterior();
        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO,
                SalaoVitinhoConstants.PROFISSIONAL, SalaoVitinhoConstants.NAO_ATENDIDO,
                diaAnteriorAgendamento).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot diaAgendamento : dataSnapshot.getChildren()) {
                        Horario horario = diaAgendamento.getValue(Horario.class);
                        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO, SalaoVitinhoConstants.PROFISSIONAL,
                                SalaoVitinhoConstants.ATENDIDO, horario.getDiaAtendimento(), horario.getHoraAtendimento()).setValue(horario);
                    }

                    FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO,
                            SalaoVitinhoConstants.PROFISSIONAL, SalaoVitinhoConstants.NAO_ATENDIDO,
                            diaAnteriorAgendamento).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void montaListaClientes() {
        listaClientesAgendados = new ArrayList<>();
        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO, SalaoVitinhoConstants.VITINHO, SalaoVitinhoConstants.NAO_ATENDIDO,
                DateFormatter.getDiaAtual()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot diaAgendamento : dataSnapshot.getChildren()) {
                    Horario horario = diaAgendamento.getValue(Horario.class);
                    if (horario != null) {
                        if (horario.isAutorizado() && !horario.isRecusado()) {
                            if (!listaClientesAgendados.contains(horario)) {
                                listaClientesAgendados.add(horario);
                            }
                        }
                    }
                }
                Collections.sort(listaClientesAgendados);
                montaListViewClientes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void montaListViewClientes() {

        if (listaClientesAgendados.size() == 0) {
            Toast.makeText(context, "Você não possui clientes agendados hoje!", Toast.LENGTH_LONG).show();
        }
        else {
            ClientesAgendadosAdapter clientesAdapter = new ClientesAgendadosAdapter(context, listaClientesAgendados);
            listViewClientesAgendados = (ListView) view.findViewById(R.id.listViewAgendamentos);
            listViewClientesAgendados.setAdapter(clientesAdapter);

            listViewClientesAgendados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Horario horario = (Horario) parent.getItemAtPosition(position);
                confirmaCancelamentoHorario(horario);
                return false;
                }
            });
        }


    }

    private void confirmaCancelamentoHorario(final Horario horario) {
        final String agendamentosDia = sdf.format(new Date());
        DialogInterface.OnClickListener acaoBotaoSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                efetuaCancelamentoHorarioFirebase(horario, agendamentosDia);
            }
        };
        SalaoVitinhoUtils.exibeDialogConfirmacao(context, "Confirma o cancelamento deste atendimento?", acaoBotaoSim, null, true);
    }

    private void efetuaCancelamentoHorarioFirebase(Horario horario, String agendamentosDia) {
        horario.setRecusado(true);
        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO,
                SalaoVitinhoConstants.VITINHO, SalaoVitinhoConstants.NAO_ATENDIDO, agendamentosDia, horario.getHoraAtendimento()).setValue(horario);
        SalaoVitinhoUtils.exibeDialogConfirmacao(context, "Cancelamento efetuado com sucesso.", null);
        listaClientesAgendados.remove(horario);
        montaListViewClientes();
    }
}
