package com.example.vanessa.p_etika;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;

public class DiseasesActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    ImageButton imageButton;

    public final static String EXTRA_DISEASE = "EXTRA_DISEASE";
    public final int REQUEST_RESPONSE = 1; //chnge from pub fin to fin only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        final ListView lv = (ListView)findViewById(R.id.listview_disease);

        ArrayList<String> arrayDisease = new ArrayList<>();
        arrayDisease.addAll(Arrays.asList(getResources().getStringArray(R.array.array_diseases)));

        adapter = new ArrayAdapter<>(DiseasesActivity.this, android.R.layout.simple_list_item_1, arrayDisease);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                intent.putExtra(EXTRA_DISEASE, adapter.getItem(position));
                startActivityForResult(intent, REQUEST_RESPONSE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
