package org.example.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class GrantData {
    private final SimpleIntegerProperty number;
    private final SimpleStringProperty iin;
    private final SimpleStringProperty fullName;
    private final SimpleIntegerProperty score;
    private final SimpleStringProperty universityCode;
    private final SimpleStringProperty universityName;
    private final SimpleStringProperty quota;
    private final SimpleStringProperty specialtyName;

    public GrantData(int number, String iin, String fullName, int score, String universityCode, String universityName, String quota, String specialtyName) {
        this.number = new SimpleIntegerProperty(number);
        this.iin = new SimpleStringProperty(iin);
        this.fullName = new SimpleStringProperty(fullName);
        this.score = new SimpleIntegerProperty(score);
        this.universityCode = new SimpleStringProperty(universityCode);
        this.universityName = new SimpleStringProperty(universityName);
        this.quota = new SimpleStringProperty(quota);
        this.specialtyName = new SimpleStringProperty(specialtyName);
    }

    public void appendToFullName(String addition) {
        this.fullName.set(this.fullName.get() + " " + addition);
    }

    // Геттеры
    public int getNumber() { return number.get(); }
    public String getIin() { return iin.get(); }
    public String getFullName() { return fullName.get(); }
    public int getScore() { return score.get(); }
    public String getUniversityCode() { return universityCode.get(); }
    public String getUniversityName() { return universityName.get(); }
    public String getQuota() { return quota.get(); }
    public String getSpecialtyName() { return specialtyName.get(); }
}
