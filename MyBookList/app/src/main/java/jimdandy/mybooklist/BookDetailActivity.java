package jimdandy.mybooklist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jimdandy.mybooklist.Data.BookContract;
import jimdandy.mybooklist.Data.BookDBHelper;
import jimdandy.mybooklist.Utilities.GoodReadsUtils;

public class BookDetailActivity extends AppCompatActivity {
    private TextView mDetailedTitle;
    private TextView mDetailedAuthor;
    private TextView mDetailedPubDate;
    private TextView mDetailedAvgRating;
    private ImageView mDetailedBookImage;
    private SQLiteDatabase mDB;
    private GoodReadsUtils.SearchResult mSearchResult;
    private boolean mGoing = false;
    private boolean mCurrent = false;
    private boolean mFuture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        mDetailedTitle = (TextView) findViewById(R.id.tv_book_detail_title);
        mDetailedAuthor = (TextView) findViewById(R.id.tv_book_detail_author);
        mDetailedPubDate = (TextView) findViewById(R.id.tv_book_detail_pub_date);
        mDetailedAvgRating = (TextView) findViewById(R.id.tv_book_detail_avg_rating);
        mDetailedBookImage = (ImageView) findViewById(R.id.iv_book_detail_book_image);

        BookDBHelper dbHelper = new BookDBHelper(this);
        //mDB = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(GoodReadsUtils.SearchResult.EXTRA_SEARCH_RESULT)) {
            mSearchResult = (GoodReadsUtils.SearchResult) intent.getSerializableExtra(GoodReadsUtils.SearchResult.EXTRA_SEARCH_RESULT);
            mDetailedTitle.setText(mSearchResult.title);
            mDetailedAuthor.setText(mSearchResult.author);
            mDetailedPubDate.setText(mSearchResult.publicationDate);
            mDetailedAvgRating.setText(mSearchResult.avgRating);

            Glide.with(mDetailedBookImage.getContext())
                    .load(mSearchResult.largeImageURL)
                    .into(mDetailedBookImage);

            //checkResultIsInDB();
        }

        //addBookToList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_detailed_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_goodreads:
                viewBookOnWeb();
                return true;
            case R.id.action_add_to_list:
                addBookToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void viewBookOnWeb() {
        if (mSearchResult != null) {
            String url = GoodReadsUtils.buildGoodReadsViewBookOnWebURL(mSearchResult);
            Uri webPage = Uri.parse(url);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
            }
        }
    }

    private boolean checkResultIsInDB() {
        boolean isInDB = false;
        if (mSearchResult != null) {
            String sqlSelection = BookContract.FavoriteRepos.COLUMN_TITLE + " = ?";
            String[] sqlSelectionArgs = { mSearchResult.title };
            Cursor cursor = mDB.query(
                    BookContract.FavoriteRepos.TABLE_NAME,
                    null,
                    sqlSelection,
                    sqlSelectionArgs,
                    null,
                    null,
                    null
            );

            isInDB = cursor.getCount() > 0;
            cursor.close();
        }
        return isInDB;
    }

    public void addBookToList() {               //!!
        if (mSearchResult != null) {
            ContentValues values = new ContentValues();
            values.put(BookContract.FavoriteRepos.COLUMN_TITLE, mSearchResult.title);
            values.put(BookContract.FavoriteRepos.COLUMN_AUTHOR, mSearchResult.author);
            values.put(BookContract.FavoriteRepos.COLUMN_BOOK_URL, mSearchResult.goodReadsBestBookID);
            values.put(BookContract.FavoriteRepos.COLUMN_IMAGE_URL, mSearchResult.largeImageURL);
            values.put(BookContract.FavoriteRepos.COLUMN_RATING, mSearchResult.avgRating);
            values.put(BookContract.FavoriteRepos.COLUMN_GOING, mGoing);
            values.put(BookContract.FavoriteRepos.COLUMN_CURRENT, mCurrent);
            values.put(BookContract.FavoriteRepos.COLUMN_FUTURE, mFuture);
            mDB.insert(BookContract.FavoriteRepos.TABLE_NAME, null, values);
            return;
        } else {
            return;
        }
    }
}
