package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    Context c;
    ArrayList<String> photos;
    SwipeRefreshLayout swiper;
    public static final  int PUBLIC  = 0;
    public static final int  PRIVATE = 1;

    int view ;

    public PhotosAdapter(Context c, ArrayList<String> photos, SwipeRefreshLayout swiper, int view ) {
        this.c = c;
        this.photos = photos;
        this.swiper = swiper;
        this.view = view;
    }
    public void addPhotos(String photo){
        photos.add(photo);
        notifyDataSetChanged();
    }
    public void clear(){
        photos.clear();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_view,viewGroup,false);
        PhotosAdapter.PhotoViewHolder holder=new PhotosAdapter.PhotoViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder photoViewHolder, int position) {

        final DatabaseReference  reference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String path =  view == PUBLIC  ? "public":"private";
       StorageReference ref = FirebaseStorage.getInstance().getReference().child("albums/"+user.getUid()+"/"+path+"/"+photos.get(position));


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Glide.with(c)
                        .load(uri.toString())
                        .into(photoViewHolder.photo);
            }

        });

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    private void refresh()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //cards.add(0,cards.get(new Random().nextInt(cards.size())));

                String path =  view == PUBLIC  ? "public":"private";
                PhotosAdapter.this.clear();
                FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("albums").child(user.getUid()).child(path);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot photo : dataSnapshot.getChildren()){
                            PhotosAdapter.this.addPhotos(photo.getValue(String.class));
                            // Toast.makeText(getActivity(), photo.getValue(String.class), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                swiper.setRefreshing(false);

            }


        },3000);
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);

        }


    }
}
