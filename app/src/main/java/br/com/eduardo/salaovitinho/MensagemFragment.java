package br.com.eduardo.salaovitinho;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.eduardo.salaovitinho.adapter.MensagemAdapter;
import br.com.eduardo.salaovitinho.banco.MensagemDB;
import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.model.Mensagem;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;
import br.com.eduardo.salaovitinho.util.SalaoVitinhoUtils;

public class MensagemFragment extends Fragment {

    View view;
    SwipeMenuListView mensagensClientesListView;
    List<Mensagem> mensagensClientes;
    Mensagem mensagemLida;
    MensagemDB mensagemDB;

    Context context = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_leitor_mensagem, viewGroup, false);
        context = view.getContext();

        mensagemDB = new MensagemDB(context);
        mensagensClientes = new ArrayList<Mensagem>();
        observerMensagemUsuario();

        return view;
    }

    private void trataListViewMensagensClientes() {
        mensagensClientesListView = (SwipeMenuListView) view.findViewById(R.id.listViewMensagens);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setTitle("apagar");
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(18);
                deleteItem.setWidth(200);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        mensagensClientesListView.setMenuCreator(creator);
        mensagensClientesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            mensagemLida = mensagensClientes.get(position);

            Bundle argumento = new Bundle();
            argumento.putString("telefone", mensagemLida.getTelefone());
            argumento.putString("mensagem", mensagemLida.getMensagem());
            argumento.putString("remetente", mensagemLida.getRemetente());

            DetalheMensagemFragment detalhe = new DetalheMensagemFragment();
            detalhe.setArguments(argumento);

            getFragmentManager().beginTransaction().replace(R.id.conteudo, detalhe).commit();
            }
        });

        final DialogInterface.OnClickListener acaoBotaoSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mensagemDB.deletar(mensagemLida);
                FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.MENSAGEM, SalaoVitinhoConstants.VITINHO, mensagemLida.getTelefone()).removeValue();
                mensagensClientes.remove(mensagemLida);

                getFragmentManager().beginTransaction().detach(MensagemFragment.this)
                        .attach(MensagemFragment.this).commit();
            }
        };

        mensagensClientesListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        mensagemLida = mensagensClientes.get(position);
                        SalaoVitinhoUtils.exibeDialogConfirmacao(context, "Você deseja apagar a mensagem?", acaoBotaoSim, null, true);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


    }

    private void observerMensagemUsuario() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    GenericTypeIndicator<Map<String, Mensagem>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Mensagem>>() {};
                    Map<String, Mensagem> mensagems = dataSnapshot.getValue(genericTypeIndicator);

                    mensagensClientes.clear();
                    if (mensagems != null) {
                        for (Map.Entry<String, Mensagem> mensagem : mensagems.entrySet()) {
                            if (!mensagem.getValue().isLido()) {
                                if (mensagemDB.buscaUltimaMensagemTelefone(mensagem.getValue()).size() == 0) {
                                    mensagemDB.salvar(mensagem.getValue());
                                    FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.MENSAGEM, SalaoVitinhoConstants.VITINHO).removeEventListener(this);
                                }
                            }
                            mensagensClientes.add(mensagem.getValue());
                        }

                        trataListViewMensagensClientes();

                        if (mensagensClientes.size() > 0) {
                            MensagemAdapter mensagemAdapter = new MensagemAdapter(context, mensagensClientes);
                            mensagensClientesListView.setAdapter(mensagemAdapter);
                        }
                        else {
                            Toast.makeText(context, "Você não possui mensagens.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(context, "Você não possui mensagens.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "Você não possui mensagens.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.MENSAGEM, SalaoVitinhoConstants.VITINHO).addValueEventListener(listener);
    }
}
