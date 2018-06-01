package br.com.eduardo.salaovitinho.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.eduardo.salaovitinho.R;
import br.com.eduardo.salaovitinho.model.Telefone;
import br.com.eduardo.salaovitinho.util.ContactsUtil;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;

import static br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants.FIREBASE_NODE_TELEFONES;

public class TelefonesAdapter extends BaseAdapter {

    private final Context context;
    private List<Telefone> telefones;

    public TelefonesAdapter(Context context, List<Telefone> objects) {
        this.context = context;
        this.telefones = objects;
    }

    @Override
    public int getCount() {
        return telefones.size();
    }

    @Override
    public Telefone getItem(int position) {
        return telefones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_adapter_telefone, parent, false);
        final TextView numero = view.findViewById(R.id.textViewTelefone);
        final Switch autorizado = view.findViewById(R.id.switchAutorizado);
        final TextView nome = view.findViewById(R.id.textViewNomeContato);
        final ImageView novo = view.findViewById(R.id.imageViewNovo);

        final Telefone telefone = getItem(position);

        autorizado.setChecked(telefone.getAutorizado());
        numero.setText(telefone.getNumero());
        nome.setText(telefone.getNome());

        String numeroTelefone =  telefone.getNumero().replace("(","").replace(")", "").replace("-","").replace(" ","");
        Boolean estaSalvo = ContactsUtil.contatoEstaSalvoAgenda(context, numeroTelefone) || ContactsUtil.contatoEstaSalvoAgenda(context, numeroTelefone.substring(2));

        if (estaSalvo && telefone.getNovo() == true) {
            telefone.setNovo(false);
            telefone.setAutorizado(true);
            novo.setVisibility(View.GONE);
            FirebaseUtils.getReferenceChild(FIREBASE_NODE_TELEFONES, telefone.getNumero()).setValue(telefone);
        }
        else {
            if (telefone.getNovo() == false) {
                novo.setVisibility(View.GONE);
            }
        }

        autorizado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                telefone.setAutorizado(isChecked);
                telefone.setNovo(false);
                String valorCompenente = isChecked ? "Autorizado" : "Negado";
                Toast.makeText(context, valorCompenente, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
                FirebaseUtils.getReferenceChild(FIREBASE_NODE_TELEFONES, telefone.getNumero()).setValue(telefone);
            }
        });
        return view;
    }
}
