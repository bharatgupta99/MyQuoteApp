package com.bharat.myquoteapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView quoteText;
    private TextView autherText;
    private ImageButton shuffleBtn;
    private ImageButton favBtn;
    private FirebaseFirestore mFirebaseFirestore;
    private int rn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteText = findViewById(R.id.quote);
        autherText = findViewById(R.id.writer);
        shuffleBtn = findViewById(R.id.shuffle_btn);
        favBtn = findViewById(R.id.fav_btn);
        mFirebaseFirestore = FirebaseFirestore.getInstance();


        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseFirestore.collection("quotes").document("IFTrI7LGepzel7b8QlXm").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            long count = task.getResult().getLong("count");
                            Random rand = new Random();
                            rn = rand.nextInt((int)count);
                            Log.i("RN",String.valueOf(rn));
                            String fullQuote = task.getResult().getString(String.valueOf(rn));
                            quoteText.setText(getQuote(fullQuote));
                            autherText.setText(getAuthor(fullQuote));

                        }
                    }
                });

                favBtn.setImageResource(R.mipmap.fav_icon);

            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Snackbar.make(findViewById(R.id.constraint_main),"Sign-In is required to add Favorite Quotes..",Snackbar.LENGTH_SHORT).setAction("Sign-In",new SignInListener()).show();
                }else{
                    mFirebaseFirestore.collection("quotes").document("IFTrI7LGepzel7b8QlXm").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Map<String,String> userFavQuotes = new HashMap<>();
                            userFavQuotes.put(getPrefrences("count"),task.getResult().getString(String.valueOf(rn)));
                            mFirebaseFirestore.collection("favQuotes").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(userFavQuotes, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    setPrefrences(Integer.valueOf(getPrefrences("count"))+1);
                                    favBtn.setImageResource(R.mipmap.fav_icon_yellow);
                                }
                            });
                        }
                    });


                }
            }
        });




        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            mFirebaseFirestore.collection("favQuotes").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            long count = task.getResult().getLong("count");
                            setPrefrences((int) count);
                        } else {
                            Map<String, Long> countMap = new HashMap<>();
                            countMap.put("count", Long.valueOf("0"));
                            mFirebaseFirestore.collection("favQuotes").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(countMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                    }
                                }
                            });

                        }
                    }
                    }
            });
        }



    }

    private String getAuthor(String fullQuote) {
        String[] strArray = fullQuote.split("/");
        return strArray[1];
    }

    private String getQuote(String fullQuote) {
        String[] strArray = fullQuote.split("/");
        return strArray[0];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_layout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.login_btn) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }else if(itemId == R.id.contribute_btn){
            Intent contributionIntent = new Intent(MainActivity.this, ContributionActivity.class);
            startActivity(contributionIntent);
            finish();
        }else if(itemId == R.id.sign_out){
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                setPrefrences(0);
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "See You Again..Bye", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                Toast.makeText(MainActivity.this,"Please SignIn first Bitch",Toast.LENGTH_SHORT).show();
            }
        }else if(itemId == R.id.fav_btn){
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                Intent favIntent = new Intent(MainActivity.this, FavouriteActivity.class);
                startActivity(favIntent);
            }else{
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        favBtn.setImageResource(R.mipmap.fav_icon);
        mFirebaseFirestore.collection("quotes").document("IFTrI7LGepzel7b8QlXm").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    long count = task.getResult().getLong("count");
                    Random rand = new Random();
                    rn = rand.nextInt((int)count);
                    Log.i("RN",String.valueOf(rn));
                    String fullQuote = task.getResult().getString(String.valueOf(rn));
                    quoteText.setText(getQuote(fullQuote));
                    autherText.setText(getAuthor(fullQuote));
                }
            }
        });



    }

    public class SignInListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    public void setPrefrences(int count){
        SharedPreferences sharedPreferences =  getSharedPreferences("CountPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("count",count);
        editor.commit();
    }

    public String getPrefrences(String v){
        SharedPreferences sharedPreferences = getSharedPreferences("CountPrefs",Context.MODE_PRIVATE);
        int count = sharedPreferences.getInt(v,0);
        return String.valueOf(count);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Map<String,Long> countMap = new HashMap<>();
        countMap.put("count",Long.valueOf(getPrefrences("count")));
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            mFirebaseFirestore.collection("favQuotes").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(countMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }

    }
}
