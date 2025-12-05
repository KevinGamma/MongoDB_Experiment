package com.mongodb.demo.service;

import com.mongodb.demo.entity.Teacher;
import com.mongodb.demo.repository.TeacherRepository;
import com.mongodb.demo.util.ExcelUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final MongoTemplate mongoTemplate;

    public TeacherService(TeacherRepository teacherRepository, MongoTemplate mongoTemplate) {
        this.teacherRepository = teacherRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Teacher> findTeachersOlderThan(int age) {
        return teacherRepository.findByAgeGreaterThan(age);
    }

    public List<Teacher> findTeachersBySex(String sex) {
        return teacherRepository.findBySex(sex);
    }

    public List<Teacher> findTeachersByDepartment(String department) {
        return teacherRepository.findByDname(department);
    }

    public Teacher create(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher update(String id, Teacher payload) {
        Teacher existing = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应教师记录: " + id));
        applyTeacherUpdates(existing, payload);
        return teacherRepository.save(existing);
    }

    public List<Teacher> bulkUpdate(List<Teacher> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            return List.of();
        }
        List<String> ids = payloads.stream()
                .map(Teacher::getId)
                .filter(StringUtils::hasText)
                .toList();
        if (ids.size() != payloads.size()) {
            throw new IllegalArgumentException("批量更新需要为每条记录提供 id");
        }
        Map<String, Teacher> existing = teacherRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Teacher::getId, teacher -> teacher));
        if (existing.size() != ids.size()) {
            throw new IllegalArgumentException("部分教师记录不存在，无法批量更新");
        }
        payloads.forEach(payload -> {
            Teacher target = existing.get(payload.getId());
            applyTeacherUpdates(target, payload);
        });
        return teacherRepository.saveAll(existing.values());
    }

    public List<Teacher> search(Integer tid,
                                String name,
                                String sex,
                                Integer minAge,
                                Integer maxAge,
                                String dname) {
        Query query = new Query();
        java.util.List<Criteria> criteriaList = new java.util.ArrayList<>();
        if (tid != null) {
            criteriaList.add(Criteria.where("TID").is(tid));
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
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        return mongoTemplate.find(query, Teacher.class);
    }

    public List<Teacher> importTeachers(MultipartFile file) {
        List<Map<String, String>> rows = ExcelUtils.readSheet(file);
        List<Teacher> teachers = rows.stream()
                .map(this::toTeacher)
                .toList();
        return teacherRepository.saveAll(teachers);
    }

    private Teacher toTeacher(Map<String, String> row) {
        Map<String, String> normalized = normalize(row);
        Teacher teacher = new Teacher();
        teacher.setTid(parseInteger(normalized.get("TID")));
        teacher.setName(normalized.getOrDefault("NAME", ""));
        teacher.setSex(normalized.getOrDefault("SEX", ""));
        teacher.setAge(parseInteger(normalized.get("AGE")));
        teacher.setDname(normalized.getOrDefault("DNAME", ""));
        return teacher;
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

    private void applyTeacherUpdates(Teacher target, Teacher payload) {
        target.setTid(payload.getTid());
        target.setName(payload.getName());
        target.setSex(payload.getSex());
        target.setAge(payload.getAge());
        target.setDname(payload.getDname());
    }
}
