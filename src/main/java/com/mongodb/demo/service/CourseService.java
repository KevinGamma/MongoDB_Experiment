package com.mongodb.demo.service;

import com.mongodb.demo.entity.Course;
import com.mongodb.demo.repository.CourseRepository;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final MongoTemplate mongoTemplate;

    public CourseService(CourseRepository courseRepository, MongoTemplate mongoTemplate) {
        this.courseRepository = courseRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> findCoursesByFcid(String fcid) {
        return courseRepository.findByFcid(fcid);
    }

    public Course create(Course course) {
        return courseRepository.save(course);
    }

    public Course update(String id, Course payload) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应课程记录: " + id));
        applyCourseUpdates(existing, payload);
        return courseRepository.save(existing);
    }

    public List<Course> bulkUpdate(List<Course> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            return List.of();
        }
        List<String> ids = payloads.stream()
                .map(Course::getId)
                .filter(StringUtils::hasText)
                .toList();
        if (ids.size() != payloads.size()) {
            throw new IllegalArgumentException("批量更新需要为每条记录提供 id");
        }
        Map<String, Course> existing = courseRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Course::getId, course -> course));
        if (existing.size() != ids.size()) {
            throw new IllegalArgumentException("部分课程记录不存在，无法批量更新");
        }
        payloads.forEach(payload -> {
            Course target = existing.get(payload.getId());
            applyCourseUpdates(target, payload);
        });
        return courseRepository.saveAll(existing.values());
    }

    public List<Course> search(String cid,
                               String name,
                               String fcid,
                               Integer minCredit,
                               Integer maxCredit) {
        Query query = new Query();
        java.util.List<Criteria> criteriaList = new java.util.ArrayList<>();
        if (StringUtils.hasText(cid)) {
            criteriaList.add(Criteria.where("CID").regex(".*" + java.util.regex.Pattern.quote(cid) + ".*", "i"));
        }
        if (StringUtils.hasText(name)) {
            criteriaList.add(Criteria.where("NAME").regex(".*" + java.util.regex.Pattern.quote(name) + ".*", "i"));
        }
        if (StringUtils.hasText(fcid)) {
            criteriaList.add(Criteria.where("FCID").is(fcid));
        }
        if (minCredit != null) {
            criteriaList.add(Criteria.where("CREDIT").gte(minCredit));
        }
        if (maxCredit != null) {
            criteriaList.add(Criteria.where("CREDIT").lte(maxCredit));
        }
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        return mongoTemplate.find(query, Course.class);
    }

    public List<Course> importCourses(MultipartFile file) {
        List<Map<String, String>> rows = ExcelUtils.readSheet(file);
        List<Course> courses = rows.stream()
                .map(this::toCourse)
                .toList();
        return courseRepository.saveAll(courses);
    }

    private Course toCourse(Map<String, String> row) {
        Map<String, String> normalized = normalize(row);
        Course course = new Course();
        course.setCid(normalized.getOrDefault("CID", ""));
        course.setName(normalized.getOrDefault("NAME", ""));
        course.setFcid(normalized.getOrDefault("FCID", ""));
        course.setCredit(parseInteger(normalized.get("CREDIT")));
        return course;
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

    private void applyCourseUpdates(Course target, Course payload) {
        target.setCid(payload.getCid());
        target.setName(payload.getName());
        target.setFcid(payload.getFcid());
        target.setCredit(payload.getCredit());
    }
}
