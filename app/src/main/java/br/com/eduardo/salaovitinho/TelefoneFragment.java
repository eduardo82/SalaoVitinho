package br.com.eduardo.salaovitinho;


import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.eduardo.salaovitinho.adapter.TelefonesAdapter;
import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.formatter.BrPhoneNumberFormatter;
import br.com.eduardo.salaovitinho.model.Telefone;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class TelefoneFragment extends Fragment {

    private View view;
    private Context context;
    private Button botaoAdicionarTelefone;
    private ListView listViewTelefones;
    private TelefonesAdapter mensagemAdapter;
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
                exibeDialogInformacoesTelefone(context, null, false);
            }
        });

        onClickLista();

        return view;
    }

    private void onClickLista() {
        listViewTelefones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Telefone telefone = telefones.get(position);
                exibeDialogInformacoesTelefone(context, telefone, true);
            }
        });
    }

    private void exibeDialogInformacoesTelefone(final Context context, Telefone telefone, boolean apagaRegistro) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_adiciona_telefone, null);

        final Switch autorizado = view.findViewById(R.id.switchAdicionarTelefone);
        final EditText telephone = view.findViewById(R.id.editTextTelefone);

        if (telefone != null) {
            autorizado.setChecked(telefone.isAutorizado());
            telephone.setText(telefone.getNumero());
        }

        autorizado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String valorComponente = isChecked ? "Autorizado" : "Negado";
                Toast.makeText(context, valorComponente, Toast.LENGTH_SHORT).show();
            }
        });

        BrPhoneNumberFormatter addLineNumberFormatter = new BrPhoneNumberFormatter(new WeakReference<>(telephone));
        telephone.addTextChangedListener(addLineNumberFormatter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String numero = ((EditText) view.findViewById(R.id.editTextTelefone)).getText().toString();

                if (numero.length() > 0 && numero.matches(".(31.)\\s9[7-9][0-9]{3}-[0-9]{4}")) {
                    Telefone telefone = new Telefone();
                    telefone.setNumero(numero);
                    telefone.setAutorizado(autorizado.isChecked());
                    FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.FIREBASE_NODE_TELEFONES, numero).setValue(telefone);
                    telefones.clear();
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(context, "O telefone deve ser válido e estar no formato (31) 99999-9999.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Fechar", null);

        if (apagaRegistro) {
            builder.setNeutralButton("Apagar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String numero = ((EditText) view.findViewById(R.id.editTextTelefone)).getText().toString();
                    FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.FIREBASE_NODE_TELEFONES, numero).removeValue();
                    telefones.clear();
                    dialog.dismiss();
                }
            });
        }

        builder.setCancelable(true);
        builder.create().show();
    }

}
