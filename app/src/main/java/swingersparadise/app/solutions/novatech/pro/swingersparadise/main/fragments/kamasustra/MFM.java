package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.kamasustra;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.AppConfig;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.FriendsAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.PositionsImageAdapter;

public class MFM   extends Fragment {

    RecyclerView mRecycler;
    PositionsImageAdapter positionsImageAdapter;
    SwipeRefreshLayout swiper;
    ArrayList<String> images = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list,
                container, false);


        mRecycler = view.findViewById(R.id.mRecycler);
        swiper = view.findViewById(R.id.swiper);
        positionsImageAdapter = new PositionsImageAdapter(getActivity(), images, swiper,"MFM");

        mRecycler.setAdapter(positionsImageAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecycler.setLayoutManager(gridLayoutManager);
        getLists();
        return view;
    }

    public void getLists() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(AppConfig.POSITION_ENDPOINT, "mfm"), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("Request", String.format(AppConfig.POSITION_ENDPOINT, "mf"));
                Log.i("Response", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    positionsImageAdapter.addImage(response.optString(i));
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.e("Response", t.getMessage());


            }
        });
    }
}