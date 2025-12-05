package com.mongodb.demo.controller;

import com.mongodb.demo.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * (1) 列出有学生选修的所有课程名称（distinct）
     */
    @GetMapping("/distinct-courses")
    public ResponseEntity<List<Map<String, Object>>> getDistinctSelectedCourses() {
        return ResponseEntity.ok(statisticsService.getDistinctSelectedCourses());
    }

    /**
     * (2) 找出平均成绩排名前10的学生
     */
    @GetMapping("/top10-students-by-avg-score")
    public ResponseEntity<List<Map<String, Object>>> getTop10StudentsByAvgScore() {
        return ResponseEntity.ok(statisticsService.getTop10StudentsByAvgScore());
    }

    /**
     * (3) 找出选课数目排名前10的学生
     */
    @GetMapping("/top10-students-by-course-count")
    public ResponseEntity<List<Map<String, Object>>> getTop10StudentsByCourseCount() {
        return ResponseEntity.ok(statisticsService.getTop10StudentsByCourseCount());
    }

    /**
     * (4) 找出每位同学的最高成绩以及最高成绩对应的课程名
     */
    @GetMapping("/student-max-score")
    public ResponseEntity<List<Map<String, Object>>> getStudentMaxScoreWithCourse() {
        return ResponseEntity.ok(statisticsService.getStudentMaxScoreWithCourse());
    }

    /**
     * (5) 求每位同学的成绩分布：优秀、良好、合格、不合格的课程门数
     */
    @GetMapping("/student-score-distribution")
    public ResponseEntity<List<Map<String, Object>>> getStudentScoreDistribution() {
        return ResponseEntity.ok(statisticsService.getStudentScoreDistribution());
    }

    /**
     * (6) 求每门课程的选修人数和平均成绩
     */
    @GetMapping("/course-student-count-avg")
    public ResponseEntity<List<Map<String, Object>>> getCourseStudentCountAndAvgScore() {
        return ResponseEntity.ok(statisticsService.getCourseStudentCountAndAvgScore());
    }

    /**
     * (7) 求每门课程最高成绩以及最高成绩对应的学生姓名
     */
    @GetMapping("/course-max-score")
    public ResponseEntity<List<Map<String, Object>>> getCourseMaxScoreWithStudent() {
        return ResponseEntity.ok(statisticsService.getCourseMaxScoreWithStudent());
    }

    /**
     * (8) 求平均成绩排名前10的课程
     */
    @GetMapping("/top10-courses-by-avg-score")
    public ResponseEntity<List<Map<String, Object>>> getTop10CoursesByAvgScore() {
        return ResponseEntity.ok(statisticsService.getTop10CoursesByAvgScore());
    }

    /**
     * (9) 求选课人数排名前10的课程
     */
    @GetMapping("/top10-courses-by-student-count")
    public ResponseEntity<List<Map<String, Object>>> getTop10CoursesByStudentCount() {
        return ResponseEntity.ok(statisticsService.getTop10CoursesByStudentCount());
    }
}

