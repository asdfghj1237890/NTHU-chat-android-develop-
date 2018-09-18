package com.google.firebase.codelab.nthuchat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Course extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messengerTextView;
        CircleImageView messengerImageView;
        View view;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            view = itemView;
        }

        public void setOnItemClick(View.OnClickListener l){
            this.view.setOnClickListener(l);
        }
    }

    public Course(){
        super();
    }

    @SuppressLint("ValidFragment")
    public Course(String title){
        super();
        this.MESSAGES_CHILD = title;
    }

    public AppDatabase dbinstance;

    public String mUsername;
    public String mPhotoUrl;
    public String mUid;
    public User user;

    private static final String TAG = "Course";

    private String MESSAGES_CHILD ;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private FloatingActionButton mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, Course.MessageViewHolder>
            mFirebaseAdapter;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView mAdView;
    private TextView countLabel;
    private long countlength;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        //inflate your activity layout here!
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_schoolchat, container, false);
        //MobileAds.initialize(getActivity(), "ca-app-pub-3589269405021012~8631287778");
        countLabel = contentView.findViewById(R.id.countLabel);

        dbinstance = AppDatabase.getAppDatabase(getContext());
        user = dbinstance.userDao().getUser();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null){
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                mUsername = mFirebaseUser.getDisplayName();
                mUid = mFirebaseUser.getUid();
            }
        }else{
            int picnum =(int) Math.round((Math.random()*12)+1);
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName("無名勇士")
                    .setPhotoUri(Uri.parse("../images/user"+picnum+".jpg")).build();
            mFirebaseUser.updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(MainActivity.this, "1Update AC Success", Toast.LENGTH_SHORT).show();
                                mUsername = mFirebaseUser.getDisplayName();
                                mUid = mFirebaseUser.getUid();
                                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                                //Toast.makeText(MainActivity.this, "1.5Displayname: "+mUsername, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(MainActivity.this, "1.5PhotoUrl: "+mPhotoUrl, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) contentView.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) contentView.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @Override
            public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(dataSnapshot.getKey());
                }
                return friendlyMessage;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {
                    //Toast.makeText(getActivity(), R.string.emptymessage, Toast.LENGTH_SHORT).show();
                    FriendlyMessage friendlyMessage = new
                            FriendlyMessage("你可以成為第一個發言的人喔!", "NTHU Chat", "https://nthuchat.com/images/user1.jpg", "999999");
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                            .push().setValue(friendlyMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        FirebaseRecyclerOptions<FriendlyMessage> options =
                new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                        .setQuery(messagesRef, parser)
                        .build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, Course.MessageViewHolder>(options) {
            @Override
            public Course.MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                switch (i) {
                    case 0:
                        return new Course.MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
                    case 1:
                        return new Course.MessageViewHolder(inflater.inflate(R.layout.item_message_me, viewGroup, false));
                    default:
                        return new Course.MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
                }
            }
            @Override
            public int getItemViewType(int position){
                if(getItem(position) != null && getItem(position).getUid() != null) {
                    if (getItem(position).getUid().equals(mUid)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
                return 0;
            }

            @Override
            protected void onBindViewHolder(final Course.MessageViewHolder viewHolder,
                                            int position,
                                            FriendlyMessage friendlyMessage) {
                switch (viewHolder.getItemViewType()){
                    case 0:
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        if (friendlyMessage.getText() != null) {
                            viewHolder.messageTextView.setText(friendlyMessage.getText());
                            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                        }
                        viewHolder.messengerTextView.setText(friendlyMessage.getName());
                        if (friendlyMessage.getPhotoUrl() == null) {
                            viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                    R.drawable.ic_account_circle_black_36dp));
                        } else {
                            Glide.with(Course.this)
                                    .load(friendlyMessage.getPhotoUrl())
                                    .into(viewHolder.messengerImageView);
                        }
                        break;
                    case 1:
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        if (friendlyMessage.getText() != null) {
                            viewHolder.messageTextView.setText(friendlyMessage.getText());
                            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                        }
                        viewHolder.messengerTextView.setText(friendlyMessage.getName());
                        if (friendlyMessage.getPhotoUrl() == null) {
                            viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                    R.drawable.ic_account_circle_black_36dp));
                        } else {
                            Glide.with(Course.this)
                                    .load(friendlyMessage.getPhotoUrl())
                                    .into(viewHolder.messengerImageView);
                        }
                        break;
                }
                viewHolder.setOnItemClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //設定你點擊每個Item後，要做的事情
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.
                                INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) contentView.findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    //Toast.makeText(MainActivity.this, "true", Toast.LENGTH_SHORT).show();
                    int current_length = charSequence.toString().trim().length();
                    countLabel.setText(current_length +"/"+ countlength);
                    mSendButton.setEnabled(true);
                } else {
                    //Toast.makeText(MainActivity.this, "false", Toast.LENGTH_SHORT).show();
                    countLabel.setText("0/"+ countlength);
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (FloatingActionButton) contentView.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsername = mFirebaseUser.getDisplayName();
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(), mUsername, mPhotoUrl, mUid);
                friendlyMessage.setUid(mUid);
                friendlyMessage.setPhotoUrl(mPhotoUrl);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        /*mAdView = (AdView) contentView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
        return contentView;
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that
        // each fetch goes to the server. This should not be used in release
        // builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings()
                .isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via
                        // FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config: " +
                                e.getMessage());
                        applyRetrievedLengthLimit();
                    }
                });
    }

    /**
     * Apply retrieved length limit to edit text field.
     * This result may be fresh from the server or it may be from cached
     * values.
     */
    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length =
                mFirebaseRemoteConfig.getLong("friendly_msg_length");
        mMessageEditText.setFilters(new InputFilter[]{new
                InputFilter.LengthFilter(friendly_msg_length.intValue())});
        countlength = friendly_msg_length;
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(MESSAGES_CHILD);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        mSendButton.setEnabled(false);
    }

    @Override
    public void onPause() {
        /*if (mAdView != null) {
            mAdView.pause();
        }*/
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
        /*if (mAdView != null) {
            mAdView.resume();
        }*/
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        /*if (mAdView != null) {
            mAdView.destroy();
        }*/
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
