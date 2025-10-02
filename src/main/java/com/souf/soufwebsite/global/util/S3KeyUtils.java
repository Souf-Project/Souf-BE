package com.souf.soufwebsite.global.util;

import java.net.URI;

public final class S3KeyUtils {
    private S3KeyUtils() {}

    public static String extractKeyFromUrl (String urlOrKey, String bucket) {
        if (urlOrKey == null || urlOrKey.isBlank()) return null;

        if (!urlOrKey.startsWith("http://") && !urlOrKey.startsWith("https://")) {
            return stripLeadingSlash(urlOrKey);
        }

        try {
            URI u = new URI(urlOrKey);
            String host = u.getHost();
            String path = u.getPath();
            if (host == null || path == null) return null;

            // 가상호스트 스타일: <bucket>.s3.<region>.amazonaws.com/<key>
            if (host.startsWith(bucket + ".")) {
                return stripLeadingSlash(path);
            }

            // 경로 스타일: s3.<region>.amazonaws.com/<bucket>/<key>
            if (host.contains("amazonaws.com")) {
                String p = stripLeadingSlash(path);
                if (p.startsWith(bucket + "/")) {
                    return p.substring(bucket.length() + 1);
                }
                return p; // 혹시 버킷 생략된 커스텀 라우팅인 경우
            }

            // CloudFront / 커스텀 도메인: path 전체를 key로 사용
            return stripLeadingSlash(path);
        } catch (Exception e) {
            return null;
        }
    }

    private static String stripLeadingSlash(String s) {
        return (s != null && s.startsWith("/")) ? s.substring(1) : s;
    }
}