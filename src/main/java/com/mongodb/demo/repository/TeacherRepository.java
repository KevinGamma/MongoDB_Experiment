package com.mongodb.demo.repository;

import com.mongodb.demo.entity.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends MongoRepository<Teacher, String> {

    @Query("{ 'AGE' : { $gt : ?0 } }")
    List<Teacher> findByAgeGreaterThan(int age);

    @Query("{ 'SEX' : ?0 }")
    List<Teacher> findBySex(String sex);

    @Query("{ 'DNAME' : ?0 }")
    List<Teacher> findByDname(String dname);
}
