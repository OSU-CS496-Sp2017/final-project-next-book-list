package jimdandy.mybooklist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    private String mGoingString = "False";
    private String mCurrentString = "False";
    private String mFutureString = "False";

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
        mDB = dbHelper.getWritableDatabase();

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

            System.out.println("HERE");
            System.out.println(mSearchResult);
            System.out.println("HERE");

        }

        final Button buttonOne = (Button) findViewById(R.id.tv_add_going_to_read);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                System.out.println("mGoing Start");

                System.out.println(mGoing);

                mGoing = !mGoing;

                System.out.println(mGoing);

                System.out.println("mGoing end");

                if(mGoing){
                    buttonOne.setBackgroundColor(Color.GREEN);
                    addBookToList();
                }else {
                    buttonOne.setBackgroundColor(Color.RED);
                    deleteSearchResultFromDB();
                }

            }
        });

        final Button buttonTwo = (Button) findViewById(R.id.tv_add_currently_reading);
        buttonTwo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                System.out.println("mCurrent Start");
                System.out.println(mCurrent);

                mCurrent = !mCurrent;

                System.out.println(mCurrent);
                System.out.println("mCurrent End");

                if(mCurrent){
                    buttonTwo.setBackgroundColor(Color.GREEN);
                    addBookToList();
                }else {
                    buttonTwo.setBackgroundColor(Color.RED);
                    deleteSearchResultFromDB();
                }

            }
        });

        final Button buttonThree = (Button) findViewById(R.id.tv_add_future_reading);
        buttonThree.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                System.out.println("mFuture Start");
                System.out.println(mFuture);

                mFuture = !mFuture;

                System.out.println(mFuture);
                System.out.println("mFuture End");

                if(mFuture){
                    buttonThree.setBackgroundColor(Color.GREEN);
                    addBookToList();
                }else {
                    buttonThree.setBackgroundColor(Color.RED);
                    deleteSearchResultFromDB();
                }

            }
        });



        //Sets the True/False
        if(checkGoingIsInDB()){
            buttonOne.setBackgroundColor(Color.GREEN);
        }else{
            buttonOne.setBackgroundColor(Color.RED);
        }

        if(checkCurrentIsInDB()){
            buttonTwo.setBackgroundColor(Color.GREEN);
        }else{
            buttonTwo.setBackgroundColor(Color.RED);
        }

        if(checkFinishedIsInDB()){
            buttonThree.setBackgroundColor(Color.GREEN);
        }else{
            buttonThree.setBackgroundColor(Color.RED);
        }


        System.out.println("Start");
        System.out.println(mGoing);
        System.out.println(mCurrent);
        System.out.println(mFuture);
        System.out.println("Dank memes");

        checkResultIsInDB();

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
            //case R.id.action_add_to_list:
                //addBookToList();
                //checkCurrentIsInDB();
                //return true;
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
            String sqlSelection = BookContract.FavoriteBook.COLUMN_TITLE + " = ?";
            String[] sqlSelectionArgs = { mSearchResult.title };
            Cursor cursor = mDB.query(
                    BookContract.FavoriteBook.TABLE_NAME,
                    null,
                    sqlSelection,
                    sqlSelectionArgs,
                    null,
                    null,
                    null
            );

            isInDB = cursor.getCount() > 0;

            System.out.println(isInDB);

            cursor.close();
        }
        return isInDB;
    }

    private boolean checkGoingIsInDB() {
        boolean isInDB = false;
        if (mSearchResult != null) {
            String sqlSelection = BookContract.FavoriteBook.COLUMN_TITLE + " = ?";
            String[] sqlSelectionArgs = { mSearchResult.title };
            Cursor cursor = mDB.query(
                    BookContract.FavoriteBook.TABLE_NAME,
                    null,
                    sqlSelection,
                    sqlSelectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()){
                do{
                    String data = cursor.getString(cursor.getColumnIndex("going"));

                    System.out.println("GRABBING DATA");
                    System.out.println(data);
                    System.out.println("END");

                    if(data.equals("True")){
                        mGoing = true;
                        isInDB = true;
                    }else{
                        mGoing = false;
                        isInDB = false;
                    }

                }while(cursor.moveToNext());
            }
            cursor.close();

        }

        return isInDB;
    }

    private boolean checkCurrentIsInDB() {
        boolean isInDB = false;
        if (mSearchResult != null) {
            String sqlSelection = BookContract.FavoriteBook.COLUMN_TITLE + " = ?";
            String[] sqlSelectionArgs = { mSearchResult.title };
            Cursor cursor = mDB.query(
                    BookContract.FavoriteBook.TABLE_NAME,
                    null,
                    sqlSelection,
                    sqlSelectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()){
                do{
                    String data = cursor.getString(cursor.getColumnIndex("current"));

                    System.out.println("GRABBING DATA");
                    System.out.println(data);
                    System.out.println("END");

                    if(data.equals("True")){
                        mCurrent = true;
                        isInDB = true;
                    }else{
                        mCurrent = false;
                        isInDB = false;
                    }

                }while(cursor.moveToNext());
            }
            cursor.close();

        }

        return isInDB;
    }

    private boolean checkFinishedIsInDB() {
        boolean isInDB = false;
        System.out.println("GRABBING DATA");



        if (mSearchResult != null) {
            String sqlSelection = BookContract.FavoriteBook.COLUMN_TITLE + " = ?";
            String[] sqlSelectionArgs = { mSearchResult.title };
            Cursor cursor = mDB.query(
                    BookContract.FavoriteBook.TABLE_NAME,
                    null,
                    sqlSelection,
                    sqlSelectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    String data = cursor.getString(cursor.getColumnIndex("future"));

                    System.out.println("GRABBING DATA");
                    System.out.println(data);
                    System.out.println("END");

                    if(data.equals("True")){
                        mFuture = true;
                        isInDB = true;
                    } else {
                        mFuture = false;
                        isInDB = false;
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();

        }
        return isInDB;
    }

    private void setBooleanStrings(){
        if(mGoing){
            mGoingString = "True";
        }else{
            mGoingString = "False";
        }

        if(mCurrent){
            mCurrentString = "True";
        }else{
            mCurrentString = "False";
        }

        if(mFuture){
            mFutureString = "True";
        }else{
            mFutureString = "False";
        }
    }


    public long addBookToList() {
        setBooleanStrings();

        if(!mGoing && !mCurrent && !mFuture){
            deleteSearchResultFromDB();


        }else if(checkResultIsInDB() && mSearchResult != null){

            String sqlSelection = BookContract.FavoriteBook.COLUMN_TITLE + " = ?";
            String[] sqlSelectionArgs = { mSearchResult.title };
            ContentValues values = new ContentValues();
            values.put(BookContract.FavoriteBook.COLUMN_GOING, mGoingString);
            values.put(BookContract.FavoriteBook.COLUMN_CURRENT, mCurrentString);
            values.put(BookContract.FavoriteBook.COLUMN_FUTURE, mFutureString);
            values.put(BookContract.FavoriteBook.COLUMN_RATING, mSearchResult.avgRating);
            return mDB.update(BookContract.FavoriteBook.TABLE_NAME, values, sqlSelection, sqlSelectionArgs);

        } else if (mSearchResult != null) {

            ContentValues values = new ContentValues();
            values.put(BookContract.FavoriteBook.COLUMN_TITLE, mSearchResult.title);
            values.put(BookContract.FavoriteBook.COLUMN_AUTHOR, mSearchResult.author);
            values.put(BookContract.FavoriteBook.COLUMN_IMAGE_URL, mSearchResult.largeImageURL);
            values.put(BookContract.FavoriteBook.COLUMN_BOOK_ID, mSearchResult.goodReadsBestBookID);

            values.put(BookContract.FavoriteBook.COLUMN_RATING, mSearchResult.avgRating);
            values.put(BookContract.FavoriteBook.COLUMN_GOING, mGoingString);
            values.put(BookContract.FavoriteBook.COLUMN_CURRENT, mCurrentString);
            values.put(BookContract.FavoriteBook.COLUMN_FUTURE, mFutureString);

            return mDB.insert(BookContract.FavoriteBook.TABLE_NAME, null, values);



        }

        return -1;

    }

    private void deleteSearchResultFromDB() {
        setBooleanStrings();

        if(mGoing == false && mCurrent == false && mFuture == false) {
            System.out.println("Deleting");

            if (mSearchResult != null) {
                String sqlSelection = BookContract.FavoriteBook.COLUMN_TITLE + " = ?";
                String[] sqlSelectionArgs = {mSearchResult.title};
                mDB.delete(BookContract.FavoriteBook.TABLE_NAME, sqlSelection, sqlSelectionArgs);
            }
        }else if(checkResultIsInDB() && mSearchResult != null){

            String sqlSelection = BookContract.FavoriteBook.COLUMN_TITLE + " = ?";
            String[] sqlSelectionArgs = { mSearchResult.title };
            ContentValues values = new ContentValues();
            values.put(BookContract.FavoriteBook.COLUMN_GOING, mGoingString);
            values.put(BookContract.FavoriteBook.COLUMN_CURRENT, mCurrentString);
            values.put(BookContract.FavoriteBook.COLUMN_FUTURE, mFutureString);
            values.put(BookContract.FavoriteBook.COLUMN_RATING, mSearchResult.avgRating);
            mDB.update(BookContract.FavoriteBook.TABLE_NAME, values, sqlSelection, sqlSelectionArgs);

        }
    }


}
