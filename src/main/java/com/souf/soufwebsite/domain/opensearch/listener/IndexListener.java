//package com.souf.soufwebsite.domain.opensearch.listener;
//
//import com.souf.soufwebsite.domain.feed.entity.Feed;
//import com.souf.soufwebsite.domain.member.entity.Member;
//import com.souf.soufwebsite.domain.opensearch.OperationType;
//import com.souf.soufwebsite.domain.opensearch.doc.FeedDoc;
//import com.souf.soufwebsite.domain.opensearch.doc.MemberDoc;
//import com.souf.soufwebsite.domain.opensearch.doc.RecruitDoc;
//import com.souf.soufwebsite.domain.opensearch.event.IndexEvent;
//import com.souf.soufwebsite.domain.opensearch.service.IndexingService;
//import com.souf.soufwebsite.domain.recruit.entity.Recruit;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class IndexListener {
//    private final IndexingService indexingService;
//
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handle(IndexEvent event) throws IOException {
//        switch (event.entityType()) {
//            case RECRUIT -> handleRecruit(event);
//            case FEED -> handleFeed(event);
//            case MEMBER -> handleMember(event);
//        }
//    }
//
//    private void handleRecruit(IndexEvent event) throws IOException {
//        if (event.operation() == OperationType.DELETE) {
//            Long id = (Long) event.payload();
//            indexingService.deleteDocument("recruit", id.toString());
//        } else {
//            Recruit recruit = (Recruit) event.payload();
//            indexingService.indexDocument("recruit", recruit.getId().toString(),
//                    new RecruitDoc(
//                    recruit.getId().toString(),
//                    recruit.getTitle(),
//                    recruit.getContent()
//            ));
//        }
//    }
//
//    private void handleFeed(IndexEvent event) throws IOException {
//        if (event.operation() == OperationType.DELETE) {
//            Long id = (Long) event.payload();
//            indexingService.deleteDocument("feed", id.toString());
//        } else {
//            Feed feed = (Feed) event.payload();
//            indexingService.indexDocument("feed", feed.getId().toString(),
//                    new FeedDoc(
//                    feed.getId().toString(),
//                    feed.getTopic(),
//                    feed.getContent()
//            ));
//        }
//    }
//
//    private void handleMember(IndexEvent event) throws IOException {
//        if (event.operation() == OperationType.DELETE) {
//            Long id = (Long) event.payload();
//            indexingService.deleteDocument("member", id.toString());
//        } else {
//            Member member = (Member) event.payload();
//            indexingService.indexDocument("member", member.getId().toString(),
//                    new MemberDoc(
//                    member.getId().toString(),
//                    member.getNickname()
//            ));
//        }
//    }
//}