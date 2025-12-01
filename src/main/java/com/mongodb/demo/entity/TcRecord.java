package com.mongodb.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "tc")
public class TcRecord {

    @Id
    private String id;

    @Field("CID")
    private Integer cid;

    @Field("TID")
    private String tid;

    public TcRecord() {
    }

    public TcRecord(Integer cid, String tid) {
        this.cid = cid;
        this.tid = tid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
