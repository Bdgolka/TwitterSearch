package com.bdgolka.twittersearch;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends ListActivity {

    //the name of XML file with data SharedPreferences
    private static final String SEARCHES = "searches";

    private EditText queryText; // EditText for request entering
    private EditText tagEditText;//EditText for tag entering
    private SharedPreferences savedSearches; // Users requests
    private ArrayList<String> tags;// list of tags for requests
    private ArrayAdapter<String> adapter; // for binding tags and ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //geting referenses on EditText
        queryText = (EditText)findViewById(R.id.queryEditText);
        tagEditText = (EditText)findViewById(R.id.tagEditText);

        //Getting object SharedPreferences for saved requests
        savedSearches = getSharedPreferences(SEARCHES,MODE_PRIVATE);

        //Save tags in ArrayList and sorting
        tags = new ArrayList<String>(savedSearches.getAll().keySet());
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);

        //Creating object ArrayAdapter and binding tags to ArrayView
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, tags);
        setListAdapter(adapter);

        //Listener registration for saving request
        ImageButton saveButton = (ImageButton)findViewById(R.id.saveButton);
        //saveButton.setOnItemClickListener(saveButtonListener);

        //Listener registration for searching in Twitter
        //getListView().setOnClickListener(itemClickListener);

        //Set listener who is able to delete or edit request
        //getListView().setOnItemClickListener(itemLongClickListener);
    }
}
