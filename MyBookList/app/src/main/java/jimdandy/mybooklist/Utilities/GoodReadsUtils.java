package jimdandy.mybooklist.Utilities;

import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.HttpUrl;

import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/*
    PARAMETERS for a search
    q: The query text to match against book title, author, and ISBN fields. Supports boolean operators and phrase searching.
    page: Which page to return (default 1, optional)
    key: Developer key (required).
    search[field]: Field to search, one of 'title', 'author', or 'all' (default is 'all')

    xml nesting:
    search -> results -> works
 */


public class GoodReadsUtils {

    private final static String GOODREADS_SEARCH_BASE_URL = "https://www.goodreads.com/search/index.xml ";
    private final static String GOODREADS_SEARCH_QUERY_PARAM = "q";
    private final static String GOODREADS_SEARCH_KEY_PARAM = "key";
    private final static String GOODREADS_SEARCH_KEY = "hyjexwpae21tTWdLAXhBw";


    public static class SearchResult implements Serializable {
        public static final String EXTRA_SEARCH_RESULT = "GoodReadsUtils.SearchResult";
        public String title;
        public String author;
        public String publicationDate;
        public String description;
        public double avgRating;
        public String imageURL;
    }

    public static String buildGoodReadsSearchURL(String searchTerm) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GOODREADS_SEARCH_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(GOODREADS_SEARCH_QUERY_PARAM, searchTerm);
        urlBuilder.addQueryParameter(GOODREADS_SEARCH_KEY_PARAM, GOODREADS_SEARCH_KEY);
        String url = urlBuilder.build().toString();
        Log.d("MAIN", "URL: " + url);
        return url;

    }

    public static ArrayList<SearchResult> parseGoodReadsSearchResultsXML(String searchResultsXML) {             //!!

//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setIgnoringComments(true);
//            factory.setIgnoringElementContentWhitespace(true);
//            factory.setValidating(true);
//            DocumentBuilder builder = factory.newDocumentBuilder();
//
//            Document xml = builder.parse(new InputSource(searchResultsXML));
//
//            Log.d("PARSER: ", "ROOT: " + xml.getDocumentElement());
//
//            //          FINISH PARSER
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        SearchResult test = new SearchResult();
        test.title = "Naznok";
        test.author = "Slingsquid";
        ArrayList<SearchResult> list = new ArrayList<SearchResult>();
        list.add(test);
        return list;
    }
}