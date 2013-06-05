package com.paul.scratch

public class FeedMessage {

    String title;
    String description;
    String link;
    String author;
    String guid;
    String modified;
    List<Enclosure> enclosures = new ArrayList<Enclosure>();

    @Override
    public String toString() {
        return "FeedMessage [title=" + title + ", description=" + description + ", link=" + link + ", author=" + author + ", guid=" + guid + "]";
    }

}