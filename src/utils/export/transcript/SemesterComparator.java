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

        // if the year is the same, then we compare by term Spring < Summer < Fall < Winter
        String termString1 = semester1.replaceAll("[0-9]", "");  // Remove numbers
        String termString2 = semester2.replaceAll("[0-9]", "");

        int term1 = getTermNumber(termString1);
        int term2 = getTermNumber(termString2);

        if (term1 < term2) {
            return -1;  // term1 comes before term2
        } else if (term1 > term2) {
            return 1;   // term2 comes before term1
        } else {
            return 0;   // They are equal
        }
    }

    private int getTermNumber(String term) {
        switch (term) {
            case "Spring": return 1;
            case "Summer": return 2;
            case "Fall": return 3;
            case "Winter": return 4;
            default: return 0;
        }
    }
}
