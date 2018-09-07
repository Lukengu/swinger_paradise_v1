package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.MatePhotosViewer;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumListViewHolder> {

    private Context mContext;
    private List<String> photos = new ArrayList<>();
    private  Card  card;
   // private RecycleViewAdapterItemListener recycleViewAdapterItemListener;

    public AlbumAdapter(Context mContext,Card card) {
        this.mContext = mContext;
        this.card = card;
        populateAlbum();

        // Toast.makeText(mContext, "Adapter"+ storeList.size(), Toast.LENGTH_LONG).show();
    }

  /*  public void setOnRecycleViewAdapterItemListener(RecycleViewAdapterItemListener recycleViewAdapterItemListener ){
        this.recycleViewAdapterItemListener = recycleViewAdapterItemListener;
    }*/

   private void populateAlbum(){
        final DatabaseReference albums  = FirebaseDatabase.getInstance().getReference().child("albums");
       // Toast.makeText(mContext,card.getUuid() , Toast.LENGTH_LONG).show();

       albums.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists() && dataSnapshot.hasChild(card.getUuid())){
                   albums.child(card.getUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
                               for (DataSnapshot propertySnapshot : entrySnapshot.getChildren()) {
                                 //  Toast.makeText(mContext, propertySnapshot.getValue(String.class), Toast.LENGTH_LONG).show();
                                   photos.add(propertySnapshot.getValue(String.class));
                                   notifyDataSetChanged();
                               }
                           }



                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });




   }

    @Override
    public AlbumListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_view, parent, false);
        final AlbumListViewHolder mViewHolder = new AlbumListViewHolder(mView);




        return mViewHolder;

    }

    public String getItem(int position){
        return photos.get(position);
    }


    @Override
    public void onBindViewHolder(final AlbumListViewHolder holder, int position) {


        StorageReference ref = FirebaseStorage.getInstance().getReference().child("albums/" + card.getUuid()+"/public/"+getItem(position));


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                Glide.with(mContext)
                        .load(uri.toString())
                        .into(holder.mImage);



            }

        });


    }


    @Override
    public int getItemCount() {
        return photos.size();
    }

    class AlbumListViewHolder extends RecyclerView.ViewHolder  implements  View.OnClickListener{

        ImageView mImage;
      //  TextView mTitle;

        public AlbumListViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.photo);
            itemView.setOnClickListener(this);
          //  mTitle = itemView.findViewById(R.id.tvTitle);

        }


        @Override
        public void onClick(View view) {
             ArrayList photos_extra = new ArrayList();
            photos_extra.addAll(photos);

            Bundle b =new Bundle();
            b.putInt("position", getAdapterPosition());
            b.putStringArrayList("photos",photos_extra);
            b.putString("display_name", card.getDisplay_name());
            b.putString("uuid", card.getUuid());
            Intent i = new Intent(mContext, MatePhotosViewer.class).putExtras(b);

            mContext.startActivity(i);

        }
    }



}