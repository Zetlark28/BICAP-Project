package it.unimib.bicap.exception;

public class UtilityException extends RuntimeException {

    public static final RuntimeException UTILITY_FIREBASE_DB_READ_VALUE_FAIL = new RuntimeException("Non si è riuscito a leggere il valore dal DB di Firebase in Utility");
    public static final RuntimeException UTILITY_FIREBASE_KEY_NOT_FOUND = new RuntimeException("Non si è riuscito a trovare il la key del DB di Firebase in Utility");
    public static final RuntimeException UTILITY_FIREBASE_UPLOAD_FAIL = new RuntimeException("L'upload del file su FireBase è fallito in Utility");
    public static final RuntimeException UTILITY_ACTIVITY_CONTEXT_UNAUTHORIZED = new RuntimeException("Non è consentito chiamare il metodo in Utility dall actvity");

}
