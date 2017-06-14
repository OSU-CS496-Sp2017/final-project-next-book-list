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

    private final static String GOODREADS_VIEW_BOOK_ON_WEB_BASE_URL = "https://www.goodreads.com/book/show/";


    public static class SearchResult implements Serializable {
        public static final String EXTRA_SEARCH_RESULT = "GoodReadsUtils.SearchResult";
        public String title;
        public String author;
        public String publicationDate;
        public String avgRating;
        public String largeImageURL;
        public String smallImageURL;
        public String goodReadsBeskBookID;

    }

    public static String buildGoodReadsSearchURL(String searchTerm) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GOODREADS_SEARCH_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(GOODREADS_SEARCH_QUERY_PARAM, searchTerm);
        urlBuilder.addQueryParameter(GOODREADS_SEARCH_KEY_PARAM, GOODREADS_SEARCH_KEY);
        String url = urlBuilder.build().toString();
        return url;

    }

    public static String buildGoodReadsViewBookOnWebURL(SearchResult searchResult) {
        String url = GOODREADS_VIEW_BOOK_ON_WEB_BASE_URL + searchResult.goodReadsBeskBookID + "." +
                searchResult.title;
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

            for (int i = 0; i < worksList.getLength(); i++) {

                SearchResult searchResult = new SearchResult();
                Element workElement = (Element)worksList.item(i);

                //      PUBLICATION DATE
                NodeList pubDayList = workElement.getElementsByTagName("original_publication_month");
                NodeList pubMonthList = workElement.getElementsByTagName("original_publication_month");
                NodeList pubYearList = workElement.getElementsByTagName("original_publication_year");

                Element pubDayElement = (Element)pubDayList.item(0);
                Node pubDayNode = pubDayElement.getChildNodes().item(0);
                String pubDay = "";
                if (pubDayNode != null) {
                    pubDay = pubDayNode.getNodeValue();
                }

                Element pubMonthElement = (Element)pubMonthList.item(0);
                Node pubMonthNode = pubMonthElement.getChildNodes().item(0);
                String pubMonth = "";
                if (pubMonthNode != null) {
                    pubMonth = pubMonthNode.getNodeValue();
                }

                Element pubYearElement = (Element)pubYearList.item(0);
                Node pubYearNode = pubYearElement.getChildNodes().item(0);
                String pubYear = "";
                if (pubMonthNode != null) {
                    pubYear = pubYearNode.getNodeValue();
                }

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
                NodeList avgRatingList = workElement.getElementsByTagName("average_rating");

                Element avgRatingElement = (Element)avgRatingList.item(0);
                Node avgRatingNode = avgRatingElement.getChildNodes().item(0);
                String avgRatingValue = "unrated";
                if (avgRatingNode != null) {
                    avgRatingValue = String.valueOf(avgRatingNode.getNodeValue());
                }

                searchResult.avgRating = avgRatingValue;


                //*** These are used for all of the remaining values
                NodeList bestBookList = workElement.getElementsByTagName("best_book");
                Element bestBook = (Element)bestBookList.item(0);
                ////*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***


                //      TITLE
                NodeList titleList = bestBook.getElementsByTagName("title");
                Element titleElement = (Element)titleList.item(0);
                Node titleNode = titleElement.getChildNodes().item(0);
                String titleValue = "no title";
                if (titleNode != null) {
                    titleValue = titleNode.getNodeValue();
                }

                searchResult.title = titleValue;


                //      AUTHOR
                NodeList nameList = bestBook.getElementsByTagName("name");
                Element nameElement = (Element)nameList.item(0);
                Node nameNode = nameElement.getChildNodes().item(0);
                String authorName = "unknown author";
                if (nameNode != null) {
                    authorName = nameNode.getNodeValue();
                }

                searchResult.author = authorName;

                //      URLS
                NodeList URLList = bestBook.getElementsByTagName("image_url");
                Element URLElement = (Element)URLList.item(0);
                Node URLNode = URLElement.getChildNodes().item(0);
                String url = "";
                if (URLNode != null) {
                    url = URLNode.getNodeValue();
                }

                searchResult.largeImageURL = url;


                NodeList smallURLList = bestBook.getElementsByTagName("small_image_url");
                Element smallURLElement = (Element)smallURLList.item(0);
                Node smallURLNode = smallURLElement.getChildNodes().item(0);
                url = "";
                if (smallURLNode != null) {
                    url = smallURLNode.getNodeValue();
                }

                searchResult.smallImageURL = url;


                //      ID
                NodeList IDList = bestBook.getElementsByTagName("id");
                Element IDElement = (Element)IDList.item(0);
                Node IDNode = IDElement.getChildNodes().item(0);
                String IDValue = "";
                if (IDNode != null) {
                    IDValue = IDNode.getNodeValue();
                }

                searchResult.goodReadsBeskBookID = IDValue;


                // BUILD RETURN LIST
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

    public String XMLGetTagValue(Element parentElement, String tagName) {
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