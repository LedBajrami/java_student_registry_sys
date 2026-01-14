package registry;

import entities.*;
import utils.FileHandler;
import utils.GradeCalculator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


public class RegistrySystem {

    private Map<String, Student> students;
    private Map<String, Course> courses;
    private List<Grade> grades;
    private boolean dataLoaded;

    public RegistrySystem() {
        this.students = new HashMap<>();
        this.courses = new HashMap<>();
        this.grades = new ArrayList<>();
        this.dataLoaded = false;
    }

    public String loadFile(String folderPath) throws IOException {
        // check if loaded first
        checkIfDataLoadedLoadCommand();

        // validate folder exists
        java.nio.file.Path path = java.nio.file.Paths.get(folderPath);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IOException("invalid folder name");
        }

        // check if all data files exist
        String studentFile = folderPath + "/students.txt";
        String courseFile = folderPath + "/courses.txt";
        String gradeFile = folderPath + "/grades.txt";

        if (!Files.exists(java.nio.file.Paths.get(studentFile)) ||
                !Files.exists(java.nio.file.Paths.get(courseFile)) ||
                !Files.exists(java.nio.file.Paths.get(gradeFile))) {
            throw new IOException("data files not found");
        }

        // load students
        List<String> studentLines = FileHandler.readLines(studentFile);
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

        // load courses
        List<String> courseLines = FileHandler.readLines(courseFile);
        for (String line : courseLines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(", ");
            if (parts.length < 3) {
                throw new IOException("Invalid course data: " + line);
            }

            String code = parts[0].trim();
            String title = parts[1].trim();
            int credits = Integer.parseInt(parts[2].trim());

            courses.put(code, new Course(code, title, credits));
        }

        // load grades
        List<String> gradeLines = FileHandler.readLines(gradeFile);
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

        dataLoaded = true;
        String successMessage = "loaded " + students.size() + " students, " + courses.size() + " courses, and " + grades.size() + " grades";
        return successMessage;
    }

    public String findCourse(String courseCode) {
        checkIfDataLoaded();

        if (!courses.containsKey(courseCode)) {
            return "no course found";
        }

        Course course = courses.get(courseCode);
        String courseLevel = course.getLevel() == Level.UG ? "undergraduate" : "graduate";
        return String.format("code: %s\ntitle: %s\ncredits: %d\nlevel: %s",
                course.getCode(),
                course.getTitle(),
                course.getCredits(),
                courseLevel
        );
    }

    public String findStudent(String studentId) {
        checkIfDataLoaded();

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

    public String findGrade(String studentId, String courseCode) {
        checkIfDataLoaded();

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


    private void checkIfDataLoaded() {
        if (!dataLoaded) {
            throw new IllegalStateException("Please load the data first!");
        }
    }

    // for load command
    private void checkIfDataLoadedLoadCommand() {
        if (dataLoaded) {
            throw new IllegalStateException("data already loaded, cannot load again!");
        }
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }
}