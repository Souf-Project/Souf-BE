package com.souf.soufwebsite.domain.feed.entity;

import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_category_maaping_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firstCategory_id")
    private FirstCategory firstCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private SecondCategory secondCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private ThirdCategory thirdCategory;

    public static FeedCategoryMapping of(Feed feed, FirstCategory firstCategory, SecondCategory secondCategory, ThirdCategory thirdCategory) {
        FeedCategoryMapping mapping = new FeedCategoryMapping();
        mapping.feed = feed;
        mapping.firstCategory = firstCategory;
        mapping.secondCategory = secondCategory;
        mapping.thirdCategory = thirdCategory;
        return mapping;
    }

    public void disconectFeed(){
        this.feed = null;
    }
}
