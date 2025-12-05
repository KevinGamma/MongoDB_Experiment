package com.mongodb.demo.service;

import com.mongodb.demo.entity.ScRecord;
import com.mongodb.demo.repository.ScRepository;
import com.mongodb.demo.util.ExcelUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScService {
    private final ScRepository scRepository;

    public ScService(ScRepository scRepository) {
        this.scRepository = scRepository;
    }

    public ScRecord create(ScRecord record) {
        return scRepository.save(record);
    }

    public List<ScRecord> findBySid(String sid) {
        return scRepository.findBySid(sid);
    }

    public ScRecord update(String id, ScRecord record) {
        ScRecord existing = scRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("选课记录不存在: " + id));
        existing.setSid(record.getSid());
        existing.setCid(record.getCid());
        existing.setScore(record.getScore());
        existing.setTid(record.getTid());
        return scRepository.save(existing);
    }

    public List<ScRecord> bulkUpdate(List<ScRecord> records) {
        return records.stream()
                .map(record -> update(record.getId(), record))
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        scRepository.deleteById(id);
    }

    public Optional<ScRecord> findById(String id) {
        return scRepository.findById(id);
    }

    public List<ScRecord> importRecords(MultipartFile file) {
        List<Map<String, String>> rows = ExcelUtils.readSheet(file);
        List<ScRecord> records = rows.stream()
                .map(this::toRecord)
                .toList();
        return scRepository.saveAll(records);
    }

    private ScRecord toRecord(Map<String, String> row) {
        Map<String, String> normalized = normalize(row);
        ScRecord record = new ScRecord();
        record.setSid(normalized.getOrDefault("SID", ""));
        record.setCid(parseInteger(normalized.get("CID")));
        record.setScore(parseInteger(normalized.get("SCORE")));
        record.setTid(normalized.getOrDefault("TID", ""));
        return record;
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
