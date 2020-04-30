package com.example.android.paperboy3;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.paperboy3.adapters.ArticleAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    //private static final String API_KEY = "2c139775-1142-456e-9839-bfb605092025";
    private static final String BASE_REQUEST = "https://content.guardianapis.com/search";
    private static final int ARTICLE_LOADER = 1;
    private ArticleAdapter mAdapter;
    private TextView mEmptyStateTextView;

    // TODO: 4/26/20 figure out how to refresh when internet connection is lost/regained

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find reference to listView
        ListView articleListView = findViewById(R.id.list_of_articles);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleListView.setAdapter(mAdapter);

        //set listener to open story onItemClicked
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article selectedArticle = mAdapter.getItem(position);
                Uri site = null;
                if (selectedArticle != null) {
                    site = Uri.parse(selectedArticle.getWebUrl());
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, site);
                startActivity(intent);
            }
        });

        if (isNetworkAvailable(MainActivity.this)) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER, null, this);
        } else {
            mEmptyStateTextView.setText(R.string.no_internet);
            View loadingCircle = findViewById(R.id.loading_circle);
            loadingCircle.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        String query;
        String numArticles;
        String apiKey;
        String sectionName;
        String orderBy;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        query = sharedPreferences.getString(getString(R.string.settings_search_term_key),
                getString(R.string.settings_search_term_default));
        numArticles = sharedPreferences.getString(getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));
        apiKey = sharedPreferences.getString(getString(R.string.settings_api_key_key_),
                getString(R.string.settings_api_key_default));
        sectionName = sharedPreferences.getString(getString(R.string.settings_section_name_key),
                getString(R.string.settings_section_name_default));
        orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        Uri baseUri = Uri.parse(BASE_REQUEST);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (query != null && !query.isEmpty()) {
            uriBuilder.appendQueryParameter("q", query);
        }
        if (sectionName != null && !sectionName.isEmpty()) {
            uriBuilder.appendQueryParameter("section", sectionName);
        }
        uriBuilder.appendQueryParameter("page-size", numArticles); // default 10
        uriBuilder.appendQueryParameter("show-tags", "contributor"); // get author every query, and
        uriBuilder.appendQueryParameter("show-fields", "thumbnail"); // thumbnail too
        uriBuilder.appendQueryParameter("order-by", orderBy); //oldest, newest, relevance
        uriBuilder.appendQueryParameter("api-key", apiKey); //user can enter their api key or stick with default: 'test'
        Log.d("check query", "built query: " + uriBuilder.toString());
        // create new loader using query developed above
        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
        View loadingCircle = findViewById(R.id.loading_circle);
        // disappear loadingindicator  when loading is finished
        loadingCircle.setVisibility(View.GONE);
        //set emptystatetextView to display only after loading is finished
        mEmptyStateTextView.setText(R.string.no_articles);
        mAdapter.clear();
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

        mAdapter.clear();
        // check internet connectivity
        if (isNetworkAvailable(MainActivity.this)) {
            // set emptyTextView to replace listView if no earthquakes were found
            mEmptyStateTextView.setText(R.string.no_articles);
            loadingCircle = findViewById(R.id.loading_circle);
            loadingCircle.setVisibility(View.GONE);

            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            } else {
                Toast.makeText(MainActivity.this, R.string.list_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            mEmptyStateTextView.setText(R.string.no_internet);
            loadingCircle = findViewById(R.id.loading_circle);
            loadingCircle.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        mAdapter.clear();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preferences_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get id of item that was selected, if it is the hamburger, start SettingsActivity
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
