package jimdandy.mybooklist.Data;

import android.provider.BaseColumns;

/**
 * Created by hessro on 5/30/17.
 */

public class BookContract {
    private BookContract() {}

    public static class FavoriteBook implements BaseColumns {
        public static final String TABLE_NAME = "favoriteBooks";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_IMAGE_URL = "image";
        public static final String COLUMN_BOOK_ID = "BookId";
        public static final String COLUMN_RATING = "Rating";
        public static final String COLUMN_TIMESTAMP = "TimeStamp";
        public static final String COLUMN_GOING = "going";
        public static final String COLUMN_CURRENT = "current";
        public static final String COLUMN_FUTURE = "future";
    }
}
