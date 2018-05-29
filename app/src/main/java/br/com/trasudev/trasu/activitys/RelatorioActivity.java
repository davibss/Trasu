package br.com.trasudev.trasu.activitys;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.classes.CartListAdapter;
import br.com.trasudev.trasu.classes.CartListGrupalAdapter;
import br.com.trasudev.trasu.entidades.TarefaGrupal;
import br.com.trasudev.trasu.entidades.TarefaIndividual;
import br.com.trasudev.trasu.entidades.Usuario;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

public class RelatorioActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private AlertDialog dialog;

    private TextView textView;
    private RecyclerView recyclerView;
    private List<TarefaIndividual> cartList;
    private CartListAdapter mAdapter;

    private void initFirebase() {
        FirebaseApp.initializeApp(getBaseContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (!calledAlready) {
            firebaseDatabase.setPersistenceEnabled(true);
            calledAlready = true;
        }
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFirebase();
        inicializarComponentes();
        recyclerViewEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void recyclerViewEvent() {
        recyclerView = findViewById(R.id.recycler_viewGrupal);
        //coordinatorLayout = findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        mAdapter = new CartListAdapter(getBaseContext(), cartList) {
            @Override
            public void onBindViewHolder(MyViewHolder holder, final int position) {
                super.onBindViewHolder(holder, position);
                holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflateDialog = getLayoutInflater();
                        View alertLayout = inflateDialog.inflate(R.layout.cadastrar_tarefa_layout, null);
                        AlertDialog.Builder alert = new AlertDialog.Builder(RelatorioActivity.this);
                        alert.setCustomTitle(customTitle(inflateDialog,mAdapter.getItem(position).getTar_nome()));
                        alert.setView(alertLayout);
                        dialog = alert.create();
                        dialog.show();
                        visualizarTarefa(alertLayout,mAdapter.getItem(position));
                    }});
                holder.menu.setVisibility(View.GONE);
            }};
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLongClickable(true);
        eventoDatabaseCard();
    }

    private View customTitle(LayoutInflater inflateDialog, String title){
        View customTitle = inflateDialog.inflate(R.layout.title_bar, null);
        ImageView imageView = customTitle.findViewById(R.id.btnVoltar);
        TextView textView = customTitle.findViewById(R.id.txtTitle);
        imageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        textView.setText(title);
        return customTitle;
    }

    private void visualizarTarefa(View alertLayout, final TarefaIndividual tarefa){
        final EditText editNome = alertLayout.findViewById(R.id.editTextTarNome);
        final EditText editDescricao = alertLayout.findViewById(R.id.editTextTarDesc);
        final RadioGroup group = (RadioGroup) alertLayout.findViewById(R.id.radioGroup);
        final EditText editPrazo = (EditText) alertLayout.findViewById(R.id.editTextTarPrazo);
        final CheckBox checkBoxNotificacao = (CheckBox) alertLayout.findViewById(R.id.checkBoxNotificacao);
        final TextView textView = alertLayout.findViewById(R.id.prioridadeText);
        final Button btnAlterar = (Button) alertLayout.findViewById(R.id.btnCadastrarTar);
        RadioButton buttonAlta = (RadioButton) group.findViewById(R.id.radioAlta);
        RadioButton buttonMedia = (RadioButton) group.findViewById(R.id.radioMedia);
        RadioButton buttonBaixa = (RadioButton) group.findViewById(R.id.radioBaixa);
        editDescricao.setBackgroundColor(Color.TRANSPARENT);
        textView.setVisibility(View.GONE);
        editNome.setVisibility(View.GONE);
        editDescricao.setKeyListener(null);
        group.setVisibility(View.GONE);
        editPrazo.setVisibility(View.GONE);
        checkBoxNotificacao.setVisibility(View.GONE);
        btnAlterar.setVisibility(View.GONE);
        buttonAlta.setVisibility(View.GONE);
        buttonMedia.setVisibility(View.GONE);
        buttonBaixa.setVisibility(View.GONE);
        btnAlterar.setVisibility(View.GONE);
        editDescricao.setText(tarefa.getTar_descricao());
    }

    private void eventoDatabaseCard() {
        databaseReference.child("usuario").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cartList.clear();
                        for (DataSnapshot obj: dataSnapshot.getChildren()){
                            Usuario u = obj.getValue(Usuario.class);
                            cartList.addAll(u.getTarefa_individual().values());
                        }
                        mAdapter.notifyDataSetChanged();
                        if (cartList.isEmpty()){
                            textView.setVisibility(View.VISIBLE);
                        }else{
                            textView.setVisibility(View.INVISIBLE);
                            Collections.sort(cartList, new Comparator<TarefaIndividual>() {
                                @Override
                                public int compare(TarefaIndividual o1, TarefaIndividual o2) {
                                    return converterData(o1.getTar_dataFinal()).
                                            compareTo(converterData(o2.getTar_dataFinal()));
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private Date converterData(String data){
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH");
        Date date = null;
        try {
            date = formato.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void inicializarComponentes() {
        textView = findViewById(R.id.textViewNothing);
    }
}
