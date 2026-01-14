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

    public static double calculateGPA(List<Grade> studentGrades, Map<String, Course> courses, Level studentLevel) {
        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Grade studentGrade : studentGrades) {
            Course course = courses.get(studentGrade.getCourseCode());

            String letterGrade = getLetterGrade(studentGrade.getNumericGrade(), studentLevel);

            double pointValue = getPointValue(letterGrade);

            totalPoints += course.getCredits() * pointValue;
            totalCredits += course.getCredits();
        }

        if (totalCredits == 0) return 0.0;
        return totalPoints / totalCredits;
    }
}