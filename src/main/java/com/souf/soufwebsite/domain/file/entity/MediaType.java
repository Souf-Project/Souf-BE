package com.souf.soufwebsite.domain.file.entity;

import java.util.EnumSet;

public enum MediaType {

    JPG, JPEG, PNG, GIF, // image
    MP4, MOV, AVI, MKV, WEBM, FLV, QUICKTIME, // video
    WEBP, PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, TXT, HWP, ZIP; // etc

    public boolean isImage() { return EnumSet.of(JPG,JPEG,PNG,GIF,WEBP).contains(this); }
    public boolean isVideo() { return EnumSet.of(MP4,MOV,AVI,MKV,WEBM,FLV,QUICKTIME).contains(this); }

    public boolean needsThumbnail() {
        return isVideo();
    }

    public boolean isDocumentOrEtc() {
        return !(isImage() || isVideo());
    }
}
