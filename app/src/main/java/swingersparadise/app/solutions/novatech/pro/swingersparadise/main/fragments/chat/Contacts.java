package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.ContactsAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Friends;

public class Contacts extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private ContactsAdapter contactsAdapter;
    private ArrayList<Card> cards = new ArrayList<>();

    private String mCurrent_user_id;

    private View mMainView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.generic_list, container, false);

        mFriendsList = (RecyclerView)mMainView.findViewById(R.id.genericList);
        mAuth=FirebaseAuth.getInstance();

        //---CURRENT USER ID--
        mCurrent_user_id=mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child(mCurrent_user_id).child("friends");
        mFriendDatabase.keepSynced(true);

        //---USERS DATA
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter(getActivity(), cards);
        mFriendsList.setAdapter(contactsAdapter);


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //---FETCHING DATABASE FROM mFriendDatabase USING Friends.class AND ADDING TO RECYCLERVIEW----
        contactsAdapter.clear();

        final FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        final java.util.List<String> friends = new ArrayList<>();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Friends...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

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
                                                    contactsAdapter.addCard(card);
                                                    internal.add(key);
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
                                            contactsAdapter.addCard(card);
                                            internal.add(dataSnapshot.getKey());
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
        progressDialog.dismiss();



    }






}
