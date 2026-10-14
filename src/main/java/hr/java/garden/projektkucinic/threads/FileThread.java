package hr.java.garden.projektkucinic.threads;

import hr.java.garden.projektkucinic.enUm.ChangeType;
import hr.java.garden.projektkucinic.login.ChangeLogging;
import hr.java.garden.projektkucinic.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public abstract class FileThread {

    public static boolean isFileOperationInProgress = false;

    public synchronized void serializeChanges(List<ChangeLogging> changeLoggingList, ChangeType changeType) {
        while (isFileOperationInProgress) {

            try {
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
        isFileOperationInProgress = true;
        FileUtils.serializePlantChanges(changeLoggingList, changeType);
        isFileOperationInProgress = false;
        notifyAll();
    }

    public synchronized List<ChangeLogging> changeLoggingListDeserialized(ChangeType changeType) {
        List<ChangeLogging> changeLoggingList = new ArrayList<>();
        while (isFileOperationInProgress) {
            logger.debug("in progress");
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
        isFileOperationInProgress = true;
        changeLoggingList = FileUtils.deserializePlants(changeType);
        isFileOperationInProgress = false;
        notifyAll();
        return changeLoggingList;
    }

    public synchronized void notifyCompletion() {
        isFileOperationInProgress = false;
        notifyAll();
    }
}
