package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.MatchesAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.matches.Favorites;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.matches.ProfileMatch;

public class Matches extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches,
                container, false);

        viewPager =  view.findViewById(R.id.viewPager);
        tabLayout =  view.findViewById(R.id.tabLayout);




        MatchesAdapter matchesAdapter =  new MatchesAdapter(getFragmentManager(), getActivity());

        matchesAdapter.addFragment(new ProfileMatch(), "Profile Matches");
        matchesAdapter.addFragment(new Favorites(), "Favorites");
        viewPager.setAdapter(matchesAdapter);
        tabLayout.setupWithViewPager(viewPager);


        return view;
    }
}
