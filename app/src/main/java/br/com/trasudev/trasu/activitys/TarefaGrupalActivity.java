package br.com.trasudev.trasu.activitys;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.entidades.Grupo;
import br.com.trasudev.trasu.entidades.TarefaIndividual;
import br.com.trasudev.trasu.entidades.Usuario;
import br.com.trasudev.trasu.fragments.TarefaFragment;
import br.com.trasudev.trasu.fragments.TarefaGrupalFragment;

public class TarefaGrupalActivity extends AppCompatActivity implements
        TarefaGrupalFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa_grupal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Grupo grupo = (Grupo)getIntent().getSerializableExtra("grupoOBJ");
        getSupportActionBar().setTitle(grupo.getGrp_nome());
        getSupportActionBar().setSubtitle("Tarefas");
        loadFragment(grupo);
    }

    private void loadFragment(Grupo grupo){
        Fragment fragment = new TarefaGrupalFragment();
        Bundle data = new Bundle();
        data.putSerializable("grupoOBJ",grupo);
        fragment.setArguments(data);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameTarefa, fragment, "TAG_TAREFA");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  android.R.id.home:
                finish();
                break;
            default:break;
        }
        return  true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
