package com.google.firebase.codelab.nthuchat;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener  {

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView mAdView;
    public String mUsername;
    public String mPhotoUrl;
    public String mUid;
    private TextView mNameView;
    private TextView mEmailView;
    private ImageView mIconView;
    public  NavigationView navigationView;
    public DrawerLayout drawer;
    private View headerView;
    public FloatingActionButton fab;
    public Fragment currentFragment;
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mFBdiv;
    public AppDatabase dbinstance;
    public User user;
    public Menu sub1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        mNameView = headerView.findViewById(R.id.nameView);
        mEmailView = headerView.findViewById(R.id.emailView);
        mIconView =  headerView.findViewById(R.id.iconView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbinstance = AppDatabase.getAppDatabase(getApplicationContext());
        user = dbinstance.userDao().getUser();

        drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset != 0){
                    hideKeyboard(MainActivity.this);
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }else{
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                mUsername = mFirebaseUser.getDisplayName();
                mUid = mFirebaseUser.getUid();
                if (mPhotoUrl !=null && mPhotoUrl.contains("..")) {
                    mPhotoUrl = "https://nthuchat.com" + mPhotoUrl.replace("..", "");
                }
                //Toast.makeText(this, "name:  "+mPhotoUrl, Toast.LENGTH_SHORT).show();
                mNameView.setText(mUsername);
                mEmailView.setText(mFirebaseUser.getEmail());
                Picasso.with(MainActivity.this).load(mPhotoUrl).transform(new CropCircleTransformation()).into(mIconView);
                //mIconView.setImageURI(Uri.parse(mPhotoUrl));
            }else{
                int picnum =(int) Math.round((Math.random()*12)+1);
                String namelist[] = {"葉葉","畫眉","JIMMY","阿醜","茶茶","麥芽","皮蛋","小豬","布丁","黑嚕嚕","憨吉","LALLY","花捲"};
                int namenum =(int) Math.round((Math.random()*namelist.length));
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName(namelist[namenum])
                        .setPhotoUri(Uri.parse("../images/user"+picnum+".jpg")).build();
                mFirebaseUser.updateProfile(profileUpdate)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mUsername = mFirebaseUser.getDisplayName();
                                    mUid = mFirebaseUser.getUid();
                                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                                    mNameView.setText(mUsername);
                                    mEmailView.setText(mFirebaseUser.getEmail());
                                    if (mPhotoUrl !=null && mPhotoUrl.contains("..")) {
                                        mPhotoUrl = "https://nthuchat.com" + mPhotoUrl.replace("..", "");
                                    }
                                    Picasso.with(MainActivity.this).load(mPhotoUrl).transform(new CropCircleTransformation()).into(mIconView);
                                }
                            }
                        });
            }

        }

        if(user != null) {
            navigationView.getMenu().findItem(R.id.div).setTitle(user.getDiv());
            String coursename = user.getClasses();
            String[] course_title = coursename.split("#");
            sub1 = navigationView.getMenu().addSubMenu(R.id.course_menu,49,49,R.string.courses);
            for(int id =0; id <= course_title.length-1; id++) {
                //Toast.makeText(MainActivity.this, course_title[id], Toast.LENGTH_SHORT).show();
                sub1.add(0, 50+id,50+id, course_title[id]).setIcon(R.drawable.ic_assignment_black_18dp);
            }

        }else{
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.school);
    }


    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //getSupportFragmentManager().beginTransaction().add(R.id.content_frame, currentFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Uri uri = Uri.parse("https://www.facebook.com/nthuchat/");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startAnimatedActivity(it);
                return true;
            default:
                hideKeyboard(this);
                return super.onOptionsItemSelected(item);
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id){
            case R.id.school:
                displaySelectedScreen(R.id.school);
                break;
            case R.id.div:
                displaySelectedScreen(R.id.div);
                break;
            case R.id.change_name:
                String title = getString(R.string.change_name);
                String intro = getString(R.string.change_name_intro);
                String confirm = getString(R.string.confirm);
                String cancel = getString(R.string.cancel);
                String lastname = mFirebaseUser.getDisplayName();

                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
                final EditText editText = new EditText(MainActivity.this);
                FrameLayout container = new FrameLayout(MainActivity.this);
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                editText.setLayoutParams(params);
                editText.setHint(lastname);
                editText.setMaxLines(1);
                editText.setSingleLine();
                editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
                container.addView(editText);

                alertdialog.setTitle(title)//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage(intro)//設定顯示的文字
                        .setView(container)
                        .setNegativeButton(cancel,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Canceled Change Name", Toast.LENGTH_SHORT).show();
                            }
                        })//設定結束的子視窗
                        .setPositiveButton(confirm,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String changename = editText.getText().toString();
                                if(changename.contains(" ")){
                                    changename.replaceAll(" ","");
                                }
                                if(changename.trim().length() > 0) {
                                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(changename).build();
                                    mFirebaseUser.updateProfile(profileUpdate)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mUsername = mFirebaseUser.getDisplayName();
                                                        mUid = mFirebaseUser.getUid();
                                                        mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                                                        Toast.makeText(MainActivity.this, "Now your name: " + mUsername, Toast.LENGTH_SHORT).show();
                                                        mNameView.setText(mUsername);
                                                    }
                                                }
                                            });
                                }
                            }
                        })//設定結束的子視窗
                        .show();
                break;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                dbinstance.userDao().delete(dbinstance.userDao().getUser());
                AppDatabase.destroyInstance();
                startAnimatedActivity(new Intent(this, SignInActivity.class));
                break;

            default:
                displaySelectedScreen(id);
                break;
        }
        hideKeyboard(this);
        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void startAnimatedActivity(Intent intent) {
        startActivity(intent);
        hideKeyboard(this);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void displaySelectedScreen(int itemId) {
        //creating fragment object
        Fragment fragment = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.school:
                fragment = new Schoolchat();
                navigationView.setCheckedItem(itemId);
                break;
            case R.id.div:
                fragment = new Department();
                navigationView.setCheckedItem(itemId);
                break;
            default:
                fragment = new Course(sub1.findItem(itemId).toString());
                navigationView.setCheckedItem(itemId);
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            hideKeyboard(this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
