package com.mongodb.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "teachers")
public class Teacher {

    @Id
    private String id;

    @Field("TID")
    private String tid;

    @Field("NAME")
    private String name;

    @Field("SEX")
    private String sex;

    @Field("AGE")
    private Integer age;

    @Field("DNAME")
    @JsonProperty("dname")
    private String dname;

    public Teacher() {
    }

    public Teacher(String tid, String name, String sex, Integer age, String dname) {
        this.tid = tid;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.dname = dname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age != null ? age : 0;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }
}
