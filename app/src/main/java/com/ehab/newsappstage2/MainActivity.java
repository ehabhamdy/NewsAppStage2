package com.ehab.newsappstage2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ehab.newsappstage2.model.Results;

public class MainActivity extends AppCompatActivity implements NewsAdapter.RecyclerViewClickListener, LoaderManager.LoaderCallbacks<List<Results>> {
    public static final int LOADER_ID = 22;
    public static final String BASE_URL = "https://content.guardianapis.com/search?";
    private static final String APIKEY_PARAM = "api-key";
    public static final String TAG_PARAM = "tag";

    RecyclerView newsRecyclerView;
    TextView emptyView;
    ProgressBar progressBar;

    NewsAdapter adapter;
    List<Results> results = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emptyView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.progressBar);
        newsRecyclerView = findViewById(R.id.newsRecylerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newsRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new NewsAdapter(results, this);
        newsRecyclerView.setAdapter(adapter);

        if(isConnected()) {
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }else{
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No Internet Connection");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private Boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    public void onClick(Results result) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(result.getWebUrl()));
        startActivity(intent);
    }

    @Override
    public Loader<List<Results>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String tag = sharedPrefs.getString(
                getString(R.string.settings_tag_key),
                getString(R.string.settings_tag_Default));


        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(TAG_PARAM, tag);
        uriBuilder.appendQueryParameter(APIKEY_PARAM, "test");

        return new NewsLoader(MainActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Results>> loader, List<Results> data) {
        progressBar.setVisibility(View.INVISIBLE);
        toggleEmptyView(data);
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Results>> loader) {
        adapter.setData(new ArrayList<Results>());
    }


    private void toggleEmptyView(List<Results> results) {
        if (results.isEmpty()) {
            newsRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            newsRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
