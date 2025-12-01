package com.mongodb.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "sc")
public class ScRecord {

    @Id
    private String id;

    @Field("SID")
    private String sid;

    @Field("CID")
    private Integer cid;

    @Field("SCORE")
    private Integer score;

    @Field("TID")
    private String tid;

    public ScRecord() {
    }

    public ScRecord(String sid, Integer cid, Integer score, String tid) {
        this.sid = sid;
        this.cid = cid;
        this.score = score;
        this.tid = tid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getScore() {
        return score != null ? score : 0;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
