package com.mongodb.demo.service;

import com.mongodb.demo.entity.Teacher;
import com.mongodb.demo.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
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
}
