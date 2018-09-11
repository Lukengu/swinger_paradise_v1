
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


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    Context c;
    ArrayList<Card> cards;
    SwipeRefreshLayout swiper;
    public static final  int REQUEST  = 0;
    public static final int LIST = 1;

    int view ;

    public FriendsAdapter(Context c, ArrayList<Card> cards, SwipeRefreshLayout swiper, int view ) {
        this.c = c;
        this.cards = cards;
        this.swiper = swiper;
        this.view = view;
    }

    public void addCard(Card card){
        cards.add(card);
        notifyDataSetChanged();

    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_card,parent,false);
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



        holder.online_status.setVisibility( (view == REQUEST )? View.GONE : View.VISIBLE);

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
        if(!TextUtils.isEmpty(String.valueOf(cards.get(position).getAge()))) {
            holder.age.setText(","+cards.get(position).getAge());
        }

        if(view == REQUEST){
            //  refreshMatches();
            holder.btn_one.setText("ACCEPT");



            Drawable drawableRed = ContextCompat.getDrawable(c, R.drawable.ic_check_circle_green_24dp
            );

            // Set the bound of the drawable object
            drawableRed.setBounds(
                    0, // left
                    0, // top
                    drawableRed.getIntrinsicWidth(), // right
                    drawableRed.getIntrinsicHeight() // bottom
            );

            holder.btn_one.setCompoundDrawables(drawableRed, null, null, null);

            drawableRed = ContextCompat.getDrawable(
                    c,
                    R.drawable.ic_cancel_red_24dp
            );

            // Set the bound of the drawable object
            drawableRed.setBounds(
                    0, // left
                    0, // top
                    drawableRed.getIntrinsicWidth(), // right
                    drawableRed.getIntrinsicHeight() // bottom
            );
            holder.btn_two.setText("REMOVE");
            holder.btn_two.setCompoundDrawables(drawableRed, null, null, null);

        }
        if(view == LIST){
            // refreshFavorites();
            holder.btn_one.setText("CHAT");
            Drawable drawableRed = ContextCompat.getDrawable(c, R.drawable.ic_messenger
            );
            drawableRed.setBounds(
                    0, // left
                    0, // top
                    drawableRed.getIntrinsicWidth(), // right
                    drawableRed.getIntrinsicHeight() // bottom
            );
            holder.btn_one.setCompoundDrawables(drawableRed, null, null, null);

            drawableRed = ContextCompat.getDrawable(c, R.drawable.ic_people_black_24dp
            );
            drawableRed.setBounds(
                    0, // left
                    0, // top
                    drawableRed.getIntrinsicWidth(), // right
                    drawableRed.getIntrinsicHeight() // bottom
            );
            holder.btn_two.setText("PROFILE");
            holder.btn_two.setCompoundDrawables(drawableRed, null, null, null);
        }


    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    private void refresh()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //cards.add(0,cards.get(new Random().nextInt(cards.size())));

                if(view == REQUEST){
                  refreshRequest();
                }
                if(view == LIST){
                   refreshList();
                }

            }


        },3000);
    }
    private void refreshRequest (){
        cards.clear();
        final FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {



                if(dataSnapshot.exists() && dataSnapshot.getKey().equals(user.getUid()) && dataSnapshot.hasChild("friends")){


                    DatabaseReference requests = ref.child(user.getUid()).child("friends");

                    requests.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull  DataSnapshot requestSnapshot, @Nullable String s) {
                            if("false".equals(requestSnapshot.getValue().toString())){
                                final String key = requestSnapshot.getKey();

                                ref.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        if(dataSnapshot.getKey().equals(key)){
                                            Map<String, String> data = new HashMap<>();

                                            // Toast.makeText(getActivity(), dataSnapshot.child("display_name").getValue().toString(), Toast.LENGTH_LONG).show();


                                            data.put("uuid",key);
                                            data.put("display_name",   dataSnapshot.hasChild("display_name") ?   dataSnapshot.child("display_name").getValue().toString() : "");
                                            data.put("drinking",   dataSnapshot.hasChild("drinking") ?   dataSnapshot.child("drinking").getValue().toString() : "");
                                            data.put("age",   dataSnapshot.hasChild("age") ?   dataSnapshot.child("age").getValue().toString() : "");
                                            data.put("body_part",   dataSnapshot.hasChild("body_part") ?   dataSnapshot.child("body_part").getValue().toString() : "");
                                            data.put("build",   dataSnapshot.hasChild("build") ?   dataSnapshot.child("build").getValue().toString() : "");
                                            data.put("country",   dataSnapshot.hasChild("country") ?   dataSnapshot.child("country").getValue().toString() : "");
                                            data.put("about_me",   dataSnapshot.hasChild("about_me") ?   dataSnapshot.child("about_me").getValue().toString() : "");
                                            data.put("gender",   dataSnapshot.hasChild("gender") ?   dataSnapshot.child("gender").getValue().toString() : "");
                                            data.put("hair_color",   dataSnapshot.hasChild("hair_color") ?   dataSnapshot.child("hair_color").getValue().toString() : "");
                                            data.put("marital_status",   dataSnapshot.hasChild("marital_status") ?   dataSnapshot.child("marital_status").getValue().toString() : "");
                                            data.put("name",   dataSnapshot.hasChild("name") ?   dataSnapshot.child("name").getValue().toString() : "");
                                            data.put("referred_by",   dataSnapshot.hasChild("referred_by") ?   dataSnapshot.child("referred_by").getValue().toString() : "");
                                            data.put("sexual_prefs",   dataSnapshot.hasChild("sexual_prefs") ?   dataSnapshot.child("sexual_prefs").getValue().toString() : "");
                                            data.put("ethnicity",   dataSnapshot.hasChild("ethnicity") ?   dataSnapshot.child("ethnicity").getValue().toString() : "");
                                            data.put("smoking",   dataSnapshot.hasChild("smoking") ?   dataSnapshot.child("smoking").getValue().toString() : "");


                                            JSONObject jsonObject = new JSONObject(data);
                                            Card card = null;
                                            try {

                                                card = new Card(jsonObject);
                                                FriendsAdapter.this.addCard(card);
                                                FriendsAdapter.this.notifyDataSetChanged();
                                                swiper.setRefreshing(false);
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });






                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void refreshList(){
        final FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        final java.util.List<String> friends = new ArrayList<>();

        final java.util.List<String> internal = new ArrayList<>();


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && dataSnapshot.getKey().equals(user.getUid()) && dataSnapshot.hasChild("friends")){
                    DatabaseReference requests = ref.child(user.getUid()).child("friends");
                    requests.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull  DataSnapshot requestSnapshot, @Nullable String s) {
                            if("true".equals(requestSnapshot.getValue().toString())){
                                final String key = requestSnapshot.getKey();
                                ref.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        if(dataSnapshot.getKey().equals(key)){
                                            Map<String, String> data = new HashMap<>();

                                            // Toast.makeText(getActivity(), dataSnapshot.child("display_name").getValue().toString(), Toast.LENGTH_LONG).show();


                                            data.put("uuid",key);
                                            data.put("display_name",   dataSnapshot.hasChild("display_name") ?   dataSnapshot.child("display_name").getValue().toString() : "");
                                            data.put("drinking",   dataSnapshot.hasChild("drinking") ?   dataSnapshot.child("drinking").getValue().toString() : "");
                                            data.put("age",   dataSnapshot.hasChild("age") ?   dataSnapshot.child("age").getValue().toString() : "");
                                            data.put("body_part",   dataSnapshot.hasChild("body_part") ?   dataSnapshot.child("body_part").getValue().toString() : "");
                                            data.put("build",   dataSnapshot.hasChild("build") ?   dataSnapshot.child("build").getValue().toString() : "");
                                            data.put("country",   dataSnapshot.hasChild("country") ?   dataSnapshot.child("country").getValue().toString() : "");
                                            data.put("about_me",   dataSnapshot.hasChild("about_me") ?   dataSnapshot.child("about_me").getValue().toString() : "");
                                            data.put("gender",   dataSnapshot.hasChild("gender") ?   dataSnapshot.child("gender").getValue().toString() : "");
                                            data.put("hair_color",   dataSnapshot.hasChild("hair_color") ?   dataSnapshot.child("hair_color").getValue().toString() : "");
                                            data.put("marital_status",   dataSnapshot.hasChild("marital_status") ?   dataSnapshot.child("marital_status").getValue().toString() : "");
                                            data.put("name",   dataSnapshot.hasChild("name") ?   dataSnapshot.child("name").getValue().toString() : "");
                                            data.put("referred_by",   dataSnapshot.hasChild("referred_by") ?   dataSnapshot.child("referred_by").getValue().toString() : "");
                                            data.put("sexual_prefs",   dataSnapshot.hasChild("sexual_prefs") ?   dataSnapshot.child("sexual_prefs").getValue().toString() : "");
                                            data.put("ethnicity",   dataSnapshot.hasChild("ethnicity") ?   dataSnapshot.child("ethnicity").getValue().toString() : "");
                                            data.put("smoking",   dataSnapshot.hasChild("smoking") ?   dataSnapshot.child("smoking").getValue().toString() : "");


                                            JSONObject jsonObject = new JSONObject(data);
                                            Card card = null;
                                            try {
                                                card = new Card(jsonObject);
                                                if(!internal.contains(key)) {
                                                    FriendsAdapter.this.addCard(card);
                                                    FriendsAdapter.this.notifyDataSetChanged();
                                                    swiper.setRefreshing(false);
                                                }
                                                //  getActivity().setTitle(card.getDisplay_name());
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()  && dataSnapshot.hasChild("friends")){
                    DatabaseReference requests = ref.child(dataSnapshot.getKey()).child("friends");
                    requests.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull  DataSnapshot requestSnapshot, @Nullable String s) {
                            if("true".equals(requestSnapshot.getValue().toString())){
                                if(requestSnapshot.getKey().equals(user.getUid())) {
                                    Map<String, String> data = new HashMap<>();

                                    // Toast.makeText(getActivity(), dataSnapshot.child("display_name").getValue().toString(), Toast.LENGTH_LONG).show();


                                    data.put("uuid",dataSnapshot.getKey());
                                    data.put("display_name",   dataSnapshot.hasChild("display_name") ?   dataSnapshot.child("display_name").getValue().toString() : "");
                                    data.put("drinking",   dataSnapshot.hasChild("drinking") ?   dataSnapshot.child("drinking").getValue().toString() : "");
                                    data.put("age",   dataSnapshot.hasChild("age") ?   dataSnapshot.child("age").getValue().toString() : "");
                                    data.put("body_part",   dataSnapshot.hasChild("body_part") ?   dataSnapshot.child("body_part").getValue().toString() : "");
                                    data.put("build",   dataSnapshot.hasChild("build") ?   dataSnapshot.child("build").getValue().toString() : "");
                                    data.put("country",   dataSnapshot.hasChild("country") ?   dataSnapshot.child("country").getValue().toString() : "");
                                    data.put("about_me",   dataSnapshot.hasChild("about_me") ?   dataSnapshot.child("about_me").getValue().toString() : "");
                                    data.put("gender",   dataSnapshot.hasChild("gender") ?   dataSnapshot.child("gender").getValue().toString() : "");
                                    data.put("hair_color",   dataSnapshot.hasChild("hair_color") ?   dataSnapshot.child("hair_color").getValue().toString() : "");
                                    data.put("marital_status",   dataSnapshot.hasChild("marital_status") ?   dataSnapshot.child("marital_status").getValue().toString() : "");
                                    data.put("name",   dataSnapshot.hasChild("name") ?   dataSnapshot.child("name").getValue().toString() : "");
                                    data.put("referred_by",   dataSnapshot.hasChild("referred_by") ?   dataSnapshot.child("referred_by").getValue().toString() : "");
                                    data.put("sexual_prefs",   dataSnapshot.hasChild("sexual_prefs") ?   dataSnapshot.child("sexual_prefs").getValue().toString() : "");
                                    data.put("ethnicity",   dataSnapshot.hasChild("ethnicity") ?   dataSnapshot.child("ethnicity").getValue().toString() : "");
                                    data.put("smoking",   dataSnapshot.hasChild("smoking") ?   dataSnapshot.child("smoking").getValue().toString() : "");


                                    JSONObject jsonObject = new JSONObject(data);
                                    Card card = null;
                                    try {
                                        card = new Card(jsonObject);
                                        if(!internal.contains(dataSnapshot.getKey())) {
                                            FriendsAdapter.this.addCard(card);
                                            FriendsAdapter.this.notifyDataSetChanged();
                                            swiper.setRefreshing(false);
                                        }
                                        //  getActivity().setTitle(card.getDisplay_name());
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView profile_image;
        public TextView display_name,online_status,age;
        public Button  btn_one, btn_two;




        public FriendViewHolder(View itemView) {
            super(itemView);
            profile_image =  itemView.findViewById(R.id.profile_image);
            display_name  =  itemView.findViewById(R.id.name);
            age  =  itemView.findViewById(R.id.age);
            online_status =  itemView.findViewById(R.id.online_status);
            btn_one =  itemView.findViewById(R.id.btn_one);
            btn_two =  itemView.findViewById(R.id.btn_two);

            btn_one.setOnClickListener(this);
            btn_two.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case  R.id.btn_one:
                    if(view == REQUEST) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                        Card card = cards.get(getLayoutPosition());
                        ref.child(user.getUid()).child("friends").child(card.getUuid()).setValue("true");
                        cards.remove(card);
                        FriendsAdapter.this.notifyDataSetChanged();
                        ;
                        final ProgressDialog progressDialog = new ProgressDialog(c);
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
                        JSONObject myprofile;
                        try {
                            myprofile = new JSONObject(sharedPreferences.getString("mmyprofile", ""));
                            String notification_message = "{\"interests\":[\"" + card.getUuid() + "\"],\"fcm\":{\"notification\":{\"title\":\"Friend Request\",\"body\":\"You are now friend with " + myprofile.optString("display_name") + "\"}}}";
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.addHeader("content-type", "application/json");
                            client.addHeader("authorization", "Bearer " + AppConfig.auth_key);
                            try {


                                StringEntity entity = new StringEntity(notification_message);
                                client.post(c, AppConfig.pusher_notification_enpoint, null, entity, "application/json", new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Log.i("Request", AppConfig.pusher_notification_enpoint);
                                        Log.i("Response", response.toString());


                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                                        } else {

                                        }

                                        progressDialog.dismiss();

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                        Log.e("Response", t.getMessage());
                                        // mCallBack.onFailure(new ClientException(t));

                                        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
                                        alertDialog.setTitle("");
                                        alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                                        alertDialog.setMessage(t.getMessage());
                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });


                                        progressDialog.dismiss();
                                        alertDialog.show();



                                    }
                                });

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }


                    if(view == LIST) {
                        Intent intent = new Intent(c, SingleChat.class);
                        intent.putExtra("user_id", cards.get(getLayoutPosition()).getUuid());
                        intent.putExtra("user_name", cards.get(getLayoutPosition()).getDisplay_name());
                        c.startActivity(intent);
                    }
                    break;
                case R.id.btn_two:
                    if(view == REQUEST) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                        Card card = cards.get(getLayoutPosition());
                        ref.child(user.getUid()).child("friends").child(card.getUuid()).removeValue();
                        cards.remove(card);
                        FriendsAdapter.this.notifyDataSetChanged();

                    } else{
                        Bundle b = new Bundle();
                        b.putSerializable("card",  cards.get(getAdapterPosition()));
                        c.startActivity(new Intent(c, MatchView.class).putExtras(b));
                    }
                    break;
            }

        }
    }
}

