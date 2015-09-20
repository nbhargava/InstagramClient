package io.github.nbhargava.instagramclient;

/**
 * Created by nikhil on 9/15/15.
 */
public class InstagramPhoto {
    public InstagramUser poster;

    public String caption;
    public String imageUrl;
    public long createdTime;
    public int imageHeight;
    public int imageWidth;
    public int numLikes;

    public int numComments;
    public InstagramComment lastComment;
}
