package it.unimib.bicap.exception;

public class DettaglioQuestionarioException extends RuntimeException {

    public static final RuntimeException DETTAGLIO_QUESTIONARIO_PROGETTO_NULL = new RuntimeException("Non è stato passato il progetto in DettaglioQuestionario," +
            " progettoString == null");
    public static final RuntimeException DETTAGLIO_QUESTIONARIO_PARSER_PROGETTO_JSON_FAIL = new RuntimeException("Si è verificato un problema nella creazione del progetto JSON " +
            "in DettaglioQuestionario");
}
