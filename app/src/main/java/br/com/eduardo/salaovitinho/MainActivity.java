package br.com.eduardo.salaovitinho;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.model.Horario;
import br.com.eduardo.salaovitinho.model.Mensagem;
import br.com.eduardo.salaovitinho.util.CircleTransform;
import br.com.eduardo.salaovitinho.util.FirebaseUtils;
import br.com.eduardo.salaovitinho.util.NotificacaoUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
    private static final int RC_SIGN_IN = 123;
    FirebaseAuth auth;
    TextView numeroSolicitacoes;
    TextView textViewNomeUsuario;
    TextView textViewEmailUsuario;
    ImageView imageViewUsuario;
    int contadorSolicitacoes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        numeroSolicitacoes = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_agendamentos));
        numeroSolicitacoes.setGravity(Gravity.CENTER_VERTICAL);
        numeroSolicitacoes.setTypeface(null, Typeface.BOLD);
        numeroSolicitacoes.setTextColor(getResources().getColor(R.color.colorAccent));

        View viewHeader = navigationView.getHeaderView(0);
        textViewEmailUsuario = viewHeader.findViewById(R.id.textViewEmailUsuario);
        textViewNomeUsuario = viewHeader.findViewById(R.id.textViewNomeUsuario);
        imageViewUsuario = viewHeader.findViewById(R.id.imageViewUsuario);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.size() > 0) {
                if (extras.get("mensagem") != null) {
                    getIntent().putExtra("telefone", extras.getString("telefone"));
                    getIntent().putExtra("lido", extras.getString("lido"));
                    getIntent().putExtra("mensagem", extras.getString("mensagem"));

                    getFragmentManager().beginTransaction().replace(R.id.conteudo,
                            new DetalheMensagemFragment()).commit();
                    NotificacaoUtil.cancelaNotificacao(context, 2);
                }
                else if (extras.get("agendamentos") != null){
                    getFragmentManager().beginTransaction().replace(R.id.conteudo,
                            new AutorizadorFragment()).commit();
                    NotificacaoUtil.cancelaNotificacao(context, 1);
                }
            }
            else {
                observerAgendamentos();
            }
        }
        else {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build())).build(), RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                textViewEmailUsuario.setText(auth.getCurrentUser().getEmail());
                textViewNomeUsuario.setText(auth.getCurrentUser().getDisplayName());
                Picasso.with(this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .error(R.mipmap.ic_launcher_round)
                    .resize(180, 180)
                    .transform(new CircleTransform())
                    .into(imageViewUsuario);
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_agendamentos) {
            fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new AutorizadorFragment()).commit();
        } else if (id == R.id.nav_agendamentos_marcados) {
            fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new VisualizarAgendamentosFragment()).commit();
        } else if (id == R.id.nav_reservar_agendamentos) {
            fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new AgendamentoFragment()).commit();
        } else if (id == R.id.nav_agenda) {
            fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new AgendaFragment()).commit();
        } else if (id == R.id.nav_mensagem) {
            fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new MensagemFragment()).commit();
        } else if (id == R.id.nav_telefones) {
            fragmentManager.beginTransaction().replace(R.id.conteudo,
                    new TelefoneFragment()).commit();
        } else if (id == R.id.nav_sair) {
            finishAffinity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void observerAgendamentos() {
        try {

            FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO, SalaoVitinhoConstants.VITINHO,
                    SalaoVitinhoConstants.NAO_ATENDIDO).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    trataGeracaoAlertasNovosAgendamentos(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trataGeracaoAlertasNovosAgendamentos(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {

            for (DataSnapshot hora : dataSnapshot.getChildren()) {
                Horario horario = hora.getValue(Horario.class);
                if (horario != null) {
                    if (!horario.isAutorizado() && !horario.isDisponivel() && !horario.isRecusado()) {
                        contadorSolicitacoes++;
                    }
                }
            }

            if (contadorSolicitacoes > 0) {
                numeroSolicitacoes.setText(String.valueOf(contadorSolicitacoes) + "+");
                Toast.makeText(context, "Você possui solicitações de agendamentos para autorização", Toast.LENGTH_LONG).show();
                Intent it = new Intent(context, MainActivity.class);
                it.putExtra("agendamentos", true);
                NotificacaoUtil.geraNotificacaoSimples(getBaseContext(), it, SalaoVitinhoConstants.INFORMACAO, "Você possui uma nova solicitação de agendamento!", 1);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Mensagem resultado = dataSnapshot.getValue(Mensagem.class);

                if (resultado != null) {
                    if (!resultado.isLido()) {
                        FirebaseUtils.getReferenceChild("").removeEventListener(this);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("mensagem", resultado.getMensagem());
                        intent.putExtra("telefone", resultado.getTelefone());
                        intent.putExtra("lido", resultado.isLido());

                        NotificacaoUtil.geraNotificacaoSimples(context, intent,  SalaoVitinhoConstants.INFORMACAO,
                                "Sua mensagem foi respondida.\n Resposta -> " + resultado.getMensagem(), 2);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //FirebaseUtils.getReferenceChild(caminho).addValueEventListener(listener);
    }
}