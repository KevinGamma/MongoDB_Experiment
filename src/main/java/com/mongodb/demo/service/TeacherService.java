package com.mongodb.demo.service;

import com.mongodb.demo.entity.Teacher;
import com.mongodb.demo.repository.TeacherRepository;
import com.mongodb.demo.util.ExcelUtils;
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

    public Teacher create(Teacher teacher) {
        return teacherRepository.save(teacher);
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
        teacher.setTid(normalized.getOrDefault("TID", ""));
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
}
