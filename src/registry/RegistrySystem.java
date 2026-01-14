package registry;

import entities.*;
import services.CourseService.CourseService;
import services.CourseService.CourseServiceInterface;
import services.GradeService.GradeService;
import services.GradeService.GradeServiceInterface;
import services.StudentService.StudentService;
import services.StudentService.StudentServiceInterface;
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

    // service dependencies declaration
    private StudentServiceInterface studentService;
    private CourseServiceInterface courseService;
    private GradeServiceInterface gradeService;

    public RegistrySystem() {
        this.students = new HashMap<>();
        this.courses = new HashMap<>();
        this.grades = new ArrayList<>();
        this.dataLoaded = false;

        this.studentService = new StudentService();
        this.courseService = new CourseService();
        this.gradeService = new GradeService();
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
        studentService.loadStudents(studentLines, students);

        // load courses
        List<String> courseLines = FileHandler.readLines(courseFile);
        courseService.loadCourses(courseLines, courses);

        // load grades
        List<String> gradeLines = FileHandler.readLines(gradeFile);
        gradeService.loadGrades(gradeLines, grades);

        dataLoaded = true;
        String successMessage = "loaded " + students.size() + " students, " + courses.size() + " courses, and " + grades.size() + " grades";
        return successMessage;
    }

    public String findCourse(String courseCode) {
        checkIfDataLoaded();
        return courseService.findCourse(courses, courseCode);
    }

    public String findStudent(String studentId) {
        checkIfDataLoaded();
        return studentService.findStudent(students, courses, grades, studentId);
    }

    public String findGrade(String studentId, String courseCode) {
        checkIfDataLoaded();
        return gradeService.findGrade(grades, students, courses, studentId, courseCode);
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