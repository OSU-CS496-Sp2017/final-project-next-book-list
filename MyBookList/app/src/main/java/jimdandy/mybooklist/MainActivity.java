package jimdandy.mybooklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import jimdandy.mybooklist.Utilities.GoodReadsUtils;
import jimdandy.mybooklist.Utilities.NetworkUtils;


/**
            TODO:
            1. FORMAT layout - search_result_view & FORMAT GoodReadsUtils SearchResult object

            2. Make correct call to GoodReads and ensure XML is returned

            3. Make parser for returned XML

            4. PLUG IN results to MainActivity - finish asyncloader shit

            SEARCH '!!' to find spots in code that need attention/have potential issues
 */


public class MainActivity extends AppCompatActivity
implements GoodReadsSearchAdapter.OnSearchResultClickListener, LoaderManager.LoaderCallbacks<String>,
        NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SEARCH_URL_KEY = "goodReadsURL";
    private static final int GOODREADS_SEARCH_LOADER_ID = 0;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView mSearchResultsRV;
    private EditText mSearchBoxET;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mLoadingErrorMessageTV;
    private GoodReadsSearchAdapter mGoodReadsSearchAdapter;
    private View mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mMainLayout = (View) findViewById(R.id.ll_main);
        mSearchBoxET = (EditText) findViewById(R.id.et_search_box);
        mLoadingIndicatorPB = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = (TextView) findViewById(R.id.tv_loading_error_message);
        mSearchResultsRV = (RecyclerView) findViewById(R.id.rv_search_results);

        mSearchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsRV.setHasFixedSize(true);

        mGoodReadsSearchAdapter = new GoodReadsSearchAdapter(this);
        mSearchResultsRV.setAdapter(mGoodReadsSearchAdapter);

        /*mMainLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {        //!!
            @Override
            public void onSwipeRight() {
                Log.d(TAG, "SWIPE RIGHT DETECTED");
                Intent listIntent = new Intent(MainActivity.this, BookListActivity.class);
                startActivity(listIntent);

            }
        });*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        //!!
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportLoaderManager().initLoader(GOODREADS_SEARCH_LOADER_ID, null, this);

        Button searchButton = (Button) findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mSearchBoxET.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    doGoodReadsSearch(searchQuery);
                }
            }
        });

        NavigationView navigationView = (NavigationView)findViewById(R.id.nv_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doGoodReadsSearch(String searchQuery) {
        String goodReadsSearchUrl = GoodReadsUtils.buildGoodReadsSearchURL(searchQuery);
        Log.d(TAG, "doGoodReadsSearch building URL: " + goodReadsSearchUrl);

        Bundle argsBundle = new Bundle();
        argsBundle.putString(SEARCH_URL_KEY, goodReadsSearchUrl);
        getSupportLoaderManager().restartLoader(GOODREADS_SEARCH_LOADER_ID, argsBundle, this);
    }

    @Override
    public void onSearchResultClick(GoodReadsUtils.SearchResult searchResult) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(GoodReadsUtils.SearchResult.EXTRA_SEARCH_RESULT, searchResult);
        startActivity(intent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mSearchResultsXML;

            @Override
            protected void onStartLoading() {
                if (args != null) {
                    if (mSearchResultsXML != null) {
                        Log.d(TAG, "AsyncTaskLoader delivering cached results");
                        deliverResult(mSearchResultsXML);
                    } else {
                        mLoadingIndicatorPB.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }
            }

            @Override
            public String loadInBackground() {
                if (args != null) {
                    String goodReadsSearchUrl = args.getString(SEARCH_URL_KEY);
                    Log.d(TAG, "AsyncTaskLoader making network call: " + goodReadsSearchUrl);
                    String searchResults = null;
                    try {
                        searchResults = NetworkUtils.doHTTPGet(goodReadsSearchUrl);
                    } catch (IOException e) {
                        Log.d(TAG, "LOAD IN BACKGROUND CATCH BLOCK TRIGGERED");
                        e.printStackTrace();
                    }
                    return searchResults;
                } else {
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                mSearchResultsXML = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "AsyncTaskLoader's onLoadFinished called");
        mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
        if (data != null) {
            mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
            mSearchResultsRV.setVisibility(View.VISIBLE);
            ArrayList<GoodReadsUtils.SearchResult> searchResultsList = GoodReadsUtils.parseGoodReadsSearchResultsXML(data);
            Log.d(TAG, "FIRST BOOK IN LIST RETURNED: " + searchResultsList.get(0).title + " " + searchResultsList.get(0).author);
            mGoodReadsSearchAdapter.updateSearchResults(searchResultsList);
        } else {
            mSearchResultsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // nada, nichts, nothing
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent savedResultsIntent;

        switch (item.getItemId()) {
            case R.id.nav_close:
                mDrawerLayout.closeDrawers();
                return true;

            case R.id.nav_going_to_read:
                mDrawerLayout.closeDrawers();
                savedResultsIntent = new Intent(this, SavedSearchResultsActivity.class);
                savedResultsIntent.putExtra("Going", "Going");
                startActivity(savedResultsIntent);
                return true;

            case R.id.nav_currently_reading:
                mDrawerLayout.closeDrawers();
                savedResultsIntent = new Intent(this, SavedSearchResultsActivity.class);
                savedResultsIntent.putExtra("Current", "Current");
                startActivity(savedResultsIntent);
                return true;

            case R.id.nav_future_reading:
                mDrawerLayout.closeDrawers();
                savedResultsIntent = new Intent(this, SavedSearchResultsActivity.class);
                savedResultsIntent.putExtra("Finished", "Finished");
                startActivity(savedResultsIntent);
                return true;

            default:
                return false;
        }
    }
}