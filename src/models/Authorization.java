package models;

import java.time.LocalDate;

public class Authorization {

    private LocalDate authorizationDate;
    private String observation;
    private User authorizedBy;

    public Authorization(User authorizedBy, String observation) {
        this.authorizationDate = LocalDate.now();
        this.authorizedBy = authorizedBy;
        this.observation = observation;
    }

    public boolean isValid() { return authorizedBy != null; }

    public LocalDate getAuthorizationDate() { return authorizationDate; }

    public String getObservation() { return observation; }

    public User getAuthorizedBy() { return authorizedBy; }
}
