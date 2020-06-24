package it.unimib.bicap.exception;

public class RecuperaPasswordException extends RuntimeException{
    public static final RuntimeException RECUPERA_PASSWORD_VIEW_FAIL = new RuntimeException("Si è verificato un problema nella visualizzazione in RecuperaPassword");
}
