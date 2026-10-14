package hr.java.garden.projektkucinic.enUm;

import java.time.MonthDay;

public enum SeasonOfInterest {
    WINTER("Winter", MonthDay.of(12, 21), MonthDay.of(3, 20)),
    SPRING("Spring", MonthDay.of(3, 21), MonthDay.of(6, 20)),
    SUMMER("Summer", MonthDay.of(6, 21), MonthDay.of(9, 22)),
    AUTUMN("Autumn", MonthDay.of(9, 23), MonthDay.of(12, 20));

    private String seasonName;
    private MonthDay startingDate;
    private MonthDay endingDate;

    SeasonOfInterest(String seasonName, MonthDay startingDate, MonthDay endingDate) {
        this.seasonName = seasonName;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
    }

    public static SeasonOfInterest findSeason(String seasonName) {
        for (SeasonOfInterest s : SeasonOfInterest.values()) {
            if (seasonName.equals(s.seasonName)) {
                return s;
            }
        }
        return SeasonOfInterest.AUTUMN;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public MonthDay getStartingDate() {
        return startingDate;
    }

    public MonthDay getEndingDate() {
        return endingDate;
    }


}
