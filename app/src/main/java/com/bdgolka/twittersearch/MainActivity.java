package com.bdgolka.twittersearch;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

        //getting references on EditText
        queryText = (EditText) findViewById(R.id.queryEditText);
        tagEditText = (EditText) findViewById(R.id.tagEditText);

        //Getting object SharedPreferences for saved requests
        savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);

        //Save tags in ArrayList and sorting
        tags = new ArrayList<>(savedSearches.getAll().keySet());
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);

        //Creating object ArrayAdapter and binding tags to ArrayView
        adapter = new ArrayAdapter<>(this, R.layout.list_item, tags);
        setListAdapter(adapter);

        //Listener registration for saving request
        ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);

        saveButton.setOnClickListener(saveButtonListener);

        //Listener registration for searching in Twitter
        getListView().setOnItemClickListener(itemClickListener);

        //Set listener who is able to delete or edit request
        getListView().setOnItemLongClickListener(itemLongClickListener);
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

    private void addTaggedSearch(String query, String tag) {
        //Get object SharedPreferences.Editor to save new pare
        SharedPreferences.Editor preferenceEditor = savedSearches.edit();
        preferenceEditor.putString(tag, query); //save current query
        preferenceEditor.apply();// save changes

        // If tag was just created - add and sort tags
        if (!tags.contains(tag)) {
            tags.add(tag); // add new tag
            Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
            adapter.notifyDataSetChanged(); // reconnection to Listview
        }
    }

    //itemClickListener launch browser for result output
    public OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            //Get string request and create URL-address for request
            String tag = ((TextView) view).getText().toString();
            String urlString = getString(R.string.searchURL) + Uri.encode(savedSearches.getString(tag, ""), "UTF-8");

            //Create intent for launching browser
            Intent wevIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
            startActivity(wevIntent);//Start browser for watching result
        }
    };

    //itemLongClickListener shows dialog window for deleting or editing saved request
    public OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            //get tag that was long clicked
            final String tag = ((TextView) view).getText().toString();

            //Create new AlertDialog object
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            //create new title
            builder.setTitle(getString(R.string.shareEditDeleteTitle, tag));

            //assigning the list of options to output in dialog window
            builder.setItems(R.array.dialog_items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0://share
                            sharedSearch(tag);
                            break;
                        case 1: //edit
                            //fill EditText with tag and request
                            tagEditText.setText(tag);
                            queryText.setText(savedSearches.getString(tag, ""));
                            break;
                        case 2: //delete
                          deleteSearch(tag);
                            break;
                    }
                }
            });

            //assigning the negative button AlertDialog
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        //called when "Cancel" button pressed
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();//close AlertDialog
                        }
                    }
            );
            builder.create().show();//show AlertDialog
            return true;
        }
    };

    //Delete request after users confirmation
    private void deleteSearch(final String tag) {
        //create new AlertDialog object
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(MainActivity.this);

        //Assigning new AlertDialog
        confirmBuilder.setMessage(getString(R.string.confirmMessage, tag));

        //Assigning AlertDialog negative button
        confirmBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
           //Called when button "Cancel" pressed
                dialog.cancel();
            }
        });

        //Assign AlertDialog positive buttom
        confirmBuilder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            //Call when "Delete" button pressed
            public void onClick(DialogInterface dialog, int i) {
             tags.remove(tag);//delete tag from tags collection

                //Get SharePreferences.Editor to delete request
                SharedPreferences.Editor preferencesEditor = savedSearches.edit();
                preferencesEditor.remove(tag);//delete request
                preferencesEditor.apply();//save changes

                //rebinding for renewed list output
                adapter.notifyDataSetChanged();

            }
        });

        confirmBuilder.create().show(); // AlertDialog output
    }

    //Selection the application to send saved request's URL-address
    private void sharedSearch(String tag) {
        //Create URL-address to represent the request
        String urlString = getString(R.string.searchURL)+ Uri.encode(savedSearches.getString(tag,""), "UTF-8");

        //Create Intent object to send urlString
        Intent shareIntend = new Intent();
        shareIntend.setAction(Intent.ACTION_SEND);
        shareIntend.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareSubject));
        shareIntend.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareMessage, urlString));
        shareIntend.setType("text/plain");

        //Output the List of application with ability to send text
        startActivity(Intent.createChooser(shareIntend,getString(R.string.sharedSearch)));
    }
    
    
}
