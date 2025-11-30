package com.mongodb.demo.service;

import com.mongodb.demo.entity.Course;
import com.mongodb.demo.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> findCoursesByFcid(Integer fcid) {
        return courseRepository.findByFcid(fcid);
    }
}
