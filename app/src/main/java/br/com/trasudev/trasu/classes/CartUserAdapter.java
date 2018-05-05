package br.com.trasudev.trasu.classes;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.trasudev.trasu.R;
import java.util.List;
import java.lang.String;
import br.com.trasudev.trasu.entidades.Usuario;

public class CartUserAdapter extends RecyclerView.Adapter<CartUserAdapter.MyViewHolder> {
    private Context context;
    private List<Usuario> cartList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, email;
        public ImageView img_move;
        //public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nome_usuario);
            email = view.findViewById(R.id.email_usuario);
            img_move = view.findViewById(R.id.move_user);
            //viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
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
        holder.name.setText(String.valueOf("Nome: " + item.getUser_nome()));
        holder.email.setText(String.valueOf("Email: " +  item.getUser_email()));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
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