package jimdandy.mybooklist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BookDetailActivity extends AppCompatActivity {
    private TextView mDetailedTitle;
    private TextView mDetailedAuthor;
    private TextView mDetailedPubDate;
    private TextView mDetailedAvgRating;
    private TextView mDetailedBookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
    }
}
