package br.com.trasudev.trasu.classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.trasudev.trasu.R;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
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
        public TextView name, prioridade, prazo, dias;
        public ImageView thumbnail;
        public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nome_tarefa);
            prioridade = view.findViewById(R.id.prioridade_tarefa);
            prazo = view.findViewById(R.id.prazo_tarefa);
            dias = view.findViewById(R.id.dias_rest);
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
        holder.name.setText(String.valueOf("Nome: " + item.getTar_nome()));
        holder.prioridade.setText(String.valueOf("Prioridade: " + item.getTar_prioridade()));
        prazoEvent(holder, item);
    }

    private void prazoEvent(MyViewHolder holder, TarefaIndividual item) {
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH");
        Calendar dataAtual = Calendar.getInstance();
        Calendar dataFinal = Calendar.getInstance();
        try {
            String data = formato.format(new Date());
            dataAtual.setTime(formato.parse(data));
            dataFinal.setTime(formato.parse(item.getTar_dataFinal()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dataAtual.before(dataFinal)){
            if (Integer.parseInt(subtrairDatas(item)) == 1){
                holder.dias.setText("Dia restante");
                holder.thumbnail.setImageResource(R.drawable.ic_error_outline_orange_96dp);
            }else if (Integer.parseInt(subtrairDatas(item)) == 2){
                holder.thumbnail.setImageResource(R.drawable.ic_error_outline_yellow_96dp);
            }else if (Integer.parseInt(subtrairDatas(item)) > 2){
                holder.thumbnail.setImageResource(R.drawable.ic_error_outline_green_96dp);
            }
            holder.prazo.setText(subtrairDatas(item));
        }else if (dataAtual.after(dataFinal)){
            holder.dias.setText("Expirado");
            holder.thumbnail.setImageResource(R.drawable.ic_cancel_black_24dp);
            holder.prazo.setText("0");
        }else if (dataAtual.equals(dataFinal)){
            holder.thumbnail.setImageResource(R.drawable.ic_error_outline_red_96dp);
            holder.prazo.setText("0");
        }

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
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH");
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

        /*String retorno = null;


        if (b.getTime().before(a.getTime())){
            retorno = ;
        }else if (b.after(a)){
            retorno = "Expirado";
        }else if (b.equals(a)){
            retorno = "0";
        }*/
        return String.valueOf(a.get(Calendar.DAY_OF_MONTH));
    }
}
