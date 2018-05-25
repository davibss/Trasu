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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.trasudev.trasu.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.lang.String;

import br.com.trasudev.trasu.entidades.Grupo;
import br.com.trasudev.trasu.entidades.TarefaIndividual;

public class CartListGroupAdapter extends RecyclerView.Adapter<CartListGroupAdapter.MyViewHolder> {
    private Context context;
    private List<Grupo> cartList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, integrantes, lider;
        public ImageView img_lider,menu_grupo;
        public ProgressBar progressBar;
        public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nome_group);
            progressBar = view.findViewById(R.id.progressBar);
            integrantes = view.findViewById(R.id.integrantes_group);
            lider = view.findViewById(R.id.lider_group);
            /*img_group = view.findViewById(R.id.img_group);*/
            img_lider = view.findViewById(R.id.img_lider);
            menu_grupo = view.findViewById(R.id.menu_grupo);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }


    public CartListGroupAdapter(Context context, List<Grupo> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_group, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Grupo item = cartList.get(position);
        holder.name.setText(String.valueOf("Nome: " + item.getGrp_nome()));
        holder.integrantes.setText(String.valueOf("Integrantes: " + item.getGrp_integrantes()));
        holder.lider.setText(String.valueOf("Líder: " + item.getGrp_lider()));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public Grupo getItem(int position){
        return cartList.get(position);
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Grupo item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
