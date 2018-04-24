package com.bharat.myquoteapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class QuoteAdapter extends ArrayAdapter<QuoteClass> {
    private int LayoutResource;

    public QuoteAdapter(@NonNull Context context, int resource, @NonNull List<QuoteClass> objects) {
        super(context, resource, objects);
        LayoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(LayoutResource,parent,false);
        }

        final TextView quote = convertView.findViewById(R.id.quote_list_item);
        TextView writer = convertView.findViewById(R.id.writer_list_item);
        ImageButton copyBtn = convertView.findViewById(R.id.copy_btn);



        final QuoteClass currentQuote = getItem(position);

        quote.setText(currentQuote.getmQuote());
        writer.setText(currentQuote.getmAuthor());




        return convertView;
    }
}
