package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MatchesAdapter extends FragmentStatePagerAdapter {

    Context mContext;
    FragmentManager mFragmentManager;
    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();


    public MatchesAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mFragmentManager = fm;
    }

    public void addFragment(Fragment fragment, String title){
        fragments.add(fragment);
        titles.add(title);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public String getPageTitle(int position) {
        return titles.get(position);
    }
}
