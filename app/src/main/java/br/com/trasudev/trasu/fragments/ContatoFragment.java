package br.com.trasudev.trasu.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.activitys.TarefaGrupalActivity;
import br.com.trasudev.trasu.classes.CartListGroupAdapter;
import br.com.trasudev.trasu.classes.CartUserAdapter;
import br.com.trasudev.trasu.classes.CircleTransform;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.entidades.Grupo;
import br.com.trasudev.trasu.entidades.Usuario;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

public class ContatoFragment extends Fragment {

    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    private RecyclerView recyclerViewIntegrante;
    private List<Usuario> cartListIntegrante;
    private CartUserAdapter mAdapterIntegrante;
    private CoordinatorLayout coordinatorLayout;

    private ArrayList<String> numeroContatos;

    private TextView textView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String TAG = "EINSZWEIDREI";

    private Context context;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = Conexao.getFirebaseUser();
        verificarUser();
    }

    private ArrayList<String> getContactList() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            ArrayList<String> phoneList = new ArrayList<>();
            ArrayList<String> phoneList2 = new ArrayList<>();
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNo.replaceAll("[^0-9]",""));
                    }
                    pCur.close();
                }
            }
            Collections.sort(phoneList);
            for (int i=0;i<phoneList.size();i++){
                if (i!=0 && phoneList.get(i-1).equals(phoneList.get(i))){
                    //
                }else{
                    phoneList2.add(phoneList.get(i));
                    //Log.i(TAG,phoneList.get(i));
                }
            }
            return phoneList2;
        }
        if(cur!=null){
            cur.close();
        }
        return null;
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (!calledAlready) {
            firebaseDatabase.setPersistenceEnabled(true);
            calledAlready = true;
        }
        databaseReference = firebaseDatabase.getReference();
    }

    private void verificarUser() {
        if (firebaseUser == null){
            throw new RuntimeException(getContext().toString()
                    + " must implement OnFragmentInteractionListener");
        }else{
            //
        }
    }

    public ContatoFragment() {
        //
    }

    public static ContatoFragment newInstance(String param1, String param2) {
        ContatoFragment fragment = new ContatoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            numeroContatos = getArguments().getStringArrayList("arraylist");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contatos, container,false);
        inicializarComponentes(rootView);
        recyclerViewEvent(rootView);
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void recyclerViewEvent(View rootView) {
        recyclerViewIntegrante = rootView.findViewById(R.id.recycler_view);
        cartListIntegrante = new ArrayList<>();
        mAdapterIntegrante = new CartUserAdapter(getActivity(), cartListIntegrante){
            @Override
            public void onBindViewHolder(final MyViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                final Usuario item = cartListIntegrante.get(position);
                mostrarIconeUsuarios(holder,item);
            }
        };
        RecyclerView.LayoutManager mLayoutManagerI = new LinearLayoutManager(context.getApplicationContext());
        recyclerViewIntegrante.setLayoutManager(mLayoutManagerI);
        recyclerViewIntegrante.setItemAnimator(new DefaultItemAnimator());
        recyclerViewIntegrante.setAdapter(mAdapterIntegrante);
        recyclerViewIntegrante.setLongClickable(true);
        /*startContatos();*/
        eventoDatabaseCard();
    }

    private void startContatos(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                numeroContatos = getContactList();
                eventoDatabaseCard();
            }
        }).start();
    }

    private void eventoDatabaseCard() {
        databaseReference.child("usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartListIntegrante.clear();
                for (DataSnapshot obj: dataSnapshot.getChildren()){
                    Usuario u = obj.getValue(Usuario.class);
                    String numero = u.getUser_telefone().replaceAll("[^0-9]","");
                    if (numeroContatos.contains(numero)){
                        cartListIntegrante.add(u);
                    }
                }
                mAdapterIntegrante.notifyDataSetChanged();
                if (cartListIntegrante.isEmpty()){
                    textView.setVisibility(View.VISIBLE);
                }else{
                    textView.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void mostrarIconeUsuarios(final CartUserAdapter.MyViewHolder holder,final Usuario item){
        databaseReference.child("usuario").child(item.getUser_id()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Usuario user = dataSnapshot.getValue(Usuario.class);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        storageReference = FirebaseStorage.getInstance().getReference();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                StorageReference filePath = storageReference.child("img_profiles").
                                        child(user.getUser_icon());
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        Activity activity = (Activity) context;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Glide.with(context.getApplicationContext())
                                                        .load(uri)
                                                        .transition(DrawableTransitionOptions.withCrossFade())
                                                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                                                        .apply(RequestOptions.centerCropTransform())
                                                        .apply(RequestOptions.fitCenterTransform())
                                                        .apply(RequestOptions.bitmapTransform(new CircleTransform()))
                                                        .thumbnail(0.5f)
                                                        .into(holder.img_integrante);
                                                holder.progressBar.setVisibility(ProgressBar.INVISIBLE);
                                            }
                                        });
                                    }
                                });
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void inicializarComponentes(View rootView){
        textView = rootView.findViewById(R.id.textViewNothing);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
