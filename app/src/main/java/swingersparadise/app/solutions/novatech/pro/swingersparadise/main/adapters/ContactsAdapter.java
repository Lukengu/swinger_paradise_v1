package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.AppConfig;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.MatchView;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.SingleChat;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.TimerUtils;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.FriendViewHolder> {

    Context c;
    ArrayList<Card> cards;


    public ContactsAdapter(Context c, ArrayList<Card> cards ) {
        this.c = c;
        this.cards = cards;


    }

    public void addCard(Card card){
        cards.add(card);
        notifyDataSetChanged();

    }
    public void clear(){
        cards.clear();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_recycle,parent,false);
        FriendViewHolder holder=new FriendViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final FriendViewHolder holder, int position) {

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + cards.get(position).getUuid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Glide.with(c)
                        .load(uri.toString())
                        .into(holder.profile_image);


            }

        });





        final DatabaseReference myConnectionsRef = FirebaseDatabase.getInstance().getReference("users/"+cards.get(position).getUuid()+"/online_presence");
        myConnectionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.online_status.setEnabled(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.display_name.setText(cards.get(position).getDisplay_name());

        final DatabaseReference  lastOnline = FirebaseDatabase.getInstance().getReference("users/"+cards.get(position).getUuid()+"/lastOnline");
        lastOnline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   holder.date.setText("Last Seen: " + TimerUtils.getTimeAgo(dataSnapshot.getValue(Long.class), c));

               } else {
                   holder.date.setText("lastr seen never");
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView profile_image;
        public TextView display_name,online_status,age,date;

        public FriendViewHolder(View itemView) {
            super(itemView);
            display_name = itemView.findViewById(R.id.textViewSingleListName);
            date =  itemView.findViewById(R.id.textViewSingleListStatus);
            profile_image = itemView.findViewById(R.id.circleImageViewUserImage);
            online_status  =  itemView.findViewById(R.id.userSingleOnlineIcon);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {

            Intent intent = new Intent(c, SingleChat.class);
            intent.putExtra("user_id", cards.get(getLayoutPosition()).getUuid());
            intent.putExtra("user_name", cards.get(getLayoutPosition()).getDisplay_name());
            c.startActivity(intent);
        }
    }
}
