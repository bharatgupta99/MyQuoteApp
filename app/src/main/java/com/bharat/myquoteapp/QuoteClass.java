package com.bharat.myquoteapp;

public class QuoteClass {

    private String mQuote;
    private String mAuthor;

    public QuoteClass(String quote, String author){
        mQuote = quote;
        mAuthor = author;
    }

    public String getmQuote() {
        return mQuote;
    }

    public String getmAuthor() {
        return mAuthor;
    }
}
