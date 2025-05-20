package com.souf.soufwebsite.domain.feed.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public FeedTag(Feed feed, Tag tag){
        this.feed = feed;
        this.tag = tag;
    }

    public static FeedTag of(Feed feed, Tag tag){
        return new FeedTag(feed, tag);
    }
}
