package services.StudentService;

import entities.Course;
import entities.Grade;
import entities.Level;
import entities.Student;
import registry.DataRepository;
import utils.FileHandler;
import utils.GradeCalculator;
import utils.export.ExportFileHandler;
import utils.export.student.StudentReportData;
import utils.export.transcript.SemesterComparator;
import utils.export.transcript.TranscriptReportData;
import utils.query.QueryService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StudentService implements StudentServiceInterface{
    private final DataRepository dataRepository;
    private final ExportFileHandler exportableService;
    private final QueryService queryService;


    public StudentService(DataRepository dataRepository, ExportFileHandler exportableService, QueryService queryService) {
        this.dataRepository = dataRepository;
        this.exportableService = exportableService;
        this.queryService = queryService;
    }

    @Override
    public void loadStudents(List<String> studentLines) throws IOException {
        Map<String, Student> students = dataRepository.getStudents();

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
    public String findStudent(String studentId) {
        Map<String, Student> students = dataRepository.getStudents();
        Map<String, Course> courses = dataRepository.getCourses();
        Map<String, List<Grade>> gradesPerStudent = dataRepository.getGradesPerStudent();

        if (!students.containsKey(studentId)) {
            return "no student found";
        }

        if (!gradesPerStudent.containsKey(studentId)) {
            return "student has no grades";
        }

        Student student = students.get(studentId);

        List<Grade> studentGrades = gradesPerStudent.get(studentId);
        int totalCredits = studentGrades.stream().
                mapToInt(grade ->
                courses.get(grade.getCourseCode()).getCredits())
                .sum();
        int coursesTaken = studentGrades.size();


        String studentLevel = student.getLevel() == Level.UG ? "undergraduate" : "graduate";
        double gpa = GradeCalculator.calculateGPA(studentGrades, courses, student.getLevel());

        return String.format("id: %s\nname: %s\nsurname: %s\nemail: %s\nlevel: %s\ncourses: %d\ntotalCredits: %d\ngpa: %.2f",
                student.getId(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                studentLevel,
                coursesTaken,
                totalCredits,
                gpa
        );
    }

    @Override
    public String queryStudent(String[] parametersArray) {
        Collection<Student> students = dataRepository.getStudents().values();

        return queryService.query(
                students,
                parametersArray,
                (student, field) -> {
                    switch (field) {
                        case "id": return student.getId();
                        case "name": return student.getName();
                        case "surname": return student.getSurname();
                        case "email": return student.getEmail();
                        case "level": return student.getLevel().name();
                        default: return null;
                    }
                }
        );
    }

    @Override
    public String addStudent(String[] parametersArray, String dataFolderPath) {
        Map<String, Student> students = dataRepository.getStudents();

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

    @Override
    public String reportTopStudents(int value, String fileName) throws IOException {
        Map<String, Student> students = dataRepository.getStudents();
        Map<String, Course> courses = dataRepository.getCourses();
        List<Grade> grades = dataRepository.getGrades();
        Map<String, List<Grade>> gradesPerStudent = dataRepository.getGradesPerStudent();

        List<StudentReportData> bestStudents = gradesPerStudent.entrySet().stream()
                .map(entryStudent -> {
                    Student student = students.get(entryStudent.getKey());
                    if (student == null) return null;


                    double gpa = GradeCalculator.calculateGPA(entryStudent.getValue(), courses, student.getLevel());
                    int totalCredits =  entryStudent.getValue().stream()
                            .mapToInt(grade ->
                                    courses.get(grade.getCourseCode()).getCredits()
                            ).sum();

                    return new StudentReportData(student, gpa, totalCredits);
                })
                .filter(data -> data != null && data.getGpa() > 0.0 && data.getTotalCredits() > 0) // filter 0 gpa and credits to not mess up sorting
                .sorted(Comparator.comparingDouble(StudentReportData::getGpa).reversed()
                        .thenComparingInt(StudentReportData::getTotalCredits))
                .limit(value)
                .collect(Collectors.toList());

        return exportableService.exportTo(bestStudents, fileName);
    }

    @Override
    public String reportTranscript(String studentId, String fileName) throws IOException {
        Map<String, Student> students = dataRepository.getStudents();
        Map<String, Course> courses = dataRepository.getCourses();
        Map<String, List<Grade>> gradesPerStudent = dataRepository.getGradesPerStudent();

        Student student = students.get(studentId);
        if (student == null) {
            return "error: no student found for given id";
        }

        List<Grade> studentGrades = gradesPerStudent.get(studentId);
        if (studentGrades.isEmpty()) {
            return "error: no grades found for student " + studentId;
        }

        List<TranscriptReportData> transcriptData = studentGrades.stream()
                .map(grade -> {
                    Course course = courses.get(grade.getCourseCode());
                    if (course == null) return null;

                    return new TranscriptReportData(
                            student.getId(),
                            student.getName(),
                            student.getLevel().toString(),
                            grade.getSemester(),
                            course.getCode(),
                            course.getTitle(),
                            course.getCredits(),
                            String.valueOf(grade.getNumericGrade())
                    );
                })
                .filter(data -> data != null)
                .sorted(Comparator.comparing(
                        TranscriptReportData::getSemester,
                        new SemesterComparator()
                ))
                .collect(Collectors.toList());

        return exportableService.exportTo(transcriptData, fileName);
    }
}
