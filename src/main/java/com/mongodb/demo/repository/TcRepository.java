package com.mongodb.demo.repository;

import com.mongodb.demo.entity.TcRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TcRepository extends MongoRepository<TcRecord, String> {
}
