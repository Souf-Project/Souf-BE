package com.souf.soufwebsite.domain.opensearch.service;
//
//import com.souf.soufwebsite.domain.feed.entity.Feed;
//import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
//import com.souf.soufwebsite.domain.member.entity.Member;
//import com.souf.soufwebsite.domain.member.repository.MemberRepository;
//import com.souf.soufwebsite.domain.opensearch.doc.FeedDoc;
//import com.souf.soufwebsite.domain.opensearch.doc.MemberDoc;
//import com.souf.soufwebsite.domain.opensearch.doc.RecruitDoc;
//import com.souf.soufwebsite.domain.recruit.entity.Recruit;
//import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.opensearch.client.opensearch.OpenSearchClient;
//import org.opensearch.client.opensearch.core.bulk.BulkOperation;
//import org.opensearch.client.opensearch.core.bulk.IndexOperation;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class InitialIndexingService {
//
//    private final RecruitRepository recruitRepository;
//    private final FeedRepository feedRepository;
//    private final MemberRepository memberRepository;
//    private final OpenSearchClient client;
//
//    @Transactional(readOnly = true)
//    public void bulkIndexAll() throws IOException {
//        // ✅ Recruit bulk
//        List<Recruit> recruits = recruitRepository.findAll();
//        log.info("리크루트 수: {}", recruits.size());
//        bulkIndex("recruit", recruits.stream().map(r -> new RecruitDoc(
//                r.getId().toString(),
//                r.getTitle(),
//                r.getContent()
//        )).collect(Collectors.toList()));
//
//        // ✅ Feed bulk
//        List<Feed> feeds = feedRepository.findAll();
//        log.info("피드 수: {}", feeds.size());
//        bulkIndex("feed", feeds.stream().map(f -> new FeedDoc(
//                f.getId().toString(),
//                f.getTopic(),
//                f.getContent()
//        )).collect(Collectors.toList()));
//
//        // ✅ Member bulk
//        List<Member> members = memberRepository.findAll();
//        log.info("멤버 수: {}", members.size());
//        bulkIndex("member", members.stream().map(m -> new MemberDoc(
//                m.getId().toString(),
//                m.getNickname()
//        )).collect(Collectors.toList()));
//
//        log.info("✅ 모든 데이터 bulk 색인 완료");
//    }
//
//    private <T> void bulkIndex(String indexName, List<T> docs) throws IOException {
//        if (docs.isEmpty()) return;
//
//        List<BulkOperation> operations = new ArrayList<>();
//        for (T doc : docs) {
//            operations.add(BulkOperation.of(b -> b
//                    .index(IndexOperation.of(i -> i
//                            .index(indexName)
//                            .id(extractId(doc)) // ID 추출
//                            .document(doc)
//                    ))
//            ));
//        }
//
//        client.bulk(b -> b.operations(operations));
//        log.info("✅ {} 인덱스에 {}건 bulk 색인 완료", indexName, docs.size());
//    }
//
//    // 문서 ID 추출을 위한 헬퍼
//    private <T> String extractId(T doc) {
//        try {
//            // 각 Doc(record)에 id() 메서드가 있다고 가정
//            return (String) doc.getClass().getMethod("getId").invoke(doc);
//        } catch (Exception e) {
//            throw new RuntimeException("문서 ID 추출 실패: " + doc, e);
//        }
//    }
//}

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.opensearch.doc.FeedDoc;
import com.souf.soufwebsite.domain.opensearch.doc.MemberDoc;
import com.souf.soufwebsite.domain.opensearch.doc.RecruitDoc;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitialIndexingService {

    private final RecruitRepository recruitRepository;
    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate openSearchRestTemplate;

    @Value("${opensearch.host}")
    private String openSearchHost;

    @Transactional(readOnly = true)
    public void bulkIndexAll() {
        indexRecruits();
        indexFeeds();
        indexMembers();
        log.info("✅ 모든 데이터 인덱싱 완료");
    }

    private void indexRecruits() {
        List<Recruit> recruits = recruitRepository.findAll();
        log.info("리크루트 수: {}", recruits.size());

        for (Recruit r : recruits) {
            RecruitDoc doc = new RecruitDoc(
                    r.getId().toString(),
                    r.getTitle(),
                    r.getContent()
            );
            index("recruit", doc.getId(), doc);
        }
    }

    private void indexFeeds() {
        List<Feed> feeds = feedRepository.findAll();
        log.info("피드 수: {}", feeds.size());

        for (Feed f : feeds) {
            FeedDoc doc = new FeedDoc(
                    f.getId().toString(),
                    f.getTopic(),
                    f.getContent()
            );
            index("feed", doc.getId(), doc);
        }
    }

    private void indexMembers() {
        List<Member> members = memberRepository.findAll();
        log.info("멤버 수: {}", members.size());

        for (Member m : members) {
            MemberDoc doc = new MemberDoc(
                    m.getId().toString(),
                    m.getNickname()
            );
            index("member", doc.getId(), doc);
        }
    }

    private <T> void index(String indexName, String id, T document) {
        String url = openSearchHost + "/" + indexName + "/_doc/" + id;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<T> request = new HttpEntity<>(document, headers);

            openSearchRestTemplate.put(url, request);
            log.info("✅ 인덱싱 성공: {}/{}", indexName, id);
        } catch (Exception e) {
            log.warn("⚠️ 인덱싱 실패: {}/{}", indexName, id, e);
        }
    }
}