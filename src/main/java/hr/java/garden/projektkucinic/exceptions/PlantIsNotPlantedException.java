package hr.java.garden.projektkucinic.exceptions;

public class PlantIsNotPlantedException extends RuntimeException {
    public PlantIsNotPlantedException() {

    }

    public PlantIsNotPlantedException(String message) {
        super(message);
    }

    public PlantIsNotPlantedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlantIsNotPlantedException(Throwable cause) {
        super(cause);
    }

    public PlantIsNotPlantedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
