package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.MatchesAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.friends.List;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.friends.Requests;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.matches.Favorites;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.matches.ProfileMatch;

public class Friends extends Fragment implements  ViewPager.OnPageChangeListener  {
    ViewPager viewPager;
    TabLayout tabLayout;
    public static final String PREFERENCE= "friends";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches,
                container, false);

        viewPager =  view.findViewById(R.id.viewPager);
        tabLayout =  view.findViewById(R.id.tabLayout);




        MatchesAdapter matchesAdapter =  new MatchesAdapter(getFragmentManager(), getActivity());

        matchesAdapter.addFragment(new Requests(), "Requests");
        matchesAdapter.addFragment(new List(), "List");
        viewPager.setAdapter(matchesAdapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();

        // tabLayout.addOnTabSelectedListener(this);

        int position = spref.contains("position") ?  spref.getInt("position",1) : 0;
        viewPager.setCurrentItem(position);




        return view;
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
