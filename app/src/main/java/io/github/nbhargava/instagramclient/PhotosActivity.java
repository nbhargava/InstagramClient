package io.github.nbhargava.instagramclient;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotosActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "d5a78e265aac4a01a8dd1d561b5ccfd8";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });

        photos = new ArrayList<InstagramPhoto>();

        // Create the adapter linking it to the source
        aPhotos = new InstagramPhotosAdapter(this, photos);
        ListView lvPhotos = (ListView)findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);

        // Send out API requests to popular photos
        fetchPopularPhotos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchPopularPhotos() {
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                aPhotos.clear();

                Log.i("DEBUG", response.toString());

                JSONArray photosArray = null;
                try {
                    photosArray = response.getJSONArray("data");

                    for (int i = 0; i < photosArray.length(); i++) {
                        JSONObject photoJson = photosArray.getJSONObject(i);
                        InstagramPhoto photo = new InstagramPhoto();

                        photo.poster = userFromJson(photoJson.getJSONObject("user"));

                        JSONObject caption = photoJson.getJSONObject("caption");
                        if (caption != null) {
                            photo.caption = caption.getString("text");
                        } else {
                            photo.caption = "";
                        }
                        photo.createdTime = photoJson.getLong("created_time");
                        photo.imageUrl = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.imageHeight = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.imageHeight = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getInt("width");
                        photo.numLikes = photoJson.getJSONObject("likes").getInt("count");

                        int numComments = photoJson
                                .getJSONObject("comments")
                                .getInt("count");
                        photo.numComments = numComments;
                        if (photo.numComments <= 0) {
                            photo.lastComment = null;
                        } else {
                            JSONArray commentArray = photoJson
                                    .getJSONObject("comments")
                                    .getJSONArray("data");
                            int length = commentArray.length();
                            JSONObject commentObject = commentArray.getJSONObject(length - 1);
                            photo.lastComment = commentFromJson(commentObject);
                        }

                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    Log.i("DEBUG", e.toString());
                } finally {
                    aPhotos.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private InstagramComment commentFromJson(JSONObject obj) throws JSONException {
        InstagramComment comment = new InstagramComment();

        comment.commenter = userFromJson(obj.getJSONObject("from"));
        comment.createdTimestamp = obj.getLong("created_time");
        comment.commentText = obj.getString("text");

        return comment;
    }

    private InstagramUser userFromJson(JSONObject obj) throws JSONException {
        InstagramUser user = new InstagramUser();

        user.username = obj.getString("username");
        user.profilePicture = obj.getString("profile_picture");
        user.fullName = obj.getString("full_name");

        return user;
    }
}
