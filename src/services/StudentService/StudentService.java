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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentService implements StudentServiceInterface{
    private final DataRepository dataRepository;
    private final ExportFileHandler exportableService;


    public StudentService(DataRepository dataRepository, ExportFileHandler exportableService) {
        this.dataRepository = dataRepository;
        this.exportableService = exportableService;
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
        Map<String, Student> students = dataRepository.getStudents();
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
