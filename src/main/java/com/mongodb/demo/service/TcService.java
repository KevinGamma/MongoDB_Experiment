package com.mongodb.demo.service;

import com.mongodb.demo.entity.TcRecord;
import com.mongodb.demo.repository.TcRepository;
import com.mongodb.demo.util.ExcelUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TcService {

    private final TcRepository tcRepository;

    public TcService(TcRepository tcRepository) {
        this.tcRepository = tcRepository;
    }

    public List<TcRecord> findAll() {
        return tcRepository.findAll();
    }

    public List<TcRecord> findByCid(Integer cid) {
        return tcRepository.findByCid(cid);
    }

    public TcRecord create(TcRecord record) {
        return tcRepository.save(record);
    }

    public List<TcRecord> importRecords(MultipartFile file) {
        List<Map<String, String>> rows = ExcelUtils.readSheet(file);
        List<TcRecord> records = rows.stream()
                .map(this::toRecord)
                .toList();
        return tcRepository.saveAll(records);
    }

    private TcRecord toRecord(Map<String, String> row) {
        Map<String, String> normalized = normalize(row);
        TcRecord record = new TcRecord();
        record.setCid(parseInteger(normalized.get("CID")));
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
