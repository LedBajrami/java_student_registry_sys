package services.GradeService;

import entities.Course;
import entities.Grade;
import entities.Student;
import utils.FileHandler;
import utils.GradeCalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradeService implements GradeServiceInterface {
    @Override
    public void loadGrades(List<String> gradeLines, List<Grade> grades) throws IOException {
        for (String line : gradeLines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(", ");
            if (parts.length < 4) {
                throw new IOException("Invalid grade data: " + line);
            }

            String studentId = parts[0].trim();
            String courseCode = parts[1].trim();
            String semester = parts[2].trim();
            int numericGrade = Integer.parseInt(parts[3].trim());

            grades.add(new Grade(studentId, courseCode, semester, numericGrade));
        }
    }

    @Override
    public String findGrade(List<Grade> grades, Map<String, Student> students, Map<String, Course> courses, String studentId, String courseCode) {
        Grade studentGrade = null;
        for (Grade grade : grades) {
            if (grade.getStudentId().equals(studentId) &&
                    grade.getCourseCode().equals(courseCode))
            {
                studentGrade = grade;
                break;
            }
        }

        if (studentGrade == null) {
            return "no grade found";
        }

        Course course = courses.get(courseCode);
        return String.format("student: (%s - %s)\ncourse: (%s - %s, %d cr.)\nsemseter: %s\ngrade: %d\nletterGrade: %s",
                studentId,
                students.get(studentId).getFullName(),
                courseCode,
                course.getTitle(),
                course.getCredits(),
                studentGrade.getSemester(),
                studentGrade.getNumericGrade(),
                GradeCalculator.getLetterGrade(studentGrade.getNumericGrade(), students.get(studentId).getLevel())
        );
    }

    @Override
    public String queryGrade(List<Grade> grades, String[] parametersArray) {
        ArrayList<Grade> foundGrades = new ArrayList<>();

        for (Grade grade : grades) {
            boolean matches = true;

            for (String parameter : parametersArray) {

                if (parameter.contains("=")) {
                    String[] commandParts = parameter.split("=");
                    String key = commandParts[0].trim();
                    String value = commandParts[1].trim();

                    switch (key) {
                        case "studentId":
                            if (!grade.getStudentId().equals(value)) matches = false;
                            break;
                        case "courseCode":
                            if (!grade.getCourseCode().equals(value)) matches = false;
                            break;
                        case "semester":
                            if (!grade.getSemester().equals(value)) matches = false;
                            break;
                        case "grade":
                            if (!String.valueOf(grade.getNumericGrade()).equals(value)) matches = false;
                            break;
                        default:
                            matches = false;
                            break;
                    }

                } else  if (parameter.contains("~")) {
                    String[] commandParts = parameter.split("~");
                    String key = commandParts[0].trim();
                    String value = commandParts[1].trim();

                    switch (key) {
                        case "studentId":
                            if (!grade.getStudentId().contains(value)) matches = false;
                            break;
                        case "courseCode":
                            if (!grade.getCourseCode().contains(value)) matches = false;
                            break;
                        case "semester":
                            if (!grade.getSemester().contains(value)) matches = false;
                            break;
                        case "grade":
                            if (!String.valueOf(grade.getNumericGrade()).contains(value)) matches = false;
                            break;
                        default:
                            matches = false;
                            break;
                    }

                }
            }
            if (matches) foundGrades.add(grade);
        }

        StringBuilder gradesString = new StringBuilder();
        for (Grade grade : foundGrades) {
            gradesString.append(grade.toString()).append("\n");
        }

        return String.format("%d records found\n%s", foundGrades.size(), gradesString);
    }

    @Override
    public String addGrade(List<Grade> grades, Map<String, Student> students, Map<String, Course> courses, String[] parametersArray, String dataFolderPath) {
        if (parametersArray.length != 4) {
            return "error: expected command - add grade studentId, courseCode, semester, grade";
        }

        String studentId = parametersArray[0].trim();
        String courseCode = parametersArray[1].trim();
        String semester = parametersArray[2].trim();
        String gradeStr = parametersArray[3].trim();


        if (studentId.isEmpty() || courseCode.isEmpty() || semester.isEmpty() || gradeStr.isEmpty()) return "error: required field is empty. Expected command - add student id, name, surname, email, level";

        int gradeInt;
        try {
            gradeInt = Integer.parseInt(gradeStr);
        } catch (NumberFormatException e) {
            return "error: grade must be a number";
        }

        // Check if the specified student and course exists first of all
        if (!students.containsKey(studentId)) {
            return "error: student with id " + studentId + " does not exist";
        }
        if (!courses.containsKey(courseCode)) {
            return "error: course with code " + courseCode + " does not exist";
        }

        for (Grade g : grades) {
            if (studentId.equals(g.getStudentId()) && courseCode.equals(g.getCourseCode())) {
                return "error: grade for (" + studentId + ", " + courseCode + ") is already present";
            }
        }

        try {
            Grade grade = new Grade(studentId, courseCode, semester, gradeInt);

            FileHandler.appendLines(dataFolderPath + "/grades.txt", grade.toString());
            grades.add(grade);

            return "1 record added";
        } catch (IOException e) {
            return "error: data file not found or could not be written";
        } catch (IllegalArgumentException e) {
            return "error: " + e.getMessage();
        }
    }

}
