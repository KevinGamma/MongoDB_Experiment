package com.mongodb.demo.controller;

import com.mongodb.demo.entity.TcRecord;
import com.mongodb.demo.service.TcService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tc")
public class TcController {

    private final TcService tcService;

    public TcController(TcService tcService) {
        this.tcService = tcService;
    }

    @PostMapping
    public ResponseEntity<TcRecord> create(@RequestBody TcRecord record) {
        return ResponseEntity.ok(tcService.create(record));
    }

    @PostMapping("/import")
    public ResponseEntity<List<TcRecord>> importRecords(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(tcService.importRecords(file));
    }
}
