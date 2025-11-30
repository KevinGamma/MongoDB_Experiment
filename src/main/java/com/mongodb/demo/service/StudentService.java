package com.mongodb.demo.service;

import com.mongodb.demo.entity.Student;
import com.mongodb.demo.repository.StudentRepository;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final MongoTemplate mongoTemplate;

    public StudentService(StudentRepository studentRepository, MongoTemplate mongoTemplate) {
        this.studentRepository = studentRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> findStudentsYoungerThan(int age) {
        return studentRepository.findByAgeLessThan(age);
    }

    public List<Student> findStudentsYoungerThanInDepartment(int age, String department) {
        return studentRepository.findByAgeLessThanAndDname(age, department);
    }

    public List<Map<String, Object>> findNameAndAgeForAll() {
        Query query = new Query();
        query.fields().include("NAME").include("AGE").exclude("_id");
        List<Document> docs = mongoTemplate.find(query, Document.class, "students");
        return docs.stream()
                .map(doc -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("name", doc.get("NAME") != null ? doc.get("NAME") : "");
                    // Ensure age is never null - return 0 or the actual value
                    Object ageValue = doc.get("AGE");
                    map.put("age", ageValue != null ? ageValue : 0);
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> findNameAndSexYoungerThan(int age) {
        Query query = new Query(Criteria.where("AGE").lt(age));
        query.fields().include("NAME").include("SEX").exclude("_id");
        List<Document> docs = mongoTemplate.find(query, Document.class, "students");
        return docs.stream()
                .map(doc -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("name", doc.get("NAME") != null ? doc.get("NAME") : "");
                    map.put("sex", doc.get("SEX") != null ? doc.get("SEX") : "");
                    return map;
                })
                .collect(Collectors.toList());
    }
}
