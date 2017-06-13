package jimdandy.mybooklist.Utilities;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        return url;

    }

    public static ArrayList<SearchResult> parseGoodReadsSearchResultsXML(String searchResultsXML) {             //!!
        ArrayList<SearchResult> formattedResultsList = new ArrayList<SearchResult>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setIgnoringComments(true);
//            factory.setIgnoringElementContentWhitespace(true);
//            factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream searchResultsStream = new ByteArrayInputStream(searchResultsXML.getBytes("UTF-8"));
            Document xml = builder.parse(searchResultsStream);

            NodeList worksList = xml.getElementsByTagName("work");
            Log.d("PARSER: ", "NUMBER OF WORKS: " + worksList.getLength());

            // EVAN WORKING ON THIS

//            for (int i = 0; i < worksList.getLength(); i++) {
//
//                Node workNode = worksList.item(i);
//                Element
//            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        SearchResult test = new SearchResult();
        test.title = "Naznok";
        test.author = "Slingsquid";
        ArrayList<SearchResult> list = new ArrayList<SearchResult>();
        list.add(test);
        return list;
    }
}