package it.unimib.bicap.exception;

public class IntermediateException extends RuntimeException {

    public static final RuntimeException INTERMEDIATE_PASSI_NULL = new RuntimeException("Non sono stati passati i passi, passi == null");
    public static final RuntimeException INTERMEDIATE_NOME_PROGETTO_NULL = new RuntimeException("Non è stato passato il progetto, progetto == null");
    public static final RuntimeException INTERMEDIATE_ID_PROGETTO_NULL = new RuntimeException("Non è stato passato l'id progetto, idProgetto == null");
    public static final RuntimeException INTERMEDIATE_MODALITA_NULL = new RuntimeException("Non è stato passata la modalità, modalita == null");


}
