package com.mongodb.demo.repository;

import com.mongodb.demo.entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    @Query("{ 'FCID' : ?0 }")
    List<Course> findByFcid(Integer fcid);
}
