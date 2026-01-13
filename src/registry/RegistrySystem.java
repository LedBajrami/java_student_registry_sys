package registry;

import entities.*;
import utils.FileHandler;

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
        if (dataLoaded) {
            throw new IOException("data already loaded, cannot load again!");
        }

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
}