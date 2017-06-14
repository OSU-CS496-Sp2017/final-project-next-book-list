package jimdandy.mybooklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
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
implements GoodReadsSearchAdapter.OnSearchResultClickListener, LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SEARCH_URL_KEY = "goodReadsURL";
    private static final int GOODREADS_SEARCH_LOADER_ID = 0;

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

        mMainLayout = (View) findViewById(R.id.ll_main);
        mSearchBoxET = (EditText) findViewById(R.id.et_search_box);
        mLoadingIndicatorPB = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = (TextView) findViewById(R.id.tv_loading_error_message);
        mSearchResultsRV = (RecyclerView) findViewById(R.id.rv_search_results);

        mSearchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsRV.setHasFixedSize(true);

        mGoodReadsSearchAdapter = new GoodReadsSearchAdapter(this);
        mSearchResultsRV.setAdapter(mGoodReadsSearchAdapter);

        mMainLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {        //!!
            @Override
            public void onSwipeRight() {
                Log.d(TAG, "SWIPE RIGHT DETECTED");
                Intent listIntent = new Intent(MainActivity.this, BookListActivity.class);
                startActivity(listIntent);

            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        //!!
//        getSupportActionBar().setHomeButtonEnabled(true);

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

}



