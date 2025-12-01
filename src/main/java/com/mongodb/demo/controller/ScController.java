package com.mongodb.demo.controller;

import com.mongodb.demo.entity.ScRecord;
import com.mongodb.demo.service.ScService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    public ResponseEntity<ScRecord> create(@RequestBody ScRecord record) {
        return ResponseEntity.ok(scService.create(record));
    }

    @PostMapping("/import")
    public ResponseEntity<List<ScRecord>> importRecords(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(scService.importRecords(file));
    }
}
