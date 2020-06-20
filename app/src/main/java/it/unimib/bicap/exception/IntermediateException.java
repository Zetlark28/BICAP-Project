package it.unimib.bicap.exception;

public class IntermediateException extends RuntimeException {

    public static final RuntimeException INTERMEDIATE_PASSI_NULL = new RuntimeException("Non sono stati passati i passi in Intermediate, passi == null");
    public static final RuntimeException INTERMEDIATE_NOME_PROGETTO_NULL = new RuntimeException("Non è stato passato il progetto in Intermediate, progetto == null");
    public static final RuntimeException INTERMEDIATE_ID_PROGETTO_NULL = new RuntimeException("Non è stato passato l'id progetto in Intermediate, idProgetto == null");
    public static final RuntimeException INTERMEDIATE_MODALITA_NULL = new RuntimeException("Non è stato passata la modalità in Intermediate, modalita == null");


}
