package it.unimib.bicap.exception;

public class GetterException extends RuntimeException {

    public RuntimeException GETTER_JSON_FAIL = new RuntimeException("Si è verificato un problema nel Getter, non si riesce a prendere: ");

    public GetterException(String campoFallito) {
        GETTER_JSON_FAIL = new RuntimeException(this.GETTER_JSON_FAIL.getMessage() + campoFallito);
    }
}
