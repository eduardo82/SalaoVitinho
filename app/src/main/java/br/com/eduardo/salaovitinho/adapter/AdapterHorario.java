package br.com.eduardo.salaovitinho.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.List;

/**
 * Created by Eduardo on 20/02/2018.
 */
public class AdapterHorario extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> horarios;
    private final List<String> horariosAgendados;

    @Override
    public int getCount() {
        return horarios != null ? horarios.size() : 0;
    }

    public AdapterHorario(Context context, List<String> horarios, List<String> horariosAgendados) {
        super(context, android.R.layout.simple_list_item_single_choice, horarios);
        this.context = context;
        this.horarios = horarios;
        this.horariosAgendados = horariosAgendados;
    }

    @Override
    public String getItem(int position) {
        return horarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        convertView = inflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);
        CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
        checkedTextView.setText(getItem(position));

        if (horariosAgendados.contains(horarios.get(position))) {
            checkedTextView.setClickable(false);
            checkedTextView.setPressed(false);
            checkedTextView.setEnabled(false);

            checkedTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    ((CheckedTextView) v).toggle();
                }
            });
        }

        return convertView;
    }
}
