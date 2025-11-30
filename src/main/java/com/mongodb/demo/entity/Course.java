package com.mongodb.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "courses")
public class Course {

    @Id
    private String id;

    @Field("CID")
    private String cid;

    @Field("NAME")
    private String name;

    @Field("FCID")
    private Integer fcid;

    @Field("CREDIT")
    private Integer credit;

    public Course() {
    }

    public Course(String cid, String name, Integer fcid, Integer credit) {
        this.cid = cid;
        this.name = name;
        this.fcid = fcid;
        this.credit = credit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFcid() {
        return fcid;
    }

    public void setFcid(Integer fcid) {
        this.fcid = fcid;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }
}
