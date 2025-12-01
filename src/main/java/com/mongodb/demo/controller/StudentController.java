package com.mongodb.demo.controller;

import com.mongodb.demo.entity.Student;
import com.mongodb.demo.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.findAllStudents());
    }

    @GetMapping("/under-20")
    public ResponseEntity<List<Student>> getStudentsUnder20() {
        return ResponseEntity.ok(studentService.findStudentsYoungerThan(20));
    }

    @GetMapping("/under-20/software")
    public ResponseEntity<List<Student>> getStudentsUnder20InSoftware() {
        return ResponseEntity.ok(studentService.findStudentsYoungerThanInDepartment(20, "软件学院"));
    }

    @GetMapping("/name-age")
    public ResponseEntity<List<Map<String, Object>>> getStudentNamesAndAges() {
        return ResponseEntity.ok(studentService.findNameAndAgeForAll());
    }

    @GetMapping("/under-20/name-sex")
    public ResponseEntity<List<Map<String, Object>>> getStudentsUnder20NameAndSex() {
        return ResponseEntity.ok(studentService.findNameAndSexYoungerThan(20));
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.create(student));
    }

    @PostMapping("/import")
    public ResponseEntity<List<Student>> importStudents(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(studentService.importStudents(file));
    }
}
