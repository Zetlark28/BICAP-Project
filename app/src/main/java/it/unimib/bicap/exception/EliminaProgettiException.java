package it.unimib.bicap.exception;

public class EliminaProgettiException extends RuntimeException {

    public static final RuntimeException ELIMINA_PROGETTI_LISTA_PROGETTI_NULL = new RuntimeException("Non è stata passata la lista dei progetti in Elimina Progetti, listaProgetti == null");
    public static final RuntimeException ELIMINA_PROGETTI_LISTA_PROGETTI_AUTORE_NULL = new RuntimeException("Non è stata passata la lista dei progetti dell'autore in Elimina Progetti, listaProgettiAutore == null");

}
