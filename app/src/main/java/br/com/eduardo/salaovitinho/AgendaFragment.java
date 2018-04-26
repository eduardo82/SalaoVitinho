package br.com.eduardo.salaovitinho;

import android.app.Activity;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.model.Agenda;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;
import br.com.eduardo.salaovitinho.util.SalaoVitinhoUtils;

public class AgendaFragment extends Fragment {

    View view;
    Context context;
    CalendarView diaAgenda;
    EditText primeiroPeriodoInicio;
    EditText primeiroPeriodoFim;
    EditText segundoPeriodoInicio;
    EditText segundoPeriodoFim;
    Button criaAgenda;
    Button limparAgenda;
    String diaEscolhido;
    CheckBox checkAgendaPadrao;
    CheckBox checkCancelaAgenda;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_agenda, viewGroup, false);
        context = view.getContext();

        diaAgenda = (CalendarView) view.findViewById(R.id.calendarViewAgenda);
        primeiroPeriodoInicio = (EditText) view.findViewById(R.id.primeiroPeriodoInicio);
        primeiroPeriodoFim = (EditText) view.findViewById(R.id.primeiroPeriodoFim);
        segundoPeriodoInicio = (EditText) view.findViewById(R.id.segundoPeriodoInicio);
        segundoPeriodoFim = (EditText) view.findViewById(R.id.segundoPeriodoFim);
        criaAgenda = (Button) view.findViewById(R.id.buttonCriaAgenda);
        limparAgenda = (Button) view.findViewById(R.id.buttonLimparAgenda);

        checkAgendaPadrao = (CheckBox) view.findViewById(R.id.checkBoxAgendaPadrao);
        checkCancelaAgenda = (CheckBox) view.findViewById(R.id.checkBoxCancelaAgenda);

        checkAgendaPadrao.setText("AGENDA PADRÃO (08:00 / 20:00)");
        checkCancelaAgenda.setText("CANCELA AGENDA DO DIA");

        primeiroPeriodoFim.setEnabled(false);
        segundoPeriodoInicio.setEnabled(false);
        segundoPeriodoFim.setEnabled(false);


        diaAgenda.setMinDate(new Date().getTime());
        diaAgenda.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                limpaAgenda();
                diaEscolhido = retornaDiaMesCorreto(dayOfMonth) + "/" + retornaDiaMesCorreto(month) + 1 + "/" + year;
            }
        });

        criaEventosInputs(primeiroPeriodoInicio, primeiroPeriodoFim);
        criaEventosInputs(primeiroPeriodoFim, segundoPeriodoInicio);
        criaEventosInputs(segundoPeriodoInicio, segundoPeriodoFim);
        criaEventosInputs(segundoPeriodoFim, segundoPeriodoFim);

        criaAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((checkCancelaAgenda.isChecked() || checkAgendaPadrao.isChecked()) ||
                        (primeiroPeriodoInicio.length() > 0 && primeiroPeriodoFim.length() > 0 &&
                        segundoPeriodoFim.length() > 0 && segundoPeriodoInicio.length() > 0)) {
                    confirmaAgenda();
                }
                else {
                    Toast.makeText(context, "Preencha todos os horários!", Toast.LENGTH_LONG).show();
                }
            }
        });

        limparAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpaAgenda();
            }
        });

        return view;
    }

    private void limpaAgenda() {
        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        primeiroPeriodoInicio.setText(null);
        primeiroPeriodoFim.setText(null);
        segundoPeriodoInicio.setText(null);
        segundoPeriodoFim.setText(null);
        checkAgendaPadrao.setChecked(false);
        checkCancelaAgenda.setChecked(false);

        primeiroPeriodoInicio.setEnabled(true);
        primeiroPeriodoFim.setEnabled(false);
        segundoPeriodoInicio.setEnabled(false);
        segundoPeriodoFim.setEnabled(false);
    }

    private void confirmaAgenda() {
        DialogInterface.OnClickListener acaoBotaoSim =  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat sdf = new SimpleDateFormat(SalaoVitinhoConstants.FORMAT_DATA);
                String selectedDate = sdf.format(new Date(diaAgenda.getDate()));

                Agenda agenda = new Agenda();

                agenda.setDia(selectedDate);
                if (checkAgendaPadrao.isChecked() || checkCancelaAgenda.isChecked()) {
                    agenda.setPrimeiraHoraFim("");
                    agenda.setPrimeiraHoraInicio("");
                    agenda.setSegundaHoraFim("");
                    agenda.setSegundaHoraInicio("");
                }
                else {
                    agenda.setPrimeiraHoraFim(primeiroPeriodoFim.getText().toString());
                    agenda.setPrimeiraHoraInicio(primeiroPeriodoInicio.getText().toString());
                    agenda.setSegundaHoraFim(segundoPeriodoFim.getText().toString());
                    agenda.setSegundaHoraInicio(segundoPeriodoInicio.getText().toString());
                }
                agenda.setCancelada(checkCancelaAgenda.isChecked());
                agenda.setHoraPadrao(checkAgendaPadrao.isChecked());
                salvaAgendaFirebase(agenda, selectedDate);
            }
        };
        SalaoVitinhoUtils.exibeDialogConfirmacao(context, "Você confirma a criação da agenda?", acaoBotaoSim, null, true);
    }

    private void salvaAgendaFirebase(Agenda agenda, String dataSelecionada) {
        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDA, SalaoVitinhoConstants.VITINHO, dataSelecionada)
                .setValue(agenda).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                limpaAgenda();
                operacaoRealizadoComSucesso();
            }
        });
    }

    private void operacaoRealizadoComSucesso() {
        SalaoVitinhoUtils.exibeDialogConfirmacao(context, "Agenda criada com sucesso!", null);
    }

    private String retornaDiaMesCorreto(int parametro) {
        if (parametro < 10) {
            return "0" + parametro;
        }
        return String.valueOf(parametro);
    }

    private void criaEventosInputs(final EditText input, final EditText proximo) {
        input.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                escondeTeclado();
                mostraIntervaloPrimeiroPeriodo(input, proximo);
            }
            }
        });

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            escondeTeclado();
            mostraIntervaloPrimeiroPeriodo(input, proximo);
            }
        });
    }

    private void escondeTeclado() {
        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void mostraIntervaloPrimeiroPeriodo(final EditText input, final EditText proximoInput) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                input.setText(retornaDiaMesCorreto(hourOfDay) + ":" + retornaDiaMesCorreto(minute));
                input.setEnabled(false);
                proximoInput.setEnabled(true);
                proximoInput.requestFocus();
            }
        };

        TimePickerDialog dialog = new TimePickerDialog(context, R.style.TimePicker, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }
}
