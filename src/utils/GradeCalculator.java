package utils;
import entities.Course;
import entities.Grade;
import entities.Level;

import java.util.List;
import java.util.Map;

public class GradeCalculator {

    public static String getLetterGrade(int numericGrade, Level studentLevel) {
        if (studentLevel == Level.UG) {
            if (numericGrade < 60) return "F";
            if (numericGrade < 64) return "D-";
            if (numericGrade < 67) return "D";
            if (numericGrade < 70) return "D+";
            if (numericGrade < 74) return "C-";
            if (numericGrade < 77) return "C";
            if (numericGrade < 80) return "C+";
            if (numericGrade < 84) return "B-";
            if (numericGrade < 87) return "B";
            if (numericGrade < 90) return "B+";
            if (numericGrade < 95) return "A-";
            return "A";
        } else { // Graduate
            if (numericGrade < 75) return "F";
            if (numericGrade < 80) return "C";
            if (numericGrade < 85) return "B";
            if (numericGrade < 90) return "B+";
            return "A";
        }
    }

    public static double getPointValue(String letterGrade) {
        switch (letterGrade) {
            case "F": return 0.00;
            case "D-": return 0.67;
            case "D": return 1.00;
            case "D+": return 1.33;
            case "C-": return 1.67;
            case "C": return 2.00;
            case "C+": return 2.33;
            case "B-": return 2.67;
            case "B": return 3.00;
            case "B+": return 3.33;
            case "A-": return 3.67;
            case "A": return 4.00;
            default: return 0.00;
        }
    }
}