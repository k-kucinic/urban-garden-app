package hr.java.garden.projektkucinic.utils;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.enUm.PlantType;
import hr.java.garden.projektkucinic.enUm.SeasonOfInterest;
import hr.java.garden.projektkucinic.exceptions.InvalidRoleException;
import hr.java.garden.projektkucinic.login.AdminRequest;
import hr.java.garden.projektkucinic.login.Role;
import hr.java.garden.projektkucinic.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DatabaseUtils {
    public static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String DATABASE_FILE = "conf/database.properties";

    public static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String databaseUrl = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(databaseUrl, username, password);
    }

    public static List<Plant> getPlantsFromDatabase() {
        List<Plant> plants = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM PLANT;";
            Statement stmt = connection.createStatement();
            stmt.execute(baseSqlQuery);
            ResultSet rs = stmt.getResultSet();
            mapResultSetToPlants(rs, plants);
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }
        return plants;
    }

    public static List<Plant> getPlants(List<Long> ids) {
        List<Plant> plants = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM PLANT WHERE ID IN (?);";
            StringBuilder stringBuilder = new StringBuilder();
            for (Long id : ids) {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(id.toString());
            }
            String idsAsString = stringBuilder.toString();
            PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.replace("?", idsAsString));
            ResultSet rs = preparedStatement.executeQuery();
            mapResultSetToPlants(rs, plants);
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }
        return plants;
    }

    public static List<Long> plantIDsFromGardenInDatabase(Long gardenId) {
        List<Long> plantIDs = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String queryToGetPlants = "SELECT PLANT_ID FROM GARDEN_PLANT WHERE GARDEN_ID=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(queryToGetPlants);
            preparedStatement.setLong(1, gardenId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long plantId = resultSet.getLong("PLANT_ID");
                plantIDs.add(plantId);
            }
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }
        return plantIDs;

    }

    private static void mapResultSetToPlants(ResultSet rs, List<Plant> plants) throws SQLException {
        while (rs.next()) {
            Long id = rs.getLong("ID");
            int typeID = rs.getInt("PLANT_TYPE_ID");
            PlantType plantType = PlantType.ORNAMENTAL_PLANT;
            for (PlantType p : PlantType.values()) {
                if (p.getPlantTypeId() == typeID) {
                    plantType = p;
                    break;
                }
            }
            String name = rs.getString("NAME");
            LocalDate optionalPlantingDate = rs.getTimestamp("PLANTING_DATE")
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            BigDecimal requiredSpace = rs.getBigDecimal("REQUIRED_SPACE");
            BigDecimal waterNeeded = rs.getBigDecimal("WATER_NEEDED");
            int wateringFrequency = rs.getInt("WATERING_FREQUENCY");
            WateringFrequency wateringFrequency1 = new WateringFrequency(wateringFrequency);

            int isMedicalInt = rs.getInt("IS_MEDICAL");
            if (isMedicalInt == 1) {
                SeasonOfInterest seasonOfInterest = SeasonOfInterest.findSeason(rs.getString("SEASON_OF_INTEREST"));
                MedicalHerbs medicalHerbs = new MedicalHerbs(name, plantType, optionalPlantingDate, requiredSpace,
                        waterNeeded, wateringFrequency1, seasonOfInterest, id);
                plants.add(medicalHerbs);
            } else {
                Plant.PlantBuilder plantBuilder = new Plant.PlantBuilder()
                        .withName(name)
                        .withType(plantType)
                        .withPlantingDate(optionalPlantingDate)
                        .requiredSpace(requiredSpace)
                        .withWaterNeeded(waterNeeded)
                        .withWateringFrequency(wateringFrequency1)
                        .withId(id);
                Plant plant = plantBuilder.build();
                plants.add(plant);
            }

        }

    }

    public static void writePlantIntoDatabase(Plant plant) {
        try (Connection connection = connectToDatabase()) {
            String insertPlantSql = "INSERT INTO PLANT (PLANT_TYPE_ID, NAME, PLANTING_DATE, REQUIRED_SPACE, WATER_NEEDED, WATERING_FREQUENCY, SEASON_OF_INTEREST, IS_MEDICAL) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertPlantSql);
            Integer plantTypeId = plant.getPlantType().getPlantTypeId();
            preparedStatement.setInt(1, plantTypeId);
            preparedStatement.setString(2, plant.getName());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(plant.getPlantingDate().atStartOfDay()));
            preparedStatement.setBigDecimal(4, plant.getRequiredSpace());
            preparedStatement.setBigDecimal(5, plant.getWaterNeeded());
            preparedStatement.setInt(6, plant.getWateringFrequencyDays().days());
            if (plant instanceof MedicalHerbs medicalHerbs) {
                preparedStatement.setString(7, medicalHerbs.getSeasonOfInterest().getSeasonName());
                preparedStatement.setInt(8, 1);
            } else {
                preparedStatement.setString(7, null);
                preparedStatement.setInt(8, 0);
            }
            preparedStatement.executeUpdate();


        } catch (SQLException | IOException ex) {
            String message = "An error occurred while saving data to the database!";
            logger.error(message, ex);
        }

    }

    public static void deletePlantFromDatabase(Long plantID) throws InvalidRoleException {
        if (UrbanGardenApplication.user.getRole() == Role.VISITOR) {
            throw new InvalidRoleException("You do not have the necessary permissions to access the requested resource.");
        }
        try (Connection connection = connectToDatabase()) {
            logger.debug(plantID.toString());
            String deletePlantSql = "DELETE FROM PLANT WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deletePlantSql);
            preparedStatement.setLong(1, plantID);
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void deletePlantFromGarden(Long plantID) throws InvalidRoleException {
        if (UrbanGardenApplication.user.getRole() == Role.VISITOR) {
            throw new InvalidRoleException("You do not have the necessary permissions to access the requested resource.");
        }
        try (Connection connection = connectToDatabase()) {
            String deletePlantSql = "DELETE FROM GARDEN_PLANT WHERE PLANT_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deletePlantSql);
            preparedStatement.setLong(1, plantID);
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void writePlantIntoGarden(long plantID) {
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "INSERT INTO GARDEN_PLANT (GARDEN_ID, PLANT_ID) VALUES(?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, UrbanGardenApplication.getGardenID());
            preparedStatement.setLong(2, plantID);
            preparedStatement.executeUpdate();
        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static long findPlantID(String plantName) {
        long plantID = 0L;
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT ID FROM PLANT WHERE NAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, plantName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                plantID = resultSet.getLong("ID");
            }

        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
        return plantID;
    }


    public static void editPlantInDatabase(Plant.PlantBuilder plantBuilder) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE PLANT SET ");
        int parameterIndex = 0;
        Map<Integer, Boolean> allChangedInfo = new HashMap<>();
        boolean first = true;

        try (Connection connection = connectToDatabase()) {
            if (Optional.ofNullable(plantBuilder.getName()).isPresent()) {
                stringBuilder.append("NAME = ?");
                first = false;
                parameterIndex++;
                allChangedInfo.put(1, true);
            } else {
                allChangedInfo.put(1, false);
            }

            if (Optional.ofNullable(plantBuilder.getRequiredSpace()).isPresent()) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append("REQUIRED_SPACE = ?");
                first = false;
                parameterIndex++;
                allChangedInfo.put(2, true);
            } else {
                allChangedInfo.put(2, false);
            }

            if (Optional.ofNullable(plantBuilder.getWaterNeeded()).isPresent()) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append("WATER_NEEDED = ?");
                first = false;
                parameterIndex++;
                allChangedInfo.put(3, true);
            } else {
                allChangedInfo.put(3, false);
            }

            if (Optional.ofNullable(plantBuilder.getWateringFrequencyDays()).isPresent()) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append("WATERING_FREQUENCY = ?");
                parameterIndex++;
                allChangedInfo.put(4, true);
            } else {
                allChangedInfo.put(4, false);
            }

            stringBuilder.append(" WHERE ID = ").append(plantBuilder.getID());
            PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString());

            int parameterIndexNew = 1;
            if (allChangedInfo.get(1)) {
                preparedStatement.setString(parameterIndexNew, plantBuilder.getName());
                parameterIndexNew++;
            }

            if (allChangedInfo.get(2)) {
                preparedStatement.setBigDecimal(parameterIndexNew, plantBuilder.getRequiredSpace());
                parameterIndexNew++;
            }

            if (allChangedInfo.get(3)) {
                preparedStatement.setBigDecimal(parameterIndexNew, plantBuilder.getWaterNeeded());
                parameterIndexNew++;
            }

            if (allChangedInfo.get(4)) {
                preparedStatement.setInt(parameterIndexNew, plantBuilder.getWateringFrequencyDays().days());
                parameterIndexNew++;
            }

            if (parameterIndex != 0) {
                preparedStatement.executeUpdate();
            }

        } catch (IOException | SQLException e) {
            logger.error("Error connecting to the database", e);
        }
    }


    public static List<UrbanGarden> getGardensFromDatabase() {
        List<UrbanGarden> urbanGardens = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM GARDEN;";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(baseSqlQuery);
            while (rs.next()) {
                Long id = rs.getLong("ID");
                String location = rs.getString("LOCATION");
                String firstName = rs.getString("OWNER_NAME");
                String ownerLastName = rs.getString("OWNER_LAST_NAME");
                BigDecimal length = rs.getBigDecimal("LENGTH");
                BigDecimal width = rs.getBigDecimal("WIDTH");
                UrbanGarden newGarden = new UrbanGarden(id, location, firstName, ownerLastName, length, width, new HashSet<>());
                urbanGardens.add(newGarden);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception: " + e.getMessage());
        } catch (IOException e) {
            logger.error("IO Exception: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Runtime Exception: " + e.getMessage());
        }
        return urbanGardens;
    }


    public static UrbanGarden getUrbanGardenByID(Set<Plant> plants, long id) {
        Optional<UrbanGarden> ug = Optional.empty();
        try (Connection connection = connectToDatabase()) {
            String deletePlantSql = "SELECT * FROM GARDEN WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deletePlantSql);
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String location = rs.getString("LOCATION");
                String firstName = rs.getString("OWNER_NAME");
                String ownerLastName = rs.getString("OWNER_LAST_NAME");
                BigDecimal length = rs.getBigDecimal("LENGTH");
                BigDecimal width = rs.getBigDecimal("WIDTH");
                UrbanGarden garden = new UrbanGarden(id, location, firstName, ownerLastName, length, width, plants);
                return garden;
            } else {
                return ug.get();
            }

        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }
        return ug.get();
    }

    public static List<Event> getEventsFromGarden() {
        List<Event> events = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQueryForEvents = "SELECT * FROM EVENT WHERE GARDEN_ID=" + UrbanGardenApplication.urbanGarden.getId() + ";";
            Statement statement = connection.createStatement();
            statement.execute(sqlQueryForEvents);
            ResultSet resultSet = statement.getResultSet();
            mapResultSetToEvents(resultSet, events);


        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
        return events;
    }

    public static List<Event> getEventsFromDatabase() {
        List<Event> events = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQueryForEvents = "SELECT * FROM EVENT;";
            Statement statement = connection.createStatement();
            statement.execute(sqlQueryForEvents);
            ResultSet resultSet = statement.getResultSet();
            mapResultSetToEvents(resultSet, events);


        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
        return events;
    }

    public static List<Event> getEventsByGardenLocation(String location) {
        List<Event> events = new ArrayList<>();
        String sqlQuery = "SELECT event.* FROM EVENT INNER JOIN GARDEN ON event.GARDEN_ID = GARDEN.ID WHERE GARDEN.LOCATION = ?";

        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setString(1, location);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                LocalDate eventDate = resultSet.getDate("DATE").toLocalDate();
                String description = resultSet.getString("DESCRIPTION");
                int peopleInvited = resultSet.getInt("PEOPLE_INVITED");
                int gardenId = resultSet.getInt("GARDEN_ID");

                Event event = new Event(id, name, eventDate, description, peopleInvited, gardenId);
                events.add(event);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    public static List<Event> getEventsFromGardenWithID(Integer gardenID) {
        List<Event> events = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQueryForEvents = "SELECT * FROM EVENT WHERE GARDEN_ID=" + gardenID + ";";
            Statement statement = connection.createStatement();
            statement.execute(sqlQueryForEvents);
            ResultSet resultSet = statement.getResultSet();
            mapResultSetToEvents(resultSet, events);


        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
        return events;
    }

    private static void mapResultSetToEvents(ResultSet resultSet, List<Event> events) {
        try {
            while (resultSet.next()) {
                Long id = resultSet.getLong("ID");
                LocalDate eventDate = resultSet.getTimestamp("DATE").toLocalDateTime().toLocalDate();
                String eventName = resultSet.getString("NAME");
                String eventDescription = resultSet.getString("DESCRIPTION");
                Integer numberOpPeopleInvited = resultSet.getInt("PEOPLE_INVITED");
                Integer gardenId = resultSet.getInt("GARDEN_ID");
                Event event = new Event(id, eventName, eventDate, eventDescription, numberOpPeopleInvited, gardenId);
                events.add(event);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void writeEventIntoDatabase(Event event) {
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "INSERT INTO EVENT (DATE, NAME, DESCRIPTION, PEOPLE_INVITED, GARDEN_ID ) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(event.getDate().atStartOfDay()));
            preparedStatement.setString(2, event.getEventName());
            preparedStatement.setString(3, event.getDescription());
            preparedStatement.setInt(4, event.getNumberOfPeopleInvited());
            preparedStatement.setInt(5, (int) UrbanGardenApplication.getUrbanGarden().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void editEvent(Event old, Event newEvent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE EVENT SET ");
        boolean first = true;
        Map<Integer, Boolean> allChangedInfo = new HashMap<>();

        try (Connection connection = connectToDatabase()) {
            if (!old.getEventName().equals(newEvent.getEventName())) {
                stringBuilder.append("NAME = ?");
                first = false;
                allChangedInfo.put(1, true);
            } else {
                allChangedInfo.put(1, false);
            }

            if (!old.getDate().equals(newEvent.getDate())) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append("DATE = ?");
                first = false;
                allChangedInfo.put(2, true);
            } else {
                allChangedInfo.put(2, false);
            }


            if (old.getNumberOfPeopleInvited() != newEvent.getNumberOfPeopleInvited()) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append("PEOPLE_INVITED = ?");
                allChangedInfo.put(3, true);
            } else {
                allChangedInfo.put(3, false);
            }
            if (old.getDescription().compareTo(newEvent.getDescription())!=0) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append("DESCRIPTION = ?");
                allChangedInfo.put(4, true);
            } else {
                allChangedInfo.put(4, false);
            }

            stringBuilder.append(" WHERE ID = ").append(old.getId()).append(";");
            PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString());

            int parameterIndexNew = 1;
            if (allChangedInfo.get(1)) {
                preparedStatement.setString(parameterIndexNew, newEvent.getEventName());
                parameterIndexNew++;
            }
            if (allChangedInfo.get(2)) {
                preparedStatement.setTimestamp(parameterIndexNew, Timestamp.valueOf(newEvent.getDate().atStartOfDay()));
                parameterIndexNew++;
            }
            if (allChangedInfo.get(3)) {
                preparedStatement.setInt(parameterIndexNew, newEvent.getNumberOfPeopleInvited());
                parameterIndexNew++;
            }
            if (allChangedInfo.get(4)) {
                preparedStatement.setString(parameterIndexNew, newEvent.getDescription());
            }

            if (parameterIndexNew > 1) {
                preparedStatement.executeUpdate();
            }

        } catch (IOException | SQLException e) {
            logger.error("Error connecting to the database", e);
        }
    }


    public static void insertAdminRequest(String username, Role currentRole, boolean approved) {
        String insertSQL = "INSERT INTO AdminRequest (username, currentRole, approved) VALUES (?, ?, ?)";
        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, currentRole.name());
            preparedStatement.setBoolean(3, approved);

            preparedStatement.executeUpdate();
        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static List<AdminRequest> getAdminRequestsFromDatabase() {
        List<AdminRequest> adminRequestList = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM ADMINREQUEST;";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(baseSqlQuery);
            while (rs.next()) {
                Long id = rs.getLong("ID");
                String username = rs.getString("USERNAME");
                Role currentRole = Role.getRoleFromName(rs.getString("CURRENTROLE"));
                boolean approved = rs.getBoolean("APPROVED");
                AdminRequest adminRequest = new AdminRequest(username, currentRole, approved);
                adminRequestList.add(adminRequest);
            }
        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
        return adminRequestList;

    }

    public static void deleteAdminRequestByUsername(Integer id) {
        String sql = "DELETE FROM ADMINREQUEST WHERE ID = ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException e) {
            logger.error("Error deleting request: " + e.getMessage());
        }
    }

    public static int findRequestID(String username) {
        int requestID = 0;
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT ADMINREQUEST.ID FROM ADMINREQUEST  WHERE USERNAME=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                requestID = resultSet.getInt("ID");
            }

        } catch (SQLException | RuntimeException | IOException e) {
            logger.error(e.getMessage());
        }
        return requestID;
    }

    public static void approveRequest(String username) {
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "UPDATE ADMINREQUEST SET approved = true WHERE username =?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }
    }


}
