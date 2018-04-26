package br.com.eduardo.salaovitinho.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.eduardo.salaovitinho.R;
import br.com.eduardo.salaovitinho.model.Horario;

/**
 * Created by Eduardo on 30/11/2017.
 */

public class ClientesAgendadosAdapter extends BaseAdapter {

    private final Context context;
    private final List<Horario> mensagens;

    public ClientesAgendadosAdapter(Context context, List<Horario> mensagens) {
        this.context = context;
        this.mensagens = mensagens;
    }


    @Override
    public int getCount() {
        return mensagens.size();
    }

    @Override
    public Object getItem(int position) {
        return mensagens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_adapter_leitor_mensagem_usuario, parent, false);
        TextView header = (TextView) view.findViewById(R.id.textViewCabecalhoMensagem);
        TextView mensagem = (TextView) view.findViewById(R.id.textViewMensagem);

        Horario horarioCliente = mensagens.get(position);

        header.setText(horarioCliente.getNome() + " - " + horarioCliente.getTelefone());
        mensagem.setText(horarioCliente.getHoraAtendimento());

        return view;
    }
}
