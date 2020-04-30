package com.example.android.paperboy3;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.Nullable;
import com.example.android.paperboy3.utilities.Utils;
import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {
    private String mUrl;

    /**
     * constructs a new {@link ArticleLoader}
     * @param context of activity
     * @param url     to load
     */
    ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Nullable
    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        return Utils.fetchArticleData(mUrl);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}

