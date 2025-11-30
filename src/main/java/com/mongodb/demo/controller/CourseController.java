package com.mongodb.demo.controller;

import com.mongodb.demo.entity.Course;
import com.mongodb.demo.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.findAllCourses());
    }

    @GetMapping("/fcid/{fcid}")
    public ResponseEntity<List<Course>> getCoursesByFcid(@PathVariable Integer fcid) {
        return ResponseEntity.ok(courseService.findCoursesByFcid(fcid));
    }
}
