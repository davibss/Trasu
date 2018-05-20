package br.com.trasudev.trasu.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.entidades.Grupo;
import br.com.trasudev.trasu.entidades.Usuario;

public class TarefaGrupalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa_grupal);
        TextView textView = findViewById(R.id.textViewGrupal);
        Grupo grupo = (Grupo)getIntent().getSerializableExtra("grupoOBJ");
        //Log.d("IDGRP",grupo.getGrp_nome());
        for (Usuario u : grupo.getIntegrantes().values()) {
            //Log.d("USUARIOS",u.getUser_nome());
        }
        textView.setText(grupo.getGrp_nome());
    }
}
