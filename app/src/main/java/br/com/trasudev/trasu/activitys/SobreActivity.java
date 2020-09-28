package br.com.trasudev.trasu.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import br.com.trasudev.trasu.R;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebView textView = findViewById(R.id.textSobre);
        textView.setBackgroundColor(0);
        String text;
        text = "<html><body><p align=\"justify\">";
        text+= "Aplicativo desenvolvido por André Luís e Davi Barbosa " +
                "para o Trabalho de Conclusão de Curso do " +
                "3º Ano de Informática do Instituto Federal de Sergipe";
        text+= "</p></body></html>";
        textView.loadData(text, "text/html", "utf-8");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
