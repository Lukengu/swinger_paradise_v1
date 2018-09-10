package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.albums;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.FriendsAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.PhotosAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;

public class Public extends Fragment {

    RecyclerView mRecycler;
    PhotosAdapter photosAdapter;
    SwipeRefreshLayout swiper;
    ArrayList<String> photos = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list,
                container, false);

        mRecycler = view.findViewById(R.id.mRecycler);
        swiper =  view.findViewById(R.id.swiper);
        photosAdapter = new PhotosAdapter(getActivity(),photos,swiper,PhotosAdapter.PUBLIC);
        mRecycler.setAdapter(photosAdapter);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 4);
        mRecycler.setLayoutManager(mGridLayoutManager);
        mRecycler.setHasFixedSize(true);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_camera) {




            return true;
        }

        if (id == R.id.action_gallery) {




            return true;
        }




        return super.onOptionsItemSelected(item);
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
    public void onStart() {
        super.onStart();
        photosAdapter.clear();
        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("albums").child(user.getUid()).child("public");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot photo : dataSnapshot.getChildren()){
                    photosAdapter.addPhotos(photo.getValue(String.class));
                   // Toast.makeText(getActivity(), photo.getValue(String.class), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
