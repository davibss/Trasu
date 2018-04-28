package br.com.trasudev.trasu.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.entidades.TarefaIndividual;

public class TarefaAdapter extends BaseAdapter{
    private final Context context;
    private final List<TarefaIndividual> tarefas;

    public TarefaAdapter(Context context, List<TarefaIndividual> tarefas){
        this.context = context;
        this.tarefas = tarefas;
    }

    @Override
    public int getCount() {
        return tarefas != null ? tarefas.size() :0;
    }

    @Override
    public Object getItem(int i) {
        return tarefas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.tarefa_adapter, viewGroup, false);
        TextView txvNome = (TextView) viewInflate.findViewById(R.id.nome_tarefa);
        TextView txvPrioridade = (TextView) viewInflate.findViewById(R.id.prioridade_tarefa);
        TextView txvPrazo = (TextView) viewInflate.findViewById(R.id.prazo_tarefa);
        TarefaIndividual tarefa = tarefas.get(i);
        txvNome.setText("Nome:" + tarefa.getTar_nome());
        txvPrioridade.setText("Prioridade:" + tarefa.getTar_prioridade());
        txvPrazo.setText(subtrairDatas(tarefa));
        return viewInflate;
    }

    private String subtrairDatas(TarefaIndividual tarefa) {
        Calendar a = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date data = new Date();
        try {
            data = formato.parse(tarefa.getTar_dataFinal());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        a.setTime(data);//data maior
        Calendar b = Calendar.getInstance();
        b.setTime(new Date());// data menor
        a.add(Calendar.DATE, - b.get(Calendar.DAY_OF_MONTH));
        return String.valueOf(a.get(Calendar.DAY_OF_MONTH));
    }
}
