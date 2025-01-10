package com.certTrack.CertificationService.DTO;

import java.time.LocalDate;

public class CertificationDTO {
    private int id;
    private int userId;
    private String courseName;
    private LocalDate issuedDate;
    private String validationCode;

    public CertificationDTO(int id, int userId, String courseName, LocalDate issuedDate, String validationCode) {
        this.id = id;
        this.userId = userId;
        this.courseName = courseName;
        this.issuedDate = issuedDate;
        this.validationCode = validationCode;
    }

    public CertificationDTO() {
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }
}