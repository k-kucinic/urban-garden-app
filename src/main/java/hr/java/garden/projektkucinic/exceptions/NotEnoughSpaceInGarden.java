package hr.java.garden.projektkucinic.exceptions;

public class NotEnoughSpaceInGarden extends RuntimeException {
    public NotEnoughSpaceInGarden() {
    }

    public NotEnoughSpaceInGarden(String message) {
        super(message);
    }

    public NotEnoughSpaceInGarden(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughSpaceInGarden(Throwable cause) {
        super(cause);
    }

    public NotEnoughSpaceInGarden(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
