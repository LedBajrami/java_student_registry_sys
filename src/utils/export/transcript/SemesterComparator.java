package utils.export.transcript;

import java.util.Comparator;

public class SemesterComparator implements Comparator<String> {

    @Override
    public int compare(String semester1, String semester2) {
        String yearString1 = semester1.replaceAll("[A-Za-z]", ""); // remove all letters
        String yearString2 = semester2.replaceAll("[A-Za-z]", "");

        // compare by year first
        int year1 = Integer.parseInt(yearString1);
        int year2 = Integer.parseInt(yearString2);

        if (year1 < year2) {
            return -1;  // semester1 comes before semester2
        } else if (year1 > year2) {
            return 1;   // semester2 comes before semester1
        }

        // if the year is the same, compare by term: Spring < Fall
        // (reverse alphabetical since "Fall" < "Spring" alphabetically)
        String termString1 = semester1.replaceAll("[0-9]", "");  // Remove numbers
        String termString2 = semester2.replaceAll("[0-9]", "");

        return -termString1.compareTo(termString2);
    }
}
