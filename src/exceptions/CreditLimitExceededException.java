package exceptions;

public class CreditLimitExceededException extends Exception {

    public CreditLimitExceededException(float currentDebt, float newOrderTotal, float creditLimit) {
        super(String.format(
            "La deuda proyectada (%.2f + %.2f = %.2f) supera el tope de crédito (%.2f).",
            currentDebt, newOrderTotal, currentDebt + newOrderTotal, creditLimit
        ));
    }
}
