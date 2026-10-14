package hr.java.garden.projektkucinic.threads;

import hr.java.garden.projektkucinic.enUm.ChangeType;
import hr.java.garden.projektkucinic.login.ChangeLogging;

import java.util.List;

public class SerializationThread extends FileThread implements Runnable {
    private List<ChangeLogging> changeLoggingList;
    private ChangeType changeType;

    public SerializationThread(List<ChangeLogging> changeLoggingList, ChangeType changeType) {
        this.changeLoggingList = changeLoggingList;
        this.changeType=changeType;
    }

    @Override
    public void run() {
        super.serializeChanges(changeLoggingList, changeType);
    }

}
