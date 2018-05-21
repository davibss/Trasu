package br.com.trasudev.trasu.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.trasudev.trasu.R;


public class HomeFragmentTab extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Context context;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragmentTab() {
        // Required empty public constructor
    }

    public static HomeFragmentTab newInstance(String param1, String param2) {
        HomeFragmentTab fragment = new HomeFragmentTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*return inflater.inflate(R.layout.fragment_home_fragment_tab, container, false);*/
        View rootView = inflater.inflate(R.layout.fragment_home_fragment_tab, container,false);
        viewPager = rootView.findViewById(R.id.viewpager);
        tabLayout = rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TarefaFragment(), "Tarefas");
        adapter.addFragment(new TabTarefaGrupalFragment(), "Tarefas2");
        viewPager.setAdapter(adapter);
        View customView = adapter.getCustomView(context,0);
        TextView texto = customView.findViewById(R.id.textView);
        ImageView image = customView.findViewById(R.id.imageView);
        texto.setText("INDIVIDUAIS");
        image.setImageResource(R.drawable.ic_person_white_24dp);
        tabLayout.getTabAt(0).setCustomView(customView);
        customView = adapter.getCustomView(context,1);
        texto = customView.findViewById(R.id.textView);
        image = customView.findViewById(R.id.imageView);
        texto.setText("GRUPAIS");
        image.setImageResource(R.drawable.ic_group_white_24dp);
        tabLayout.getTabAt(1).setCustomView(customView);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public View getCustomView(Context context, int pos) {
            View mView = LayoutInflater.from(context).inflate(R.layout.custom_tab_view, null);
            TextView mTextView = (TextView) mView.findViewById(R.id.textView);
            ImageView imageView = (ImageView) mView.findViewById(R.id.imageView);
            mTextView.setGravity(Gravity.CENTER);
            /*mTextView.setTypeface(Typeface.createFromAsset(context.getAssets(),
                    "fonts/aller_regular.ttf"));*/
            mTextView.setText(getPageTitle(pos));
            return mView;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
