package com.mongodb.demo.controller;

import com.mongodb.demo.entity.ScRecord;
import com.mongodb.demo.service.ScService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/sc")
public class ScController {

    private final ScService scService;

    public ScController(ScService scService) {
        this.scService = scService;
    }

    @GetMapping("/student/{sid}")
    public ResponseEntity<List<ScRecord>> getByStudentId(@PathVariable String sid) {
        return ResponseEntity.ok(scService.findBySid(sid));
    }

    @PostMapping
    public ResponseEntity<ScRecord> create(@RequestBody ScRecord record) {
        return ResponseEntity.ok(scService.create(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScRecord> update(@PathVariable String id, @RequestBody ScRecord record) {
        return ResponseEntity.ok(scService.update(id, record));
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<ScRecord>> bulkUpdate(@RequestBody List<ScRecord> records) {
        return ResponseEntity.ok(scService.bulkUpdate(records));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        scService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<List<ScRecord>> importRecords(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(scService.importRecords(file));
    }
}
