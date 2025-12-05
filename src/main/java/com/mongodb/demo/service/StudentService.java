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

    public Student update(String id, Student payload) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应学生记录: " + id));
        applyStudentUpdates(existing, payload);
        return studentRepository.save(existing);
    }

    public List<Student> bulkUpdate(List<Student> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            return List.of();
        }
        List<String> ids = payloads.stream()
                .map(Student::getId)
                .filter(StringUtils::hasText)
                .toList();
        if (ids.size() != payloads.size()) {
            throw new IllegalArgumentException("批量更新需要为每条记录提供 id");
        }
        Map<String, Student> existingMap = studentRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Student::getId, student -> student));
        if (existingMap.size() != ids.size()) {
            throw new IllegalArgumentException("部分学生记录不存在，无法批量更新");
        }
        payloads.forEach(payload -> {
            Student target = existingMap.get(payload.getId());
            applyStudentUpdates(target, payload);
        });
        return studentRepository.saveAll(existingMap.values());
    }

    public List<Student> search(Long sid,
                                String name,
                                String sex,
                                Integer minAge,
                                Integer maxAge,
                                String dname,
                                Integer clazz) {
        Query query = new Query();
        List<Criteria> criteriaList = new java.util.ArrayList<>();
        if (sid != null) {
            criteriaList.add(Criteria.where("SID").is(sid));
        }
        if (StringUtils.hasText(name)) {
            criteriaList.add(Criteria.where("NAME").regex(".*" + java.util.regex.Pattern.quote(name) + ".*", "i"));
        }
        if (StringUtils.hasText(sex)) {
            criteriaList.add(Criteria.where("SEX").is(sex));
        }
        if (minAge != null) {
            criteriaList.add(Criteria.where("AGE").gte(minAge));
        }
        if (maxAge != null) {
            criteriaList.add(Criteria.where("AGE").lte(maxAge));
        }
        if (StringUtils.hasText(dname)) {
            criteriaList.add(Criteria.where("DNAME").regex(".*" + java.util.regex.Pattern.quote(dname) + ".*", "i"));
        }
        if (clazz != null) {
            criteriaList.add(Criteria.where("CLASS").is(clazz));
        }
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        return mongoTemplate.find(query, Student.class);
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
        student.setSid(parseLong(normalized.get("SID")));
        student.setName(normalized.getOrDefault("NAME", ""));
        student.setSex(normalized.getOrDefault("SEX", ""));
        student.setAge(parseInteger(normalized.get("AGE")));
        student.setBirthday(normalized.getOrDefault("BIRTHDAY", ""));
        student.setDname(normalized.getOrDefault("DNAME", ""));
        student.setClazz(parseInteger(normalized.get("CLASS")));
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

    private Long parseLong(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无法解析长整型数字: " + value);
        }
    }

    private void applyStudentUpdates(Student target, Student payload) {
        target.setSid(payload.getSid());
        target.setName(payload.getName());
        target.setSex(payload.getSex());
        target.setAge(payload.getAge());
        target.setBirthday(payload.getBirthday());
        target.setDname(payload.getDname());
        target.setClazz(payload.getClazz());
    }
}
