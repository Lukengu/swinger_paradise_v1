package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.MessageAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Messages;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.TimerUtils;

public class SingleChat extends AppCompatActivity {
    private String mChatUser;
    TextView mUserName;
    TextView mUserLastSeen;
    CircleImageView mUserImage;
    private FirebaseAuth mAuth;

    String mCurrentUserId;

    DatabaseReference mDatabaseReference;
    private DatabaseReference mRootReference, mChatRootRef,mMessagesRef;

    private ImageButton mChatSendButton,mChatAddButton;
    private EditText mMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mMessageAdapter;

    public static final int TOTAL_ITEM_TO_LOAD = 10;
    private int mCurrentPage = 1;

    //Solution for descending list on refresh
    private int itemPos = 0;
    private String mLastKey="";
    private String mPrevKey="";
    private Boolean typingStarted;

    private DatabaseReference user_message_push_typing;

    private static final int GALLERY_PICK=1;
    StorageReference mImageStorage;
    long  active_chats = 0;
    private SharedPreferences spref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        spref = PreferenceManager.getDefaultSharedPreferences(this);

        mChatAddButton = (ImageButton)findViewById(R.id.chatAddButton);
        mChatSendButton = (ImageButton)findViewById(R.id.chatSendButton);
        mMessageView = (EditText)findViewById(R.id.chatMessageView);

        //-----GETING FROM INTENT----
        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");

        //---SETTING ONLINE------
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mChatRootRef = FirebaseDatabase.getInstance().getReference().child("chats");
        mMessagesRef= FirebaseDatabase.getInstance().getReference().child("messages");

        //----ADDING ACTION BAR-----
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //---INFLATING APP BAR LAYOUT INTO ACTION BAR----
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.app_bar_layout,null);
        actionBar.setCustomView(actionBarView);

        //---ADDING DATA ON ACTION BAR----
        mUserName=(TextView) actionBarView.findViewById(R.id.textView3);
        mUserLastSeen = (TextView) actionBarView.findViewById(R.id.textView5);
        mUserImage = (CircleImageView) actionBarView.findViewById(R.id.circleImageView);
        mUserName.setText(userName);

        mRootReference = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mMessageAdapter = new MessageAdapter(messagesList);

        mMessagesList = (RecyclerView)findViewById(R.id.recycleViewMessageList);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        mLinearLayoutManager = new LinearLayoutManager(SingleChat.this);

        // mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayoutManager);
        mMessagesList.setAdapter(mMessageAdapter);

        loadMessages();

        //----ADDING LAST SEEN-----
        mRootReference.child("users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String onlineValue=dataSnapshot.child("online").getValue().toString();
                // String imageValue = dataSnapshot.child("thumb_image").getValue().toString();

                StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + mChatUser);
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        Picasso.with(SingleChat.this).load(uri.toString()).placeholder(R.drawable.ic_account_box_black_24dp).into(mUserImage);


                    }

                });

                final DatabaseReference myConnectionsRef = FirebaseDatabase.getInstance().getReference("users/"+mChatUser+"/online_presence");
                myConnectionsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            mUserLastSeen.setText("online");
                        } else {
                            final DatabaseReference  lastOnline = FirebaseDatabase.getInstance().getReference("users/"+mChatUser+"/lastOnline");
                            lastOnline.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        TimerUtils getTimeAgo = new TimerUtils();
                                        long lastTime = dataSnapshot.getValue(Long.class);

                                        String lastSeen = getTimeAgo.getTimeAgo(lastTime,getApplicationContext());
                                        mUserLastSeen.setText(lastSeen);

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
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //----ADDING SEEN OF MESSAGES----
        mRootReference.child("chats").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(mChatUser)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("time_stamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chats/"+mChatUser+"/"+mCurrentUserId,chatAddMap);
                    chatUserMap.put("chats/"+mCurrentUserId+"/"+mChatUser,chatAddMap);

                    mRootReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                              //  Toast.makeText(getApplicationContext(), "Successfully Added chats feature", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                //   Toast.makeText(getApplicationContext(), "Cannot Add chats feature", Toast.LENGTH_SHORT).show();
                            }
                        }


                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something went wrong.. Please go back..", Toast.LENGTH_SHORT).show();
            }
        });




        //----SEND MESSAGE--BUTTON----

        mChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final  String message = mMessageView.getText().toString();
                if(!TextUtils.isEmpty(message)){

                    String current_user_ref = "messages/"+mCurrentUserId+"/"+mChatUser;
                    String chat_user_ref = "messages/"+ mChatUser +"/"+mCurrentUserId;

                    DatabaseReference user_message_push = mRootReference.child("messages")
                            .child(mCurrentUserId).child(mChatUser).push();

                    String push_id = user_message_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("message",message);
                    messageMap.put("seen",false);
                    messageMap.put("type","text");
                    messageMap.put("time",ServerValue.TIMESTAMP);
                    messageMap.put("from",mCurrentUserId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                    messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

                    mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener(){

                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.e("CHAT_ACTIVITY","Cannot add message to database");
                            }
                            else{
                                final MediaPlayer mp = MediaPlayer.create(SingleChat.this, R.raw.sms_alert);
                                mp.start();
                                Toast.makeText(SingleChat.this, "Message sent", Toast.LENGTH_SHORT).show();
                                String display_name = spref.getString("display_name", "");

                                String notification_message = "{\"interests\":[\""+mChatUser+"\"],\"fcm\":{\"notification\":{\"title\":\""+display_name+"\",\"body\":\""+message+"\"}}}";
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.addHeader("content-type", "application/json");
                                client.addHeader("authorization", "Bearer "+ AppConfig.auth_key);
                                try {
                                    StringEntity entity = new StringEntity(notification_message);
                                    client.post(SingleChat.this,AppConfig.pusher_notification_enpoint,null,entity,"application/json",new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            Log.i("Request", AppConfig.pusher_notification_enpoint);
                                            Log.i("Response", response.toString());
                                            Toast.makeText(SingleChat.this, response.toString(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(SingleChat.this,mChatUser, Toast.LENGTH_SHORT).show();
                                            Toast.makeText(SingleChat.this,message, Toast.LENGTH_SHORT).show();

                                        }
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                            Log.e("Response", t.getMessage());
                                            // mCallBack.onFailure(new ClientException(t));

                                        }
                                    });
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                mMessageView.setText("");
                                //Send Notification
                            }

                        }
                    });




                }

            }
        });
       /*
        //----THE WRAP CONTENT OF IMAGE VIEW IS GIVING ERROR--- SO REMOVING THIS FUNCTIONALITY-------
       mChatAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);
            }
        });
        */
        //----LOADING 10 MESSAGES ON SWIPE REFRESH----
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemPos = 0;
                mCurrentPage++;
                loadMoreMessages();;

            }
        });

    }


    private void sendNotification(){

        final List<Messages> messagesList = new ArrayList<>();


        mChatRootRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                active_chats = dataSnapshot.getChildrenCount();
                if(active_chats == 1) {
                    mMessagesRef.child(mCurrentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            Messages messages =  dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //---FIRST 10 MESSAGES WILL LOAD ON START----
    private void loadMessages() {

        DatabaseReference messageRef = mRootReference.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage*TOTAL_ITEM_TO_LOAD);



        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = (Messages) dataSnapshot.getValue(Messages.class);

                itemPos++;

                if(itemPos == 1){
                    String mMessageKey = dataSnapshot.getKey();

                    mLastKey = mMessageKey;
                    mPrevKey = mMessageKey;
                }


                if(!messages.getFrom().equals(mCurrentUserId) && getIntent().getExtras().containsKey("user_id")){
                    messages.setSeen(true);
                    mRootReference.child("messages").child(mChatUser).child(mCurrentUserId).child(dataSnapshot.getKey()).child("seen").setValue(true);
                    mRootReference.child("messages").child(mCurrentUserId).child(mChatUser).child(dataSnapshot.getKey()).child("seen").setValue(true);

                }

                messagesList.add(messages);
                mMessageAdapter.notifyDataSetChanged();



                mMessagesList.scrollToPosition(messagesList.size()-1);

                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //---ON REFRESHING 10 MORE MESSAGES WILL LOAD----
    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootReference.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = (Messages) dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();


                if(!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++,message);

                }
                else{
                    mPrevKey = mLastKey;
                }

                if(itemPos == 1){
                    String mMessageKey = dataSnapshot.getKey();
                    mLastKey = mMessageKey;
                }


                mMessageAdapter.notifyDataSetChanged();

                mSwipeRefreshLayout.setRefreshing(false);

                mLinearLayoutManager.scrollToPositionWithOffset(10,0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //---THIS FUNCTION IS CALLED WHEN SYSTEM ACTIVITY IS CALLED---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //---FOR PICKING IMAGE FROM GALLERY ACTIVITY AND SENDING---
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            //---GETTING IMAGE DATA IN FORM OF URI--
            Uri imageUri = data.getData();
            final String current_user_ref = "messages/"+mCurrentUserId+"/"+mChatUser;
            final String chat_user_ref = "messages/"+ mChatUser +"/"+mCurrentUserId;

            DatabaseReference user_message_push = mRootReference.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            final String push_id = user_message_push.getKey();

            //---PUSHING IMAGE INTO STORAGE---
            StorageReference filepath = mImageStorage.child("message_images").child(push_id+".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        @SuppressWarnings("VisibleForTests")
                        String download_url =  task.getResult().getUploadSessionUri().toString();//task.getResult().getDownloadUrl().toString();



                        Map messageMap = new HashMap();
                        messageMap.put("message",download_url);
                        messageMap.put("seen",false);
                        messageMap.put("type","image");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from",mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                        messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

                        mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener(){

                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError != null){
                                    Log.e("CHAT_ACTIVITY","Cannot add message to database");
                                }
                                else{
                                    Toast.makeText(SingleChat.this, "Message sent", Toast.LENGTH_SHORT).show();
                                    mMessageView.setText("");
                                }

                            }
                        });
                    }

                }
            });


        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //mDatabaseReference.child(mCurrentUserId).child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // mDatabaseReference.child(mCurrentUserId).child("online").setValue(ServerValue.TIMESTAMP);

    }





}
