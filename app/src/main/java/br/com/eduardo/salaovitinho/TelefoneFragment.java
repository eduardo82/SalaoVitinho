package br.com.eduardo.salaovitinho;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.eduardo.salaovitinho.adapter.TelefonesAdapter;
import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.model.Telefone;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;
import br.com.eduardo.salaovitinho.util.SalaoVitinhoUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class TelefoneFragment extends Fragment {

    private View view;
    private Context context;
    private Button botaoAdicionarTelefone;
    private ListView listViewTelefones;
    TelefonesAdapter mensagemAdapter;
    private List<Telefone> telefones = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_telefone, container, false);
        context = view.getContext();

        botaoAdicionarTelefone = view.findViewById(R.id.buttonAddTelefone);
        listViewTelefones = view.findViewById(R.id.listViewTelefones);

        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.FIREBASE_NODE_TELEFONES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                telefones.clear();
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    GenericTypeIndicator<Map<String, Telefone>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Telefone>>() {};
                    Map<String, Telefone> telefonesBanco = dataSnapshot.getValue(genericTypeIndicator);

                    if (telefonesBanco != null) {
                        for (Map.Entry<String, Telefone> telefone : telefonesBanco.entrySet()) {
                            telefones.add(telefone.getValue());
                        }
                    }

                    if (telefones.size() > 0) {
                        mensagemAdapter = new TelefonesAdapter(context, telefones);
                        listViewTelefones.setAdapter(mensagemAdapter);
                        onClickLista();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        botaoAdicionarTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalaoVitinhoUtils.exibeDialogInformacoesUsuario(context, null);
            }
        });

        return view;
    }

    private void onClickLista() {
        listViewTelefones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Telefone telefone = telefones.get(position);
                SalaoVitinhoUtils.exibeDialogInformacoesUsuario(context, telefone);
            }
        });
    }

}
