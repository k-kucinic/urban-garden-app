package hr.java.garden.projektkucinic.utils;

import hr.java.garden.projektkucinic.enUm.ChangeType;
import hr.java.garden.projektkucinic.login.ChangeLogging;
import hr.java.garden.projektkucinic.login.Role;
import hr.java.garden.projektkucinic.login.User;
import hr.java.garden.projektkucinic.model.Plant;
import hr.java.garden.projektkucinic.model.UrbanGarden;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public class FileUtils {
    private static final String LOGIN = "files/login.txt";
    private static final String PLANTS_SERIALIZED = "files/plants_deleted.dat";

    public static void writeUserInFile(List<User> users) {
        File categoriesFile = new File(LOGIN);
        try (PrintWriter pw = new PrintWriter(categoriesFile)) {
            for (User user : users) {
                pw.println(user.getUsername());
                pw.println(user.getPassword());
                pw.println(user.getRole().getRoleName());
                pw.println(user.getGarden().getId());
            }
        } catch (IOException ex) {
            String message = "An error occurred while writing to the file.";
            logger.error(message);
        }

    }

    public static List<User> getUsersFromFile() {
        File usersFile = new File(LOGIN);
        List<User> allUsers = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(usersFile))) {
            Optional<String> optionalUsername;
            while ((optionalUsername = Optional.ofNullable(bufferedReader.readLine())).isPresent()) {
                String userName = optionalUsername.get();
                String hashedPassword = bufferedReader.readLine();
                String role = bufferedReader.readLine();
                String gardenIdString = bufferedReader.readLine();

                Optional<Role> role2 = Optional.empty();
                for (Role role1 : Role.values()) {
                    if (role1.getRoleName().equals(role)) {
                        role2 = Optional.of(role1);
                    }
                }
                Integer gardenId = Integer.valueOf(gardenIdString);
                Set<Plant> plantsInThisGarden = new HashSet<>();
                List<Long> plantIDs = DatabaseUtils.plantIDsFromGardenInDatabase(Long.valueOf(gardenId));
                plantsInThisGarden = DatabaseUtils.getPlants(plantIDs).stream().collect(Collectors.toSet());
                UrbanGarden urbanGarden = DatabaseUtils.getUrbanGardenByID(plantsInThisGarden, gardenId);
                if (role2.isPresent()) {
                    allUsers.add(new User(userName, hashedPassword, role2.get(), urbanGarden));
                }

            }
        } catch (IOException e) {
            logger.error("An error occurred while getting data from the file.");
        }
        return allUsers;
    }

    public static void serializePlantChanges(List<ChangeLogging> changeLoggingList, ChangeType changeType) {
        String fileName = "";
        if (changeType == ChangeType.UPDATE) {
            fileName = "files/plant_changes.dat";
        } else {
            fileName = PLANTS_SERIALIZED;
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(changeLoggingList);
        } catch (IOException e) {
            String message = "Serialization error. ";
            logger.error(message, e);

        }
    }

    public static List<ChangeLogging> deserializePlants(ChangeType changeType) {
        String fileName = "";
        if (changeType == ChangeType.UPDATE) {
            fileName = "files/plant_changes.dat";
        } else {
            fileName = PLANTS_SERIALIZED;
        }
        File file = new File(fileName);
        List<ChangeLogging> plants = new ArrayList<>();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            plants = (List<ChangeLogging>) objectInputStream.readObject();
        } catch (EOFException e) {
            logger.error(e.getMessage() + " " + changeType.getChangeType());
        } catch (InvalidClassException e) {
            logger.error(e.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        return plants;
    }

}
