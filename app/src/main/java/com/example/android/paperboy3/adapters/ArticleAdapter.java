package com.example.android.paperboy3.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.paperboy3.Article;
import com.example.android.paperboy3.MainActivity;
import com.example.android.paperboy3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {
    public ArticleAdapter(MainActivity context, List<Article> articles) {
        super(context, 0, articles);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable  View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.alt_list, parent, false);
        }

        Article currentArticle = getItem(position);

        TextView tvSectionName = listItem.findViewById(R.id.tv_section_name);
        TextView tvAuthor = listItem.findViewById(R.id.tv_author_name);
        TextView tvPubDate = listItem.findViewById(R.id.tv_pub_date);
        TextView tvWebTitle = listItem.findViewById(R.id.tv_title);
        ImageView image = listItem.findViewById(R.id.iv_image);
        String thumbnail = null;
        if (currentArticle != null) {
            tvSectionName.setText(currentArticle.getSectionName());
            tvPubDate.setText(formatDate(currentArticle.getDate()));
            tvAuthor.setText(currentArticle.getAuthor());
            tvWebTitle.setText(currentArticle.getTitle());
            // get url for image from currentArticle
            thumbnail = currentArticle.getImage();
        }

        //load image using Picasso, placeholder no_image drawable
        Picasso.with(getContext()).load(thumbnail).placeholder(R.drawable.no_image).into(image);

        return listItem;
    }

    private String formatDate(String date) {
        String formattedDate = "";
        if(date.contains("T")){
            String[] parts = date.split("T");
            formattedDate = parts[0];
        }
        return formattedDate;
    }
}
