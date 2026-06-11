package models;

import models.enums.PaymentMethodType;

import java.time.LocalDate;

public class PaymentItem {

    private final PaymentMethodType type;
    private final float amount;
    private final String checkNumber;
    private final LocalDate checkIssueDate;
    private final LocalDate checkDueDate;
    private final String checkSigner;

    public PaymentItem(PaymentMethodType type, float amount) {
        this.type = type;
        this.amount = amount;
        this.checkNumber = null;
        this.checkIssueDate = null;
        this.checkDueDate = null;
        this.checkSigner = null;
    }

    public PaymentItem(PaymentMethodType type, float amount,
                       String checkNumber, LocalDate checkIssueDate,
                       LocalDate checkDueDate, String checkSigner) {
        this.type = type;
        this.amount = amount;
        this.checkNumber = checkNumber;
        this.checkIssueDate = checkIssueDate;
        this.checkDueDate = checkDueDate;
        this.checkSigner = checkSigner;
    }

    public boolean isCheck() {
        return type == PaymentMethodType.CHEQUE_PROPIO || type == PaymentMethodType.CHEQUE_DE_TERCEROS;
    }

    public PaymentMethodType getType() { return type; }
    public float getAmount() { return amount; }
    public String getCheckNumber() { return checkNumber; }
    public LocalDate getCheckIssueDate() { return checkIssueDate; }
    public LocalDate getCheckDueDate() { return checkDueDate; }
    public String getCheckSigner() { return checkSigner; }
}
