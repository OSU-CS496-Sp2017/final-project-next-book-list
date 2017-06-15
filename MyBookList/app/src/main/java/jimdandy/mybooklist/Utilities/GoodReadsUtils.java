package jimdandy.mybooklist.Utilities;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.HttpUrl;

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

    private final static String GOODREADS_VIEW_BOOK_ON_WEB_BASE_URL = "https://www.goodreads.com/book/show/";


    public static class SearchResult implements Serializable {
        public static final String EXTRA_SEARCH_RESULT = "GoodReadsUtils.SearchResult";
        public String title;
        public String author;
        public String publicationDate;
        public String avgRating;
        public String largeImageURL;
        public String smallImageURL;
        public String goodReadsBestBookID;

    }

    public static String buildGoodReadsSearchURL(String searchTerm) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GOODREADS_SEARCH_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(GOODREADS_SEARCH_QUERY_PARAM, searchTerm);
        urlBuilder.addQueryParameter(GOODREADS_SEARCH_KEY_PARAM, GOODREADS_SEARCH_KEY);
        String url = urlBuilder.build().toString();
        return url;

    }

    public static String buildGoodReadsViewBookOnWebURL(SearchResult searchResult) {
        String url = GOODREADS_VIEW_BOOK_ON_WEB_BASE_URL + searchResult.goodReadsBestBookID + "." +
                searchResult.title;
        return url;
    }


    public static ArrayList<SearchResult> parseGoodReadsSearchResultsXML(String searchResultsXML) {             //!!
        ArrayList<SearchResult> formattedResultsList = new ArrayList<SearchResult>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream searchResultsStream = new ByteArrayInputStream(searchResultsXML.getBytes("UTF-8"));
            Document xml = builder.parse(searchResultsStream);

            NodeList worksList = xml.getElementsByTagName("work");
            for (int i = 0; i < worksList.getLength(); i++) {
                Element work = (Element)worksList.item(i);
                NodeList bestBookList = work.getElementsByTagName("best_book");
                Element bestBook = (Element)bestBookList.item(0);

                SearchResult searchResult = new SearchResult();

                //      PUBLICATION DATE
                String pubDay = getXMLTagValue(work, "original_publication_day");
                String pubMonth = getXMLTagValue(work, "original_publication_month");
                String pubYear = getXMLTagValue(work, "original_publication_year");

                String fullPubDate = "";
                if (pubDay != "") {
                    fullPubDate += pubDay + "/";
                }
                if (pubMonth != "") {
                    fullPubDate += pubMonth + "/";
                }
                if (pubYear != "") {
                    fullPubDate += pubYear;
                }

                searchResult.publicationDate = fullPubDate;

                //      AVERAGE RATING
                String avgRatingValue = getXMLTagValue(work, "average_rating");
                searchResult.avgRating = avgRatingValue;

                //      TITLE
                String titleValue = getXMLTagValue(bestBook, "title");
                searchResult.title = titleValue;

                //      AUTHOR
                String authorValue = getXMLTagValue(bestBook, "name");
                searchResult.author = authorValue;

                //      URLS
                String largeURLValue = getXMLTagValue(bestBook, "image_url");
                searchResult.largeImageURL = largeURLValue;

                String smallURLValue = getXMLTagValue(bestBook, "small_image_url");
                searchResult.smallImageURL = smallURLValue;

                //      ID
                String IDValue = getXMLTagValue(bestBook, "id");
                searchResult.goodReadsBestBookID = IDValue;

                // ADD TO RETURN LIST
                Log.d("UTILS: ", searchResult.title + " by " + searchResult.author +
                        " published: " + searchResult.publicationDate +
                        " with an average rating of " + searchResult.avgRating + "\n" +
                        "URLs at: " + searchResult.largeImageURL + " and " + searchResult.smallImageURL
                );

                formattedResultsList.add(searchResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedResultsList;
    }

    private static String getXMLTagValue(Element parentElement, String tagName) {
        NodeList NodeList = parentElement.getElementsByTagName(tagName);
        Element tagElement = (Element)NodeList.item(0);
        Node valueNode = tagElement.getChildNodes().item(0);

        String value = "";
        if (valueNode != null) {
            value = String.valueOf(valueNode.getNodeValue());
        }
        return value;
    }
}