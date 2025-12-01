package com.mongodb.demo.service;

import com.mongodb.demo.entity.Student;
import com.mongodb.demo.repository.StudentRepository;
import com.mongodb.demo.util.ExcelUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Locale;
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

    public Student create(Student student) {
        return studentRepository.save(student);
    }

    public List<Map<String, Object>> findNameAndAgeForAll() {
        Query query = new Query();
        query.fields().include("NAME").include("AGE").exclude("_id");
        List<Document> docs = mongoTemplate.find(query, Document.class, "students");
        return docs.stream()
                .map(doc -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("name", doc.get("NAME") != null ? doc.get("NAME") : "");
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

    public List<Student> importStudents(MultipartFile file) {
        List<Map<String, String>> rows = ExcelUtils.readSheet(file);
        List<Student> students = rows.stream()
                .map(this::toStudent)
                .toList();
        return studentRepository.saveAll(students);
    }

    private Student toStudent(Map<String, String> row) {
        Map<String, String> normalized = normalize(row);
        Student student = new Student();
        student.setSid(normalized.getOrDefault("SID", ""));
        student.setName(normalized.getOrDefault("NAME", ""));
        student.setSex(normalized.getOrDefault("SEX", ""));
        student.setAge(parseInteger(normalized.get("AGE")));
        student.setBirthday(normalized.getOrDefault("BIRTHDAY", ""));
        student.setDname(normalized.getOrDefault("DNAME", ""));
        student.setClazz(normalized.getOrDefault("CLASS", ""));
        return student;
    }

    private Map<String, String> normalize(Map<String, String> source) {
        return source.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toUpperCase(Locale.ROOT),
                        Map.Entry::getValue,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                ));
    }

    private Integer parseInteger(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无法解析数字: " + value);
        }
    }
}
