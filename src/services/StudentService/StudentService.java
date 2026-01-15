package services.StudentService;

import entities.Course;
import entities.Grade;
import entities.Level;
import entities.Student;
import utils.FileHandler;
import utils.GradeCalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentService implements StudentServiceInterface{
    @Override
    public void loadStudents(List<String> studentLines, Map<String, Student> students) throws IOException {
        for (String line : studentLines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(", ");
            if (parts.length < 5) {
                throw new IOException("Invalid student data: " + line);
            }

            String id = parts[0].trim();
            String name = parts[1].trim();
            String surname = parts[2].trim();
            String email = parts[3].trim();
            String levelStr = parts[4].trim();

            if (email.isEmpty()) {
                email = null;
            }

            // parse level from string to Level
            Level level = levelStr.equals("UG") ? Level.UG : Level.G;

            students.put(id, new Student(id, name, surname, email, level));
        }
    }

    @Override
    public String findStudent(Map<String, Student> students, Map<String, Course> courses, List<Grade> grades, String studentId) {
        if (!students.containsKey(studentId)) {
            return "no student found";
        }

        Student student = students.get(studentId);
        int totalCredits = 0;

        /* iterate to:
            1. populate the studentGrades list, to then pass for gpa calculation
            2. find the unique courses the student has taken
         */
        List<Grade> studentGrades = new ArrayList<>();
        List<String> uniqueCourses = new ArrayList<>();
        for (Grade studentGrade : grades) {
            // check to see if we have the correct student
            if (!studentGrade.getStudentId().equals(studentId)) {
                continue;
            }
            studentGrades.add(studentGrade);

            String courseCode = studentGrade.getCourseCode();
            if (!uniqueCourses.contains(courseCode)) {
                uniqueCourses.add(studentGrade.getCourseCode());
            }

            totalCredits += courses.get(courseCode).getCredits();
        }

        if (studentGrades.isEmpty()) {
            return "student has no grades";
        }

        String studentLevel = student.getLevel() == Level.UG ? "undergraduate" : "graduate";
        double gpa = GradeCalculator.calculateGPA(studentGrades, courses, student.getLevel());

        return String.format("id: %s\nname: %s\nsurname: %s\nemail: %s\nlevel: %s\ncourses: %d\ncourses: %d\ngpa: %.2f",
                student.getId(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                studentLevel,
                uniqueCourses.size(),
                totalCredits,
                gpa
        );
    }


    @Override
    public String queryStudent(Map<String, Student> students, String[] parametersArray) {
        ArrayList<Student> foundStudents = new ArrayList<>();

        for (Student student : students.values()) {
            boolean matches = true;

            for (String parameter : parametersArray) {

                if (parameter.contains("=")) {
                    String[] commandParts = parameter.split("=");
                    String key = commandParts[0].trim();
                    String value = commandParts[1].trim();

                    switch (key) {
                        case "id":
                            if (!student.getId().equals(value)) matches = false;
                            break;
                        case "name":
                            if (!student.getName().equals(value)) matches = false;
                            break;
                        case "surname":
                            if (!student.getSurname().equals(value)) matches = false;
                            break;
                        case "email":
                            if (!value.equals(student.getEmail())) matches = false;
                            break;
                        case "level":
                            // Convert string to Level enum, then compare
                            if (!student.getLevel().name().equals(value)) matches = false;
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
                        case "id":
                            if (!student.getId().contains(value)) matches = false;
                            break;
                        case "name":
                            if (!student.getName().contains(value)) matches = false;
                            break;
                        case "surname":
                            if (!student.getSurname().contains(value)) matches = false;
                            break;
                        case "email":
                            if (!value.equals(student.getEmail())) matches = false;
                            break;
                        case "level":
                            // Convert string to Level enum, then compare
                            if (!student.getLevel().name().contains(value)) matches = false;
                            break;
                        default:
                            matches = false;
                            break;
                    }

                }
            }
            if (matches) foundStudents.add(student);
        }

        StringBuilder studentsString = new StringBuilder();
        for (Student student : foundStudents) {
            studentsString.append(student.toString()).append("\n");
        }

        return String.format("%d records found\n%s", foundStudents.size(), studentsString);
    }

    @Override
    public String addStudent(Map<String, Student> students, String[] parametersArray, String dataFolderPath) {
        if (parametersArray.length != 5) {
            return "error: expected command - add student id, name, surname, email, level";
        }

        String id = parametersArray[0].trim();
        String name = parametersArray[1].trim();
        String surname = parametersArray[2].trim();
        String email = parametersArray[3].trim();
        String level = parametersArray[4].trim();

        if (id.isEmpty() || name.isEmpty() || surname.isEmpty() || level.isEmpty()) return "error: required field is empty. Expected command - add student id, name, surname, email, level";

        Level studentLevel;
        try {
            studentLevel = Level.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "error: level should be either G or UG";
        }

        if (students.containsKey(id)) return "error: student with id " + id + " is already present";

        try {
            Student student = new Student(id, name, surname, email, studentLevel);

            FileHandler.appendLines(dataFolderPath + "/students.txt", student.toString());
            students.put(id, student);

            return "1 record added";
        } catch (IOException e) {
            return "error: data file not found or could not be written";
        } catch (IllegalArgumentException e) {
            return "error: " + e.getMessage();
        }
    }

}
