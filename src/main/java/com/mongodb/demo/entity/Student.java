package com.mongodb.demo.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "students")
public class Student {

    @Id
    private String id;

    @Field("SID")
    private String sid;

    @Field("NAME")
    private String name;

    @Field("SEX")
    private String sex;

    @Field("AGE")
    private Integer age;

    @Field("BIRTHDAY")
    private String birthday;

    @Field("DNAME")
    @JsonProperty("dname")
    private String dname;

    @Field("CLASS")
    @JsonProperty("CLASS")
    @JsonAlias({"class", "clazz", "CLASS"})
    private String clazz;


    public Student() {
    }

    public Student(String sid, String name, String sex, Integer age, String birthday, String dname, String clazz) {
        this.sid = sid;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.birthday = birthday;
        this.dname = dname;
        this.clazz = clazz;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
