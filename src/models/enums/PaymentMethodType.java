package models.enums;

public enum PaymentMethodType {
    EFECTIVO,
    TRANSFERENCIA_BANCARIA,
    CHEQUE_PROPIO,
    CHEQUE_DE_TERCEROS;

    @Override
    public String toString() {
        return switch (this) {
            case EFECTIVO -> "Efectivo";
            case TRANSFERENCIA_BANCARIA -> "Transferencia Bancaria";
            case CHEQUE_PROPIO -> "Cheque Propio";
            case CHEQUE_DE_TERCEROS -> "Cheque de Terceros";
        };
    }
}
