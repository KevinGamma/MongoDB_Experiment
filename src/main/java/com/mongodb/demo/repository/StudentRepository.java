package com.mongodb.demo.repository;

import com.mongodb.demo.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    @Query("{ 'AGE' : { $lt : ?0 } }")
    List<Student> findByAgeLessThan(int age);

    @Query("{ 'AGE' : { $lt : ?0 }, 'DNAME' : ?1 }")
    List<Student> findByAgeLessThanAndDname(int age, String dname);
}
