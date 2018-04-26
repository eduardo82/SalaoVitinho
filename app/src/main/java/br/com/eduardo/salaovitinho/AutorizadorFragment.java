package br.com.eduardo.salaovitinho;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import br.com.eduardo.salaovitinho.adapter.AgendamentosAdapter;
import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.model.Horario;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;
import br.com.eduardo.salaovitinho.util.SalaoVitinhoUtils;

public class AutorizadorFragment extends Fragment {

    Context context;
    View view;
    ListView listViewClientesSolicitantes;
    ArrayList<Horario> listaClientesSolicitantes = new ArrayList<>();
    AgendamentosAdapter clientesAdapter;
    Button aprovarBtn;
    Button recusarBtn;
    String motivo;
    String[] motivosRecusaAtendimento = new String[]{"Estarei viajando", "Estou com problemas de saúde",
    "Resolvendo assuntos pessoais", "Sem energia elétrica", "Meu filho está doente"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_autorizador, viewGroup, false);
        context = view.getContext();

        aprovarBtn = (Button) view.findViewById(R.id.buttonAprovar);
        recusarBtn = (Button) view.findViewById(R.id.buttonRecusar);
        listViewClientesSolicitantes = (ListView) view.findViewById(R.id.listViewSolicitacoes);

        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO, SalaoVitinhoConstants.VITINHO, SalaoVitinhoConstants.NAO_ATENDIDO)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    trataAcaoBotoesTela();
                    for (DataSnapshot diaAgendamento : dataSnapshot.getChildren()) {
                        for (DataSnapshot horaAgendamento : diaAgendamento.getChildren()) {
                            Horario horario = horaAgendamento.getValue(Horario.class);

                            if (horario != null) {
                                if (!horario.isAutorizado() && !horario.isDisponivel() && !horario.isRecusado()) {
                                    if (!listaClientesSolicitantes.contains(horario)) {
                                        listaClientesSolicitantes.add(horario);
                                    }
                                }
                            }
                        }
                    }
                }

                if (listaClientesSolicitantes.size() > 0) {
                    Collections.sort(listaClientesSolicitantes);
                    montaListaClientesSolicitantes(listaClientesSolicitantes);
                    aprovarBtn.setVisibility(View.VISIBLE);
                    recusarBtn.setVisibility(View.VISIBLE);
                }
                else {
                    aprovarBtn.setVisibility(View.GONE);
                    recusarBtn.setVisibility(View.GONE);
                    Toast.makeText(context, "Você não possui agendamentos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) { }
        });
        return view;
    }

    private void trataAcaoBotoesTela() {
        aprovarBtn = (Button) view.findViewById(R.id.buttonAprovar);
        recusarBtn = (Button) view.findViewById(R.id.buttonRecusar);

        aprovarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraDialogConfirmacaoCancelamento(true, false, false);
            }
        });

        recusarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Horario> listaEscolhidos = clientesAdapter.getHorariosEscolhidos();
                if (listaEscolhidos.size() > 0) {
                    mostraDialogMensagem();
                }
                else {
                    Toast.makeText(context, "Escolha pelo menos um horário!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void efetuaAcaoBanco(boolean autorizado, boolean recusado, boolean disponivel) {
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        OnCompleteListener complete = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                SalaoVitinhoUtils.exibeDialogConfirmacao(context, "Ação efetuada com sucesso!", listener);
            }
        };

        final ArrayList<Horario> listaEscolhidos = clientesAdapter.getHorariosEscolhidos();
        int contadorRegistros = 0;
        for (Horario horario : listaEscolhidos) {
            contadorRegistros++;

            horario.setAutorizado(autorizado);
            horario.setRecusado(recusado);
            horario.setDisponivel(disponivel);
            horario.setVerificado(true);
            horario.setMotivo(motivo);

            String[] caminho = {SalaoVitinhoConstants.AGENDAMENTO, SalaoVitinhoConstants.VITINHO, SalaoVitinhoConstants.NAO_ATENDIDO,
                    horario.getDiaAtendimento(), horario.getHoraAtendimento()};
            if (contadorRegistros == listaEscolhidos.size()) {
                FirebaseUtils.getReferenceChild(caminho).setValue(horario).addOnCompleteListener(complete);
            }
            else {
                FirebaseUtils.getReferenceChild(caminho).setValue(horario);
            }
        }
    }

    private void montaListaClientesSolicitantes(ArrayList<Horario> listaClientesSolicitantes) {
        clientesAdapter = new AgendamentosAdapter(context, R.layout.layout_adapter_autorizar, listaClientesSolicitantes);
        listViewClientesSolicitantes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewClientesSolicitantes.setAdapter(clientesAdapter);
    }

    private void mostraDialogConfirmacaoCancelamento(final boolean autorizado, final boolean recusado, final boolean disponivel) {
        ArrayList<Horario> listaEscolhidos = clientesAdapter.getHorariosEscolhidos();
        if (listaEscolhidos.size() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmação");
            builder.setIcon(R.mipmap.ic_ok_icon);
            builder.setMessage("Você confirma a operação?");
            builder.setPositiveButton(SalaoVitinhoConstants.SIM, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    efetuaAcaoBanco(autorizado, recusado, disponivel);
                }
            });

            builder.setNegativeButton(SalaoVitinhoConstants.NAO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            Toast.makeText(context, "Escolha pelo menos um horário!", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostraDialogMensagem() {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_mensageiro, null);

        final Spinner motivosRecusa = (Spinner) view.findViewById(R.id.spinnerMotivoPreCadastrados);
        final Adapter adapterProfissional = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, motivosRecusaAtendimento);
        motivosRecusa.setAdapter((SpinnerAdapter) adapterProfissional);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setPositiveButton(SalaoVitinhoConstants.ENVIAR, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText mensagemMensageiro = (EditText) view.findViewById(R.id.editTextMensageiro);
                motivo = motivosRecusa.getSelectedItem().toString() + mensagemMensageiro.getText().toString();
                mostraDialogConfirmacaoCancelamento(false, true, false);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
