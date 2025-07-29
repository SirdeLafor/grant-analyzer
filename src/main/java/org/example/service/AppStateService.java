package org.example.service;

import org.example.model.GrantData;
import java.util.ArrayList;
import java.util.List;

public class AppStateService {
    private List<GrantData> loadedGrantData = new ArrayList<>();

    public List<GrantData> getLoadedGrantData() {
        return loadedGrantData;
    }

    public void setLoadedGrantData(List<GrantData> loadedGrantData) {
        this.loadedGrantData = loadedGrantData;
    }

    public boolean hasData() {
        return loadedGrantData != null && !loadedGrantData.isEmpty();
    }
}