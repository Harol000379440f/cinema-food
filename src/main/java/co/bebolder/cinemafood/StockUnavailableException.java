package co.bebolder.cinemafood;

public class StockUnavailableException extends Throwable {
    public StockUnavailableException(String mensaje) {
        super(mensaje);
    }
}
