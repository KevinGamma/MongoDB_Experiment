package com.mongodb.demo.controller;

import com.mongodb.demo.entity.Teacher;
import com.mongodb.demo.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/older-than/{age}")
    public ResponseEntity<List<Teacher>> getTeachersOlderThan(@PathVariable int age) {
        return ResponseEntity.ok(teacherService.findTeachersOlderThan(age));
    }

    @GetMapping("/sex/{sex}")
    public ResponseEntity<List<Teacher>> getTeachersBySex(@PathVariable String sex) {
        return ResponseEntity.ok(teacherService.findTeachersBySex(sex));
    }

    @GetMapping("/department/{dname}")
    public ResponseEntity<List<Teacher>> getTeachersByDepartment(@PathVariable String dname) {
        return ResponseEntity.ok(teacherService.findTeachersByDepartment(dname));
    }

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.create(teacher));
    }

    @PostMapping("/import")
    public ResponseEntity<List<Teacher>> importTeachers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(teacherService.importTeachers(file));
    }
}
