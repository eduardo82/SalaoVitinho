package br.com.eduardo.salaovitinho.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.eduardo.salaovitinho.R;
import br.com.eduardo.salaovitinho.model.Mensagem;

/**
 * Created by Eduardo on 30/11/2017.
 */

public class MensagemAdapter extends BaseAdapter {

    private final Context context;
    private final List<Mensagem> mensagens;

    public MensagemAdapter(Context context, List<Mensagem> mensagens) {
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

        Mensagem mensagemCliente = mensagens.get(position);

        if (mensagemCliente.getRemetente() != null) {
            header.setText(mensagemCliente.getRemetente() + " - " + mensagemCliente.getTelefone());
        }

        if (mensagemCliente.getMensagem() != null && mensagemCliente.getMensagem().length() > 0) {
            mensagem.setText(mensagemCliente.getMensagem());
        }
        else {
            mensagem.setText(mensagemCliente.getResposta());
        }

        return view;
    }
}
