package jimdandy.mybooklist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jimdandy.mybooklist.Utilities.GoodReadsUtils;

public class BookDetailActivity extends AppCompatActivity {
    private TextView mDetailedTitle;
    private TextView mDetailedAuthor;
    private TextView mDetailedPubDate;
    private TextView mDetailedAvgRating;
    private ImageView mDetailedBookImage;
    private GoodReadsUtils.SearchResult mSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        mDetailedTitle = (TextView) findViewById(R.id.tv_book_detail_title);
        mDetailedAuthor = (TextView) findViewById(R.id.tv_book_detail_author);
        mDetailedPubDate = (TextView) findViewById(R.id.tv_book_detail_pub_date);
        mDetailedAvgRating = (TextView) findViewById(R.id.tv_book_detail_avg_rating);
        mDetailedBookImage = (ImageView) findViewById(R.id.iv_book_detail_book_image);

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
        }
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

    public void addBookToList() {               //!!

    }
}
