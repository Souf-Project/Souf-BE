package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.entity.FeedTag;
import com.souf.soufwebsite.domain.feed.entity.Tag;
import com.souf.soufwebsite.domain.feed.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public void createFeedTag(Feed feed, List<String> tags){

        List<Tag> allTags = getTags(tags);

        for(Tag tag : allTags){
            FeedTag feedTag = FeedTag.of(feed, tag);
        }

    }

    private List<Tag> getTags(List<String> tags) {
        List<Tag> existingTags = tagRepository.findByNameIn(tags);

        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> allTags = new ArrayList<>(existingTags);

        for (String tagName : tags) {
            if (!existingTagNames.contains(tagName)) {
                Tag newTag = tagRepository.save(Tag.of(tagName));
                allTags.add(newTag);
            }
        }
        return allTags;
    }
}
