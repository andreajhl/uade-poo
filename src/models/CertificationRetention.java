package models;

import models.enums.TaxType;

import java.time.LocalDate;

public class CertificationRetention {

    private TaxType taxType;
    private LocalDate issueDate;
    private LocalDate expirationDate;

    public CertificationRetention(TaxType taxType, LocalDate issueDate, LocalDate expirationDate) {
        this.taxType = taxType;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
    }

    public boolean isValid(LocalDate onDate) {
        return !onDate.isAfter(expirationDate);
    }

    public TaxType getTaxType() { return taxType; }

    public LocalDate getIssueDate() { return issueDate; }

    public LocalDate getExpirationDate() { return expirationDate; }
}
