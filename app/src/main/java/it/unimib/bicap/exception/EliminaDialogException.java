package it.unimib.bicap.exception;

public class EliminaDialogException extends RuntimeException {

    public static final RuntimeException ELIMINA_DIALOG_CREAZIONE_PROGETTI_FAIL = new RuntimeException("Si è verificato un problema nella creazione della nuova lista di progetti " +
            "dopo eliminazione, in EliminaDialog");

}