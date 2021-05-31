package com.cyansecurity.vlasak.rsstopicanalyzer.beans;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Feed {

    private String title;
    private String link;

    public Feed() {
    }

    public Feed(@Nonnull final String title, @Nonnull final String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Feed feed = (Feed) obj;
        return title.equals(feed.title) && link.equals(feed.link);
    }

    @Override
    public String toString() {
        return new StringBuilder().append("title: ")
                .append(title)
                .append("link: ")
                .append(link)
                .toString();
    }
}