package jimdandy.mybooklist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import jimdandy.mybooklist.Data.BookContract;
import jimdandy.mybooklist.Data.BookDBHelper;
import jimdandy.mybooklist.Utilities.GoodReadsUtils;

public class SavedSearchResultsActivity extends AppCompatActivity implements GoodReadsSearchAdapter.OnSearchResultClickListener {

    private RecyclerView mSavedSearchResultsRV;
    private SQLiteDatabase mDB;
    private String listPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search_results);

        BookDBHelper dbHelper = new BookDBHelper(this);
        mDB = dbHelper.getReadableDatabase();

        ArrayList<GoodReadsUtils.SearchResult> searchResultsList;// = getAllSavedSearchResults();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Going")) {
            searchResultsList = getGoingSavedSearchResults();
            listPath = "Going";

        }else if (intent != null && intent.hasExtra("Finished")) {
            searchResultsList = getFinishedSavedSearchResults();
            listPath = "Finished";

        }else {
            searchResultsList = getCurrentSavedSearchResults();
            listPath = "Current";

        }

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

    private ArrayList<GoodReadsUtils.SearchResult> getCurrentSavedSearchResults() {
        String sqlSelection = BookContract.FavoriteBook.COLUMN_CURRENT + " = ?";
        String[] selectionArgs = { "True" };

        Cursor cursor = mDB.query(
                BookContract.FavoriteBook.TABLE_NAME,
                null,
                sqlSelection,
                selectionArgs,
                null,
                null,
                BookContract.FavoriteBook.COLUMN_TIMESTAMP + " DESC"
        );

        ArrayList<GoodReadsUtils.SearchResult> searchResultsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            GoodReadsUtils.SearchResult searchResult = new GoodReadsUtils.SearchResult();
            searchResult.title = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_TITLE)
            );
            searchResult.largeImageURL = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_IMAGE_URL)
            );
            searchResult.author = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_AUTHOR)
            );
            searchResult.avgRating = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_RATING)
            );
            searchResult.goodReadsBestBookID = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_BOOK_ID)
            );
            searchResultsList.add(searchResult);
        }
        cursor.close();
        return searchResultsList;
    }

    private ArrayList<GoodReadsUtils.SearchResult> getFinishedSavedSearchResults() {
        String sqlSelection = BookContract.FavoriteBook.COLUMN_FUTURE + " = ?";
        String[] selectionArgs = { "True" };

        Cursor cursor = mDB.query(
                BookContract.FavoriteBook.TABLE_NAME,
                null,
                sqlSelection,
                selectionArgs,
                null,
                null,
                BookContract.FavoriteBook.COLUMN_TIMESTAMP + " DESC"
        );

        ArrayList<GoodReadsUtils.SearchResult> searchResultsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            GoodReadsUtils.SearchResult searchResult = new GoodReadsUtils.SearchResult();
            searchResult.title = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_TITLE)
            );
            searchResult.largeImageURL = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_IMAGE_URL)
            );
            searchResult.author = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_AUTHOR)
            );
            searchResult.avgRating = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_RATING)
            );
            searchResult.goodReadsBestBookID = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_BOOK_ID)
            );
            searchResultsList.add(searchResult);
        }
        cursor.close();
        return searchResultsList;
    }

    private ArrayList<GoodReadsUtils.SearchResult> getGoingSavedSearchResults() {
        String sqlSelection = BookContract.FavoriteBook.COLUMN_CURRENT + " = ?";
        String[] selectionArgs = { "True" };

        Cursor cursor = mDB.query(
                BookContract.FavoriteBook.TABLE_NAME,
                null,
                sqlSelection,
                selectionArgs,
                null,
                null,
                BookContract.FavoriteBook.COLUMN_TIMESTAMP + " DESC"
        );

        ArrayList<GoodReadsUtils.SearchResult> searchResultsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            GoodReadsUtils.SearchResult searchResult = new GoodReadsUtils.SearchResult();
            searchResult.title = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_TITLE)
            );
            searchResult.largeImageURL = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_IMAGE_URL)
            );
            searchResult.author = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_AUTHOR)
            );
            searchResult.avgRating = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_RATING)
            );
            searchResult.goodReadsBestBookID = cursor.getString(
                    cursor.getColumnIndex(BookContract.FavoriteBook.COLUMN_BOOK_ID)
            );
            searchResultsList.add(searchResult);
        }
        cursor.close();
        return searchResultsList;
    }


}
