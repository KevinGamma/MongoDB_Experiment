package com.mongodb.demo.repository;

import com.mongodb.demo.entity.ScRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScRepository extends MongoRepository<ScRecord, String> {
    List<ScRecord> findBySid(String sid);

    List<ScRecord> findBySidAndCid(String sid, Integer cid);

    void deleteBySidAndCid(String sid, Integer cid);
}
