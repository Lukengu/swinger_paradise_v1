package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.MatchesAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
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
    private java.util.List<String> friend_requests =  new ArrayList();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches,
                container, false);

        viewPager =  view.findViewById(R.id.viewPager);
        tabLayout =  view.findViewById(R.id.tabLayout);




        MatchesAdapter matchesAdapter =  new MatchesAdapter(getFragmentManager(), getActivity());

        matchesAdapter.addFragment(new List(), "List");
        matchesAdapter.addFragment(new Requests(), "Requests");

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
    public  void setCountRequestsBadge(int ct){
       // if(ct > 0) {
          /*  TabLayout.Tab tab = tabLayout.getTabAt(0);
            TextView tv = new TextView(getActivity());
            tv.setText("REQUEST");
            tv.setTextColor(getActivity().getResources().getColor(R.color.colorChocolateSombre));
            tab.setCustomView(tv);
            BadgeView badge = new BadgeView(getActivity(), tv);
            badge.setText(""+ct);
            badge.setBadgeBackgroundColor(getActivity().getResources().getColor(R.color.colorChocolate));
            badge.show();*/
        //}
    }

}
