package hr.java.garden.projektkucinic.comparator;

import hr.java.garden.projektkucinic.model.AllEvents;

import java.util.Comparator;

public class GardenEventComparator implements Comparator<AllEvents> {
    @Override
    public int compare(AllEvents o1, AllEvents o2) {
        int dateComparison = o1.getDate().compareTo(o2.getDate());

        if (dateComparison == 0) {
            return o1.getDescription().compareTo(o2.getDescription());
        }


        return dateComparison;
    }
}
