package br.com.eduardo.salaovitinho.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.eduardo.salaovitinho.R;
import br.com.eduardo.salaovitinho.model.Horario;

/**
 * Created by eduardo.vasconcelos on 09/11/2017.
 */

public class AgendamentosAdapter extends ArrayAdapter<Horario> {

    private final Context context;
    private final List<Horario> horariosAgendados;

    public ArrayList<Horario> getHorariosEscolhidos() {
        return horariosEscolhidos;
    }

    public void setHorariosEscolhidos(ArrayList<Horario> horariosEscolhidos) {
        this.horariosEscolhidos = horariosEscolhidos;
    }

    private ArrayList<Horario> horariosEscolhidos = new ArrayList<>();

    public AgendamentosAdapter(@NonNull Context context, @LayoutRes int id, @NonNull ArrayList<Horario> objects) {
        super(context, id, objects);
        this.context = context;
        this.horariosAgendados = objects;
    }

    @Override
    public int getCount() {
        return horariosAgendados != null ? horariosAgendados.size() : 0;
    }

    @Override
    public Horario getItem(int position) {
        return horariosAgendados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_adapter_autorizar, parent, false);
        final TextView dataHora = view.findViewById(R.id.textViewDataHoraSolicitada);
        final TextView solicitante = view.findViewById(R.id.textViewSolicitante);
        final CheckBox checkedTextView = view.findViewById(R.id.checkBoxAcao);

        final Horario item = getItem(position);

        String horaAtendimento = "";
        if (item.getHoraAtendimento() != null) {
            horaAtendimento = item.getHoraAtendimento().split(" ")[0];
        }

        dataHora.setText(item.getDiaAtendimento().replace("_", "/") + " - " +  horaAtendimento);
        solicitante.setText(item.getNome() + " - " + item.getTelefone());
        checkedTextView.setClickable(true);
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextView.isChecked()) {
                    horariosEscolhidos.add(item);
                    checkedTextView.setChecked(true);
                }
                else {
                    checkedTextView.setChecked(false);
                    horariosEscolhidos.remove(item);
                }
            }
        });

        return view;
    }
}
