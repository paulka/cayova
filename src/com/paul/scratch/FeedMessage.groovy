package com.paul.scratch;

import java.sql.Timestamp;

public class FeedMessage {

    String title;
    String description;
    String link;
    String author;
    String guid;
    String modified;

    @Override
    public String toString() {
        return "FeedMessage [title=" + title + ", description=" + description
        + ", link=" + link + ", author=" + author + ", guid=" + guid
        + "]";
    }

}