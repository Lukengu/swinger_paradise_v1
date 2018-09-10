package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.MatchesAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.albums.Private;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.albums.Public;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.chat.Contacts;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.chat.Conversations;

public class Albums extends Fragment  implements  ViewPager.OnPageChangeListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    public static final String PREFERENCE= "albums";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches,
                container, false);

        viewPager =  view.findViewById(R.id.viewPager);
        tabLayout =  view.findViewById(R.id.tabLayout);




        MatchesAdapter matchesAdapter =  new MatchesAdapter(getFragmentManager(), getActivity());

        matchesAdapter.addFragment(new Public(), "PUBLIC");
        matchesAdapter.addFragment(new Private(), "PRIVATE");

        viewPager.setAdapter(matchesAdapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();

        // tabLayout.addOnTabSelectedListener(this);

        int position = spref.contains("position") ?  spref.getInt("position",1) : 0;
        viewPager.setCurrentItem(position);
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        menu.findItem(R.id.action_settings).setVisible(false);



        //  menu.findItem(R.id.action_camera).setVisible(true);
        menu.findItem(R.id.action_gallery).setVisible(true);
        inflater.inflate(R.menu.content, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if(spref.contains("position"))
            editor.remove("position").commit();

        editor.putInt("position", i).commit();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
