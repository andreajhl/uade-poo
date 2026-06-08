package models;

import models.enums.IVACondition;
import models.enums.TaxType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Supplier {

    private UUID id;
    private String cuit;
    private String razonSocial;
    private String fantasyName;
    private String address;
    private String phone;
    private String email;
    private IVACondition ivaCondition;
    private String ingresosBrutos;
    private LocalDate activityStartDate;
    private float creditLimit;
    private List<Category> categories;
    private List<CertificationRetention> certifications;

    public Supplier(String cuit, String razonSocial, String fantasyName, String address,
                    String phone, String email, IVACondition ivaCondition,
                    String ingresosBrutos, LocalDate activityStartDate, float creditLimit) {
        this.id = UUID.randomUUID();
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.fantasyName = fantasyName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.ivaCondition = ivaCondition;
        this.ingresosBrutos = ingresosBrutos;
        this.activityStartDate = activityStartDate;
        this.creditLimit = creditLimit;
        this.categories = new ArrayList<>();
        this.certifications = new ArrayList<>();
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }

    public void addCertification(CertificationRetention certification) {
        certifications.add(certification);
    }

    public boolean hasValidCertificationFor(TaxType taxType, LocalDate onDate) {
        for (CertificationRetention c : certifications) {
            if (c.getTaxType() == taxType && c.isValid(onDate)) return true;
        }
        return false;
    }

    public UUID getId() { return id; }

    public String getCuit() { return cuit; }

    public String getRazonSocial() { return razonSocial; }

    public String getFantasyName() { return fantasyName; }

    public String getAddress() { return address; }

    public String getPhone() { return phone; }

    public String getEmail() { return email; }

    public IVACondition getIvaCondition() { return ivaCondition; }

    public String getIngresosBrutos() { return ingresosBrutos; }

    public LocalDate getActivityStartDate() { return activityStartDate; }

    public float getCreditLimit() { return creditLimit; }

    public List<Category> getCategories() { return categories; }

    public List<CertificationRetention> getCertifications() { return certifications; }

    public void setCuit(String cuit) { this.cuit = cuit; }

    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public void setFantasyName(String fantasyName) { this.fantasyName = fantasyName; }

    public void setAddress(String address) { this.address = address; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setEmail(String email) { this.email = email; }

    public void setIvaCondition(IVACondition ivaCondition) { this.ivaCondition = ivaCondition; }

    public void setIngresosBrutos(String ingresosBrutos) { this.ingresosBrutos = ingresosBrutos; }

    public void setActivityStartDate(LocalDate activityStartDate) { this.activityStartDate = activityStartDate; }

    public void setCreditLimit(float creditLimit) { this.creditLimit = creditLimit; }

    @Override
    public String toString() { return razonSocial + " (" + cuit + ")"; }
}
