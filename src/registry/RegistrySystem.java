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
import utils.export.ExportFileHandler;
import utils.export.course.CourseReportData;
import utils.export.student.StudentReportData;
import utils.export.transcript.SemesterComparator;
import utils.export.transcript.TranscriptReportData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class RegistrySystem {

    private Map<String, Student> students;
    private Map<String, Course> courses;
    private List<Grade> grades;
    private boolean dataLoaded;
    private String dataFolderPath;

    // service dependencies declaration
    private StudentServiceInterface studentService;
    private CourseServiceInterface courseService;
    private GradeServiceInterface gradeService;
    private ExportFileHandler exportableService;


    public RegistrySystem() {
        this.students = new HashMap<>();
        this.courses = new HashMap<>();
        this.grades = new ArrayList<>();
        this.dataLoaded = false;

        this.exportableService = new ExportFileHandler();
        this.studentService = new StudentService(exportableService);
        this.courseService = new CourseService(exportableService);
        this.gradeService = new GradeService(exportableService);
    }

    // --- LOAD METHOD ---
    public String loadFile(String folderPath) throws IOException {
        // check if loaded first
        checkIfDataLoadedLoadCommand();

        // validate folder exists
        Path path = Paths.get(folderPath);
        this.dataFolderPath = folderPath;

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IOException("invalid folder name");
        }

        // check if all data files exist
        String studentFile = folderPath + "/students.txt";
        String courseFile = folderPath + "/courses.txt";
        String gradeFile = folderPath + "/grades.txt";

        if (!Files.exists(Paths.get(studentFile)) ||
                !Files.exists(Paths.get(courseFile)) ||
                !Files.exists(Paths.get(gradeFile))) {
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



    // --- FIND METHOD ---
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



    // --- QUERY METHOD ---
    public String queryCourse(String[] parametersArray) {
        checkIfDataLoaded();
        return courseService.queryCourse(courses, parametersArray);
    }

    public String queryStudent(String[] parametersArray) {
        checkIfDataLoaded();
        return studentService.queryStudent(students, parametersArray);
    }

    public String queryGrade(String[] parametersArray) {
        checkIfDataLoaded();
        return gradeService.queryGrade(grades, parametersArray);
    }



    // --- ADD METHOD ---
    public String addCourse(String[] parametersArray) {
        checkIfDataLoaded();
        return courseService.addCourse(courses, parametersArray, dataFolderPath);
    }

    public String addStudent(String[] parametersArray) {
        checkIfDataLoaded();
        return studentService.addStudent(students, parametersArray, dataFolderPath);
    }

    public String addGrade(String[] parametersArray) {
        checkIfDataLoaded();
        return gradeService.addGrade(grades, students, courses, parametersArray, dataFolderPath);
    }



    // --- REPORT METHOD ---
    public String reportTopCourses(int value, String fileName) throws IOException {
        checkIfDataLoaded();
        return courseService.reportTopCourses(courses, grades, value, fileName);
    }

    public String reportTopStudents(int value, String fileName) throws IOException {
        checkIfDataLoaded();
        return studentService.reportTopStudents(students, courses, grades, value, fileName);
    }

    public String reportTranscript(String studentId, String fileName) throws IOException {
        checkIfDataLoaded();
        return studentService.reportTranscript(students, courses, grades, studentId, fileName);
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