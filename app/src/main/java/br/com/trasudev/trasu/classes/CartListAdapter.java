package br.com.trasudev.trasu.classes;

import android.content.Context;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.trasudev.trasu.R;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.lang.String;

import br.com.trasudev.trasu.entidades.TarefaIndividual;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> {
    private Context context;
    private List<TarefaIndividual> cartList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, prioridade, prazo;
        public ImageView thumbnail;
        public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nome_tarefa);
            prioridade = view.findViewById(R.id.prioridade_tarefa);
            prazo = view.findViewById(R.id.prazo_tarefa);
            thumbnail = view.findViewById(R.id.img_tarefa);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }


    public CartListAdapter(Context context, List<TarefaIndividual> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final TarefaIndividual item = cartList.get(position);
        Resources res = Resources.getSystem();
        holder.name.setText(res.getString(R.string.placeNome,item.getTar_nome()));
        holder.prioridade.setText(res.getString(R.string.placePrioridade,item.getTar_prioridade()));
        holder.prazo.setText(subtrairDatas(item));
        /*Glide.with(context)
                .load(R.drawable.ic_group_black_24dp)
                .into(holder.thumbnail);*/
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(TarefaIndividual item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
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
