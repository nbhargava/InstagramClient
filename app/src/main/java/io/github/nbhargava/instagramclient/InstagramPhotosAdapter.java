package io.github.nbhargava.instagramclient;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nikhil on 9/15/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    private static class InstagramPhotoViewHolder {
        ImageView photo;
        TextView likes;
        TextView caption;
    }

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        InstagramPhotoViewHolder viewHolder;
        if (convertView == null) {
            // View not recycled
            viewHolder = new InstagramPhotoViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_photo, parent, false);

            viewHolder.photo = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.caption = (TextView) convertView.findViewById(R.id.tvCaption);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InstagramPhotoViewHolder) convertView.getTag();
        }

        String likesText = getContext().getResources().getQuantityString(R.plurals.like_count, photo.numLikes, photo.numLikes);
        viewHolder.likes.setText(likesText);

        String tvHtml = "<b>" + photo.username + "</b> - " + photo.caption;
        viewHolder.caption.setText(Html.fromHtml(tvHtml));

        // Clear photo until Picasso loads it in
        ImageView ivPhoto = viewHolder.photo;
        ivPhoto.setImageResource(0);
        Picasso.with(getContext())
                .load(photo.imageUrl)
                .placeholder(R.drawable.placeholder)
                .resize(photo.imageWidth, photo.imageHeight)
                .into(ivPhoto);

        return convertView;
    }
}
