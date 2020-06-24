package it.unimib.bicap.exception;

public class PresentazioneProgettoException extends RuntimeException {

    public static final RuntimeException PRESENTAZIONE_PROGETTO_PROGETTO_NULL = new RuntimeException("Non è stato passato il progetto in PresentazioneProgetto, " +
            "progettoString == null");
    public static final RuntimeException PRESENTAZIONE_PROGETTO_PARSER_PROGETTO_JSON_FAIL = new RuntimeException("Si è verificato un problema nella creazione del progetto JSON " +
            "in PresentazioneProgetto");


}
