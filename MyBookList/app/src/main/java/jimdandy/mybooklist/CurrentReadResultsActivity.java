package jimdandy.mybooklist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import jimdandy.mybooklist.Data.BookContract;
import jimdandy.mybooklist.Data.BookDBHelper;
import jimdandy.mybooklist.Utilities.GoodReadsUtils;

/**
 * Created by TrevorSpear on 6/15/17.
 */

public class CurrentReadResultsActivity extends AppCompatActivity implements GoodReadsSearchAdapter.OnSearchResultClickListener {

    private RecyclerView mSavedSearchResultsRV;
    private SQLiteDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search_results);

        BookDBHelper dbHelper = new BookDBHelper(this);
        mDB = dbHelper.getReadableDatabase();

        ArrayList<GoodReadsUtils.SearchResult> searchResultsList = getAllSavedSearchResults();
        GoodReadsSearchAdapter adapter = new GoodReadsSearchAdapter(this);
        adapter.updateSearchResults(searchResultsList);

        mSavedSearchResultsRV = (RecyclerView)findViewById(R.id.rv_saved_search_results);
        mSavedSearchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        mSavedSearchResultsRV.setHasFixedSize(true);
        mSavedSearchResultsRV.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        mDB.close();
        super.onDestroy();
    }

    @Override
    public void onSearchResultClick(GoodReadsUtils.SearchResult searchResult) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(GoodReadsUtils.SearchResult.EXTRA_SEARCH_RESULT, searchResult);
        startActivity(intent);
    }

    private ArrayList<GoodReadsUtils.SearchResult> getAllSavedSearchResults() {
        String sqlSelection = BookContract.FavoriteRepos.COLUMN_CURRENT + " = ?";
        String[] selectionArgs = { "True" };

        Cursor cursor = mDB.query(
                BookContract.FavoriteRepos.TABLE_NAME,
                null,
                sqlSelection,
                selectionArgs,
                null,
                null,
                BookContract.FavoriteRepos.COLUMN_TIMESTAMP + " DESC"
        );

        ArrayList<GoodReadsUtils.SearchResult> searchResultsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            GoodReadsUtils.SearchResult searchResult = new GoodReadsUtils.SearchResult();
            searchResult.title = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteRepos.COLUMN_TITLE)
            );
            searchResult.largeImageURL = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteRepos.COLUMN_IMAGE_URL)
            );
            searchResult.author = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteRepos.COLUMN_AUTHOR)
            );
            searchResult.avgRating = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteRepos.COLUMN_RATING)
            );
            searchResultsList.add(searchResult);
        }
        cursor.close();
        return searchResultsList;
    }
}