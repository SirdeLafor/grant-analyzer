package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.MainApp;
import org.example.model.GrantData;
import org.example.model.UniversityData;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataService {
    private Map<String, String> universityMap;
    private final DatabaseService databaseService;

    public DataService(DatabaseService databaseService) {
        this.databaseService = databaseService;
        loadUniversities();
    }

    private void loadUniversities() {
        try (InputStream is = MainApp.class.getResourceAsStream("/org/example/university-data.json")) {
            if (is == null) {
                System.err.println("Не удалось найти university-data.json");
                universityMap = Map.of();
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            List<UniversityData> universities = mapper.readValue(is, new TypeReference<>() {});
            this.universityMap = universities.stream()
                    .collect(Collectors.toMap(UniversityData::getCode, UniversityData::getName));
        } catch (Exception e) {
            e.printStackTrace();
            universityMap = Map.of();
        }
    }

    public String getUniversityNameByCode(String code) {
        return universityMap.getOrDefault(code, "ВУЗ с кодом " + code);
    }

    public List<GrantData> getLoadedGrantData() {
        return databaseService.loadGrants(this);
    }

    public void saveGrantData(List<GrantData> data) {
        databaseService.saveGrants(data);
    }

    public boolean hasData() {
        return databaseService.hasData();
    }
}