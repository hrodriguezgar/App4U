package main.java.store;

public class ArticuloNoEncontradoException extends Exception {
    public ArticuloNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}