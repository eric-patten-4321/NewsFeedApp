package com.example.android.paperboy3.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.paperboy3.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String TAG = "Utils";
    // private constructor cannot be instantiated. Utils only holds static methods/variables
    private Utils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(TAG, "Error creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // make sure response code is 200, get and read from stream. otherwise, continue
            // to return statement which still returns an empty string.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "HTTP error - response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving article JSON results", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Article} object by parsing out information
     * about the first earthquake from the input articleJson string.
     */
    private static List<Article> extractFeatureFromJson(String articleJson) {
        //handle potential empty or null string
        if (TextUtils.isEmpty(articleJson)) {
            return null;
        }

        List<Article> articles = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(articleJson);
            JSONObject baseArray = baseJsonResponse.getJSONObject("response");
            JSONArray articleArray = baseArray.getJSONArray("results");

            // create an Article object for each article in articleArray
            for (int i = 0; i < articleArray.length(); i++) {
                // Extract out the first feature
                JSONObject currentArticle = articleArray.getJSONObject(i);
                //JSONObject properties = currentArticle.getJSONObject("properties");

                // Extract out article data
                String sectionId = currentArticle.getString("sectionId");
                String sectionName = currentArticle.getString("sectionName");
                String date = currentArticle.getString("webPublicationDate");
                String title = currentArticle.getString("webTitle");
                String webUrl = currentArticle.getString("webUrl");

                //value of author is first 'contributor' in the key 'tags'
                // drill down and get it.
                String author = null;
                if(currentArticle.has("tags")) {
                    JSONArray contributorArray = currentArticle.getJSONArray("tags");
                    if(contributorArray.length() != 0) {
                        JSONObject firstContributor = contributorArray.getJSONObject(0);
                        author = firstContributor.getString("webTitle");
                    }
                }
                // String image is the url of a thumbnail returned as part of the query. it is then
                // passed to ArticleAdapter via constructor then loaded by Picasso

                String image = null;
                if(currentArticle.has("fields")) {
                    JSONObject fieldsObject = currentArticle.getJSONObject("fields");
                    if(fieldsObject.has("thumbnail")) {
                        image = fieldsObject.getString("thumbnail");
                    }
                }

                Log.d("author", "extractFeatureFromJson: AUTHOR: " + author);
                // pass all info via the constructor of each Article
                Article article = new Article(sectionId, sectionName, date, title, webUrl, author, image);
                articles.add(article);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the article JSON results", e);
        }
        return articles;
    }

    public static List<Article> fetchArticleData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Error making HTTP request: ", e);
        }
        return extractFeatureFromJson(jsonResponse);
    }
}


