package it.unimib.bicap.exception;

public class LoginSomministratoreException extends RuntimeException{
    public static final RuntimeException LOGIN_VIEW_FAIL = new RuntimeException("Si è verificato un problema nella visualizzazione in LoginSomministratore");
}
