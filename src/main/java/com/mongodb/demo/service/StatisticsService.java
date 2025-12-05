package com.mongodb.demo.service;

import com.mongodb.demo.entity.Course;
import com.mongodb.demo.entity.ScRecord;
import com.mongodb.demo.entity.Student;
import com.mongodb.demo.repository.CourseRepository;
import com.mongodb.demo.repository.ScRepository;
import com.mongodb.demo.repository.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final MongoTemplate mongoTemplate;
    private final ScRepository scRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public StatisticsService(MongoTemplate mongoTemplate, ScRepository scRepository,
                             CourseRepository courseRepository, StudentRepository studentRepository) {
        this.mongoTemplate = mongoTemplate;
        this.scRepository = scRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * (1) 列出有学生选修的所有课程名称（distinct）
     */
    public List<Map<String, Object>> getDistinctSelectedCourses() {
        // 获取所有选课记录中的课程ID
        List<ScRecord> allRecords = scRepository.findAll();
        Set<Integer> selectedCids = allRecords.stream()
                .map(ScRecord::getCid)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 获取所有课程
        List<Course> allCourses = courseRepository.findAll();

        // 过滤出有学生选修的课程名称
        return allCourses.stream()
                .filter(c -> {
                    try {
                        return selectedCids.contains(Integer.parseInt(c.getCid()));
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .map(c -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("cid", c.getCid());
                    map.put("courseName", c.getName());
                    return map;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * (2) 找出平均成绩排名前10的学生
     */
    public List<Map<String, Object>> getTop10StudentsByAvgScore() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("SID")
                        .avg("SCORE").as("avgScore")
                        .count().as("courseCount"),
                Aggregation.sort(Sort.Direction.DESC, "avgScore"),
                Aggregation.limit(10)
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "sc", Map.class);
        List<Map> rawResults = results.getMappedResults();

        // 获取学生信息
        Map<String, Student> studentMap = getStudentMap();

        return rawResults.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            String sid = String.valueOf(r.get("_id"));
            map.put("sid", sid);
            Student student = studentMap.get(sid);
            map.put("studentName", student != null ? student.getName() : "未知");
            map.put("avgScore", formatDouble(r.get("avgScore")));
            map.put("courseCount", r.get("courseCount"));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * (3) 找出选课数目排名前10的学生
     */
    public List<Map<String, Object>> getTop10StudentsByCourseCount() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("SID").count().as("courseCount"),
                Aggregation.sort(Sort.Direction.DESC, "courseCount"),
                Aggregation.limit(10)
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "sc", Map.class);
        List<Map> rawResults = results.getMappedResults();

        Map<String, Student> studentMap = getStudentMap();

        return rawResults.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            String sid = String.valueOf(r.get("_id"));
            map.put("sid", sid);
            Student student = studentMap.get(sid);
            map.put("studentName", student != null ? student.getName() : "未知");
            map.put("courseCount", r.get("courseCount"));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * (4) 找出每位同学的最高成绩以及最高成绩对应的课程名
     */
    public List<Map<String, Object>> getStudentMaxScoreWithCourse() {
        List<ScRecord> allRecords = scRepository.findAll();
        Map<String, Student> studentMap = getStudentMap();
        Map<String, Course> courseMap = getCourseMap();

        // 按学生分组，找出最高成绩（过滤掉sid为null的记录）
        Map<String, List<ScRecord>> studentRecords = allRecords.stream()
                .filter(r -> r.getSid() != null)
                .collect(Collectors.groupingBy(ScRecord::getSid));

        return studentRecords.entrySet().stream().map(entry -> {
            String sid = entry.getKey();
            List<ScRecord> records = entry.getValue();

            ScRecord maxRecord = records.stream()
                    .max(Comparator.comparingInt(r -> r.getScore() != null ? r.getScore() : 0))
                    .orElse(null);

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("sid", sid);
            Student student = studentMap.get(sid);
            map.put("studentName", student != null ? student.getName() : "未知");
            if (maxRecord != null) {
                map.put("maxScore", maxRecord.getScore());
                Course course = courseMap.get(String.valueOf(maxRecord.getCid()));
                map.put("courseName", course != null ? course.getName() : "未知课程");
            } else {
                map.put("maxScore", 0);
                map.put("courseName", "-");
            }
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * (5) 求每位同学的成绩分布：优秀、良好、合格、不合格的课程门数
     * 优秀: >=90, 良好: 80-89, 合格: 60-79, 不合格: <60
     */
    public List<Map<String, Object>> getStudentScoreDistribution() {
        List<ScRecord> allRecords = scRepository.findAll();
        Map<String, Student> studentMap = getStudentMap();

        Map<String, List<ScRecord>> studentRecords = allRecords.stream()
                .filter(r -> r.getSid() != null)
                .collect(Collectors.groupingBy(ScRecord::getSid));

        return studentRecords.entrySet().stream().map(entry -> {
            String sid = entry.getKey();
            List<ScRecord> records = entry.getValue();

            long excellent = records.stream().filter(r -> r.getScore() != null && r.getScore() >= 90).count();
            long good = records.stream().filter(r -> r.getScore() != null && r.getScore() >= 80 && r.getScore() < 90).count();
            long pass = records.stream().filter(r -> r.getScore() != null && r.getScore() >= 60 && r.getScore() < 80).count();
            long fail = records.stream().filter(r -> r.getScore() != null && r.getScore() < 60).count();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("sid", sid);
            Student student = studentMap.get(sid);
            map.put("studentName", student != null ? student.getName() : "未知");
            map.put("excellent", excellent);
            map.put("good", good);
            map.put("pass", pass);
            map.put("fail", fail);
            map.put("total", records.size());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * (6) 求每门课程的选修人数和平均成绩
     */
    public List<Map<String, Object>> getCourseStudentCountAndAvgScore() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("CID")
                        .count().as("studentCount")
                        .avg("SCORE").as("avgScore")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "sc", Map.class);
        List<Map> rawResults = results.getMappedResults();

        Map<String, Course> courseMap = getCourseMap();

        return rawResults.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            String cid = String.valueOf(r.get("_id"));
            map.put("cid", cid);
            Course course = courseMap.get(cid);
            map.put("courseName", course != null ? course.getName() : "未知课程");
            map.put("studentCount", r.get("studentCount"));
            map.put("avgScore", formatDouble(r.get("avgScore")));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * (7) 求每门课程最高成绩以及最高成绩对应的学生姓名
     */
    public List<Map<String, Object>> getCourseMaxScoreWithStudent() {
        List<ScRecord> allRecords = scRepository.findAll();
        Map<String, Student> studentMap = getStudentMap();
        Map<String, Course> courseMap = getCourseMap();

        // 按课程分组
        Map<Integer, List<ScRecord>> courseRecords = allRecords.stream()
                .filter(r -> r.getCid() != null)
                .collect(Collectors.groupingBy(ScRecord::getCid));

        return courseRecords.entrySet().stream().map(entry -> {
            Integer cid = entry.getKey();
            List<ScRecord> records = entry.getValue();

            ScRecord maxRecord = records.stream()
                    .max(Comparator.comparingInt(r -> r.getScore() != null ? r.getScore() : 0))
                    .orElse(null);

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("cid", cid);
            Course course = courseMap.get(String.valueOf(cid));
            map.put("courseName", course != null ? course.getName() : "未知课程");
            if (maxRecord != null) {
                map.put("maxScore", maxRecord.getScore());
                Student student = studentMap.get(maxRecord.getSid());
                map.put("studentName", student != null ? student.getName() : "未知");
                map.put("sid", maxRecord.getSid());
            } else {
                map.put("maxScore", 0);
                map.put("studentName", "-");
                map.put("sid", "-");
            }
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * (8) 求平均成绩排名前10的课程
     */
    public List<Map<String, Object>> getTop10CoursesByAvgScore() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("CID")
                        .avg("SCORE").as("avgScore")
                        .count().as("studentCount"),
                Aggregation.sort(Sort.Direction.DESC, "avgScore"),
                Aggregation.limit(10)
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "sc", Map.class);
        List<Map> rawResults = results.getMappedResults();

        Map<String, Course> courseMap = getCourseMap();

        return rawResults.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            String cid = String.valueOf(r.get("_id"));
            map.put("cid", cid);
            Course course = courseMap.get(cid);
            map.put("courseName", course != null ? course.getName() : "未知课程");
            map.put("avgScore", formatDouble(r.get("avgScore")));
            map.put("studentCount", r.get("studentCount"));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * (9) 求选课人数排名前10的课程
     */
    public List<Map<String, Object>> getTop10CoursesByStudentCount() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("CID").count().as("studentCount"),
                Aggregation.sort(Sort.Direction.DESC, "studentCount"),
                Aggregation.limit(10)
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "sc", Map.class);
        List<Map> rawResults = results.getMappedResults();

        Map<String, Course> courseMap = getCourseMap();

        return rawResults.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            String cid = String.valueOf(r.get("_id"));
            map.put("cid", cid);
            Course course = courseMap.get(cid);
            map.put("courseName", course != null ? course.getName() : "未知课程");
            map.put("studentCount", r.get("studentCount"));
            return map;
        }).collect(Collectors.toList());
    }

    // Helper methods
    private Map<String, Student> getStudentMap() {
        return studentRepository.findAll().stream()
                .collect(Collectors.toMap(
                        s -> String.valueOf(s.getSid()),
                        s -> s,
                        (a, b) -> a
                ));
    }

    private Map<String, Course> getCourseMap() {
        return courseRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Course::getCid,
                        c -> c,
                        (a, b) -> a
                ));
    }

    private String formatDouble(Object value) {
        if (value == null) return "0.00";
        if (value instanceof Double) {
            return String.format("%.2f", (Double) value);
        }
        return String.valueOf(value);
    }
}

