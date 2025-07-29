package org.example.model;

import javafx.beans.property.SimpleStringProperty;

public class University {
    private final SimpleStringProperty code;
    private final SimpleStringProperty name;
    private final SimpleStringProperty shortName;

    public University(String code, String name, String shortName) {
        this.code = new SimpleStringProperty(code);
        this.name = new SimpleStringProperty(name);
        this.shortName = new SimpleStringProperty(shortName);
    }

    // Геттеры
    public String getCode() { return code.get(); }
    public String getName() { return name.get(); }
    public String getShortName() { return shortName.get(); }
    @Override public String toString() { return name.get(); }
}
