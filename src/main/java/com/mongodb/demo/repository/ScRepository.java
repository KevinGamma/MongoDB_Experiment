package com.mongodb.demo.repository;

import com.mongodb.demo.entity.ScRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScRepository extends MongoRepository<ScRecord, String> {
}
