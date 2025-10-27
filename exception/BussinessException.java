package Main.java.onlinestore.exception;

public abstract class BussinessException extends RuntimeException{
    public BusinessException(String message) { super(message); }
    public BusinessException(String message, Throwable cause) { super(message, cause); }

}
