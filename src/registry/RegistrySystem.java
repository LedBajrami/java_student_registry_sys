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

    private DataRepository dataRepository;
    private boolean dataLoaded;
    private String dataFolderPath;

    // service dependencies declaration
    private StudentServiceInterface studentService;
    private CourseServiceInterface courseService;
    private GradeServiceInterface gradeService;
    private ExportFileHandler exportableService;


    public RegistrySystem() {
        this.dataRepository = new DataRepository();
        this.dataLoaded = false;

        this.exportableService = new ExportFileHandler();
        this.studentService = new StudentService(dataRepository, exportableService);
        this.courseService = new CourseService(dataRepository, exportableService);
        this.gradeService = new GradeService(dataRepository, exportableService);
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
        studentService.loadStudents(studentLines);

        // load courses
        List<String> courseLines = FileHandler.readLines(courseFile);
        courseService.loadCourses(courseLines);

        // load grades
        List<String> gradeLines = FileHandler.readLines(gradeFile);
        gradeService.loadGrades(gradeLines);

        // build grades per student
        dataRepository.buildGradesPerStudent();

        dataLoaded = true;
        String successMessage = "loaded " + dataRepository.getStudents().size() + " students, " + dataRepository.getCourses().size() + " courses, and " + dataRepository.getGrades().size() + " grades";
        return successMessage;
    }



    // --- FIND METHOD ---
    public String findCourse(String courseCode) {
        checkIfDataLoaded();
        return courseService.findCourse(courseCode);
    }

    public String findStudent(String studentId) {
        checkIfDataLoaded();
        return studentService.findStudent(studentId);
    }

    public String findGrade(String studentId, String courseCode) {
        checkIfDataLoaded();
        return gradeService.findGrade(studentId, courseCode);
    }



    // --- QUERY METHOD ---
    public String queryCourse(String[] parametersArray) {
        checkIfDataLoaded();
        return courseService.queryCourse(parametersArray);
    }

    public String queryStudent(String[] parametersArray) {
        checkIfDataLoaded();
        return studentService.queryStudent(parametersArray);
    }

    public String queryGrade(String[] parametersArray) {
        checkIfDataLoaded();
        return gradeService.queryGrade(parametersArray);
    }



    // --- ADD METHOD ---
    public String addCourse(String[] parametersArray) {
        checkIfDataLoaded();
        return courseService.addCourse(parametersArray, dataFolderPath);
    }

    public String addStudent(String[] parametersArray) {
        checkIfDataLoaded();
        return studentService.addStudent(parametersArray, dataFolderPath);
    }

    public String addGrade(String[] parametersArray) {
        checkIfDataLoaded();
        return gradeService.addGrade(parametersArray, dataFolderPath);
    }



    // --- REPORT METHOD ---
    public String reportTopCourses(int value, String fileName) throws IOException {
        checkIfDataLoaded();
        return courseService.reportTopCourses(value, fileName);
    }

    public String reportTopStudents(int value, String fileName) throws IOException {
        checkIfDataLoaded();
        return studentService.reportTopStudents(value, fileName);
    }

    public String reportTranscript(String studentId, String fileName) throws IOException {
        checkIfDataLoaded();
        return studentService.reportTranscript(studentId, fileName);
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