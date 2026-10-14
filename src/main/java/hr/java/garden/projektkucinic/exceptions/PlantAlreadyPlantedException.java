package hr.java.garden.projektkucinic.exceptions;

public class PlantAlreadyPlantedException extends Exception {
    public PlantAlreadyPlantedException() {
    }

    public PlantAlreadyPlantedException(String message) {
        super(message);
    }

    public PlantAlreadyPlantedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlantAlreadyPlantedException(Throwable cause) {
        super(cause);
    }

    public PlantAlreadyPlantedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
