package br.com.trasudev.trasu.activitys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

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
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.classes.CircleTransform;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.entidades.TarefaGrupal;
import br.com.trasudev.trasu.entidades.TarefaIndividual;
import br.com.trasudev.trasu.entidades.Usuario;
import br.com.trasudev.trasu.fragments.HomeFragmentTab;
import br.com.trasudev.trasu.fragments.TabTarefaGrupalFragment;
import br.com.trasudev.trasu.fragments.TarefaFragment;
import br.com.trasudev.trasu.fragments.ContatoFragment;
import br.com.trasudev.trasu.fragments.PerfilFragment;
import br.com.trasudev.trasu.fragments.GrupoFragment;
import br.com.trasudev.trasu.fragments.SettingsFragment;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

public class MainActivity extends AppCompatActivity implements
        TabTarefaGrupalFragment.OnFragmentInteractionListener,
    TarefaFragment.OnFragmentInteractionListener,
    ContatoFragment.OnFragmentInteractionListener,
    PerfilFragment.OnFragmentInteractionListener,
    GrupoFragment.OnFragmentInteractionListener,
    HomeFragmentTab.OnFragmentInteractionListener,
    SettingsFragment.OnFragmentInteractionListener{
    private static FirebaseUser firebaseUser;
    private static StorageReference storageReference;
    private ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    public static TextView txtWebsite;
    public static  TextView txtName;
    public static TextView txtPontos;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    /*private static final String urlNavHeaderBg =
            "https://i.imgur.com/SGhvkjv.jpg";*/
    private static final String urlNavHeaderBg =
            "https://www.setaswall.com/wp-content/uploads/2017/06/Material-Backgrounds-02-1920-x-1080-768x432.png";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_TAREFA = "tarefa";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_TAREFA;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load tarefa fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = Conexao.getFirebaseUser();
        verificarUser();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (!calledAlready) {
            firebaseDatabase.setPersistenceEnabled(true);
            calledAlready = true;
        }
        databaseReference = firebaseDatabase.getReference();
    }

    private void verificarUser() {
        if (firebaseUser == null){
            finish();
        }else{
            //
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirebase();
        setPassword();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        txtPontos = (TextView) navHeader.findViewById(R.id.pontos_usuario);
        progressBar = (ProgressBar) navHeader.findViewById(R.id.progressBar);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_TAREFA;
            loadHomeFragment();
        }
    }

    private void setPassword() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                firebaseUser = Conexao.getFirebaseUser();
                databaseReference.child("usuario").child(firebaseUser.getUid()).
                        child("user_senha").setValue(getIntent().getStringExtra("senha"));
            }
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.menu, menu);
        }*/
        // when fragment is notifications, load the menu created for notifications
        /*if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }*/
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void alert(String msg) {
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_SHORT).show();
    }

    private void loadNavHeader() {
        // name, website
        firebaseUser = Conexao.getFirebaseUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        new Usuario().buscar(databaseReference,firebaseUser,txtWebsite,txtName,txtPontos);
        // loading header background image
        Glide.with(MainActivity.this)
                .load(urlNavHeaderBg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(imgNavHeaderBg);
        // Loading profile image
        loadIconUser();
    }

    private void loadIconUser() {
        databaseReference.child("usuario").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Usuario userSelect = dataSnapshot.getValue(Usuario.class);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StorageReference filePath = storageReference.child("img_profiles").
                                child(userSelect.getUser_icon());
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(getBaseContext())
                                                .load(uri)
                                                .transition(DrawableTransitionOptions.withCrossFade())
                                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                                                .apply(RequestOptions.centerCropTransform())
                                                .apply(RequestOptions.fitCenterTransform())
                                                .apply(RequestOptions.bitmapTransform(new CircleTransform()))
                                                .thumbnail(0.5f)
                                                .into(imgProfile);
                                        progressBar.setVisibility(ProgressBar.INVISIBLE);
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

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            //toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        //toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                /*TarefaFragment tarefaFragment = new TarefaFragment();
                return tarefaFragment;*/
                HomeFragmentTab homeFragmentTab = new HomeFragmentTab();
                return homeFragmentTab;
            case 1:
                // photos
                GrupoFragment grupoFragment = new GrupoFragment();
                return grupoFragment;
            case 2:
                // movies fragment
                ContatoFragment contatoFragment = new ContatoFragment();
                return contatoFragment;
            case 3:
                // notifications fragment
                PerfilFragment perfilFragment = new PerfilFragment();
                return perfilFragment;
            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragmentTab();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_TAREFA;
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Deseja realmente sair?")
                                .setCancelable(false)
                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        progressDialog = new ProgressDialog(MainActivity.this);
                                        progressDialog.setMessage("Saindo...");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Conexao.logOut();
                                                finish();
                                            }
                                        }).start();
                                    }
                                }).setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        drawer.closeDrawers();
                                        loadHomeFragment();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, SobreActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AjudaActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_TAREFA;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
