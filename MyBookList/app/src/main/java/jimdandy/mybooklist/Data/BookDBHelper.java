package jimdandy.mybooklist.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hessro on 5/30/17.
 */

public class BookDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Book.db";
    private static final int DATABASE_VERSION = 1;

    public BookDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITE_REPOS_TABLE =
                        "CREATE TABLE " + BookContract.FavoriteRepos.TABLE_NAME + " (" +
                        BookContract.FavoriteRepos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BookContract.FavoriteRepos.COLUMN_TITLE + " TEXT NOT NULL, " +
                        BookContract.FavoriteRepos.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                        BookContract.FavoriteRepos.COLUMN_IMAGE_URL + " TEXT, " +
                        BookContract.FavoriteRepos.COLUMN_BOOK_URL + " TEXT NOT NULL, " +
                        //BookContract.FavoriteRepos.COLUMN_RATING + " INTEGER DEFAULT 0, " +
                        BookContract.FavoriteRepos.COLUMN_TIMESTAMP + " INTEGER DEFAULT 0, " +

                        //BookContract.FavoriteRepos.COLUMN_GOING + " BOOLEAN DEFAULT false, " +
                        //BookContract.FavoriteRepos.COLUMN_CURRENT + " BOOLEAN DEFAULT false, " +
                        //BookContract.FavoriteRepos.COLUMN_FUTURE + " BOOLEAN DEFAULT false, " +
                        ");";

        db.execSQL(SQL_CREATE_FAVORITE_REPOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BookContract.FavoriteRepos.TABLE_NAME);
        onCreate(db);
    }
}
