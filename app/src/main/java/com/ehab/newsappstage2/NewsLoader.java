package com.ehab.newsappstage2;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.ehab.newsappstage2.model.Results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ehabhamdy on 3/24/18.
 */

public class NewsLoader extends AsyncTaskLoader<List<Results>> {

    List<Results> news;
    String mainUri;

    public NewsLoader(Context context, String uri) {
        super(context);
        this.mainUri = uri;
    }

    @Override
    public List<Results> loadInBackground() {

        URL url = null;
        try {
            url = new URL(mainUri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String jsonRecipesResponse = null;
        try {
            jsonRecipesResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            news = getNewsFromJson(jsonRecipesResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return news;
    }

    @Override
    protected void onStartLoading() {
        if (news != null) {
            deliverResult(news);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Results> data) {
        super.deliverResult(data);
    }

    private String getResponseFromHttpUrl(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private List<Results> getNewsFromJson(String jsonNewsResponse) throws JSONException {
        List<Results> parsedNewsData = new ArrayList<>();

        JSONObject jsonObj = new JSONObject(jsonNewsResponse);
        JSONObject responseObj = jsonObj.getJSONObject("response");
        JSONArray resultsJSONArray = responseObj.getJSONArray("results");
        for (int i = 0; i < resultsJSONArray.length(); i++) {
            JSONObject newsItem = resultsJSONArray.getJSONObject(i);
            Results result = new Results();
            result.setId(newsItem.getString("id"));
            result.setType(newsItem.getString("type"));
            result.setSectionId(newsItem.getString("sectionId"));
            result.setSectionName(newsItem.getString("sectionName"));
            result.setWebPublicationDate(newsItem.getString("webPublicationDate"));
            result.setWebTitle(newsItem.getString("webTitle"));
            result.setWebUrl(newsItem.getString("webUrl"));
            result.setApiUrl(newsItem.getString("apiUrl"));
            result.setIsHosted(newsItem.getString("isHosted"));


            parsedNewsData.add(result);
        }

        return parsedNewsData;
    }
}
