package jimdandy.mybooklist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jimdandy.mybooklist.Utilities.GoodReadsUtils;

/**
 * Created by jimdandy on 6/9/17.
 */

class GoodReadsSearchAdapter extends RecyclerView.Adapter<GoodReadsSearchAdapter.SearchResultViewHolder>{
    private ArrayList<GoodReadsUtils.SearchResult> mSearchResultsList;
    private OnSearchResultClickListener mSearchResultClickListener;

    public GoodReadsSearchAdapter(OnSearchResultClickListener clickListener) {
        mSearchResultClickListener = clickListener;
    }

    public void updateSearchResults(ArrayList<GoodReadsUtils.SearchResult> searchResultsList) {
        mSearchResultsList = searchResultsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mSearchResultsList != null) {
            return mSearchResultsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_result_item, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        holder.bind(mSearchResultsList.get(position));
    }

    public interface OnSearchResultClickListener {          //!!
        void onSearchResultClick(GoodReadsUtils.SearchResult searchResult);
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout searchResultLayout;
        private TextView mTitle;
        private TextView mAuthor;
        private TextView mPubDate;
        private TextView mAvgRating;

        public SearchResultViewHolder(View itemView) {      //!!
            super(itemView);
            mTitle = (TextView)itemView.findViewById(R.id.tv_search_result_title);
            mAuthor = (TextView)itemView.findViewById(R.id.tv_search_result_author);
            mPubDate = (TextView)itemView.findViewById(R.id.tv_search_result_pub_date);
            mAvgRating = (TextView)itemView.findViewById(R.id.tv_search_result_avg_rating);

            searchResultLayout = (LinearLayout) itemView.findViewById(R.id.ll_search_result);
            searchResultLayout.setOnClickListener(this);
        }

        public void bind(GoodReadsUtils.SearchResult searchResult) {        //!!
            mTitle.setText(searchResult.title);
            mAuthor.setText(searchResult.author);
            mPubDate.setText(searchResult.publicationDate);
            mAvgRating.setText(String.valueOf(searchResult.avgRating));
        }

        @Override
        public void onClick(View v) {
            GoodReadsUtils.SearchResult searchResult = mSearchResultsList.get(getAdapterPosition());
            mSearchResultClickListener.onSearchResultClick(searchResult);
        }
    }

}
