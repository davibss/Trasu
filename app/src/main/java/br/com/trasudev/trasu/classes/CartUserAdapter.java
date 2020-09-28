package br.com.trasudev.trasu.classes;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.trasudev.trasu.R;
import java.util.List;
import java.lang.String;
import br.com.trasudev.trasu.entidades.Usuario;

public class CartUserAdapter extends RecyclerView.Adapter<CartUserAdapter.MyViewHolder> {
    private Context context;
    private List<Usuario> cartList;
    private CustomItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name, email, textView;
        public ImageView img_move,img_integrante;
        public ProgressBar progressBar;
        public CheckBox checkBox;
        //public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nome_usuario);
            email = view.findViewById(R.id.email_usuario);
            img_move = view.findViewById(R.id.move_user);
            img_integrante = view.findViewById(R.id.img_group);
            checkBox = view.findViewById(R.id.check_user);
            progressBar = view.findViewById(R.id.progressBar);
            textView = view.findViewById(R.id.viewApoio);
            img_move.setOnClickListener(this);
            //viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onClick(view, getAdapterPosition());
        }
    }


    public CartUserAdapter(Context context, List<Usuario> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_usuarios, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Usuario item = cartList.get(position);
        holder.name.setText(String.valueOf("" + item.getUser_nome()));
        holder.email.setText(String.valueOf("" +  item.getUser_email()));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void setClickListener(CustomItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    public Usuario getItem(int position){
        return cartList.get(position);
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Usuario item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}