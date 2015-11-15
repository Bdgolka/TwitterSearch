package com.bdgolka.twittersearch;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
        queryText = (EditText) findViewById(R.id.queryEditText);
        tagEditText = (EditText) findViewById(R.id.tagEditText);

        //Getting object SharedPreferences for saved requests
        savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);

        //Save tags in ArrayList and sorting
        tags = new ArrayList<>(savedSearches.getAll().keySet());
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);

        //Creating object ArrayAdapter and binding tags to ArrayView
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, tags);
        setListAdapter(adapter);

        //Listener registration for saving request
        ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);

        saveButton.setOnClickListener(saveButtonListener);

        //Listener registration for searching in Twitter
        //getListView().setOnItemClickListener(itemClickListener);

        //Set listener who is able to delete or edit request
        //getListView().setOnItemClickListener(itemLongClickListener);
    }

    //saveButtonListener save pare "tag-request" in SharePraferences
    public OnClickListener saveButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create tag, if there is any data in queryEditText and tagEditText
            if (queryText.getText().length() > 0 && tagEditText.getText().length() > 0) {
                addTaggedSearch(queryText.getText().toString(), tagEditText.getText().toString());
                queryText.setText("");//clean quertyEditText
                tagEditText.setText("");//clean tagEditText
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tagEditText.getWindowToken(), 0);
            } else // mesage with advise to input reques and tag
            {
                //Create AlertDialog Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                //Set dialog window header and message
                builder.setMessage(R.string.missingMessage);

                //Button OK just close the dialog window
                builder.setPositiveButton(R.string.ok, null);

                //Create object AlertDialog basis AlertDialogBeilder
                AlertDialog errorDialog = builder.create();
                errorDialog.show();// Modal dialog window output
            }
        }
    };

    private void addTaggedSearch(String s, String s1) {

    }
}
