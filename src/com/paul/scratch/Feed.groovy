package com.paul.scratch;

public class Feed {

    String title
    String link
    String description
    String language
    String copyright
    String pubDate
    String image

    final List<FeedMessage> entries = new ArrayList<FeedMessage>();

    public Feed() {
    }

    public List<FeedMessage> getMessages() {
        return entries;
    }

    @Override
    public String toString() {
        return "Feed [copyright=" + copyright + ", description=" + description + ", language=" + language + ", link=" + link + ", pubDate=" + pubDate + ", title=" + title + "]";
    }

}
