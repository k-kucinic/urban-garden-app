package hr.java.garden.projektkucinic.threads;

import hr.java.garden.projektkucinic.enUm.ChangeType;
import hr.java.garden.projektkucinic.login.ChangeLogging;
import javafx.application.Platform;

import java.util.List;

public class DeserializationThread extends FileThread implements Runnable {
    private List<ChangeLogging> changeLoggingList;
    private ChangeType changeType;

    public DeserializationThread(ChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public void run() {
        this.changeLoggingList = super.changeLoggingListDeserialized(this.changeType);
        Platform.runLater(super::notifyCompletion);
    }

    public List<ChangeLogging> getChangeLoggingList() {
        return this.changeLoggingList;
    }
}
