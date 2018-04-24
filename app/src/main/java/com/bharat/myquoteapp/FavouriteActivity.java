package com.bharat.myquoteapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private ListView quoteList;
    private List<QuoteClass> aListQuote;
    private QuoteAdapter quoteAdapter;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        firebaseFirestore = FirebaseFirestore.getInstance();

        quoteList = findViewById(R.id.fav_quotes_list);
        aListQuote = new ArrayList<>();

        firebaseFirestore.collection("favQuotes").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    for(int i=0;i<Integer.valueOf(getPrefrences("count"));i++){
                        String quote = getQuote(task.getResult().getString(String.valueOf(i)));
                        String author = getAuthor(task.getResult().getString(String.valueOf(i)));
                        aListQuote.add(new QuoteClass(quote,author));
                    }

                    quoteAdapter = new QuoteAdapter(FavouriteActivity.this,R.layout.list_layout,aListQuote);
                    quoteList.setAdapter(quoteAdapter);                }
            }
        });




    }
    public String getPrefrences(String v){
        SharedPreferences sharedPreferences = getSharedPreferences("CountPrefs", Context.MODE_PRIVATE);
        int count = sharedPreferences.getInt(v,0);
        return String.valueOf(count);
    }

    private String getAuthor(String fullQuote) {
        String[] strArray = fullQuote.split("/");
        return strArray[1];
    }

    private String getQuote(String fullQuote) {
        String[] strArray = fullQuote.split("/");
        return strArray[0];
    }
}
