package services.GradeService;

import entities.Course;
import entities.Grade;
import entities.Student;
import registry.DataRepository;
import utils.FileHandler;
import utils.GradeCalculator;
import utils.export.ExportFileHandler;
import utils.query.QueryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GradeService implements GradeServiceInterface {
    private final DataRepository dataRepository;
    private final ExportFileHandler exportableService;
    private final QueryService queryService;


    public GradeService(DataRepository dataRepository, ExportFileHandler exportableService, QueryService queryService) {
        this.dataRepository = dataRepository;
        this.exportableService = exportableService;
        this.queryService = queryService;
    }

    @Override
    public void loadGrades(List<String> gradeLines) throws IOException {
        List<Grade> grades = dataRepository.getGrades();

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
    public String findGrade(String studentId, String courseCode) {
        Map<String, Student> students = dataRepository.getStudents();
        Map<String, Course> courses = dataRepository.getCourses();
        List<Grade> grades = dataRepository.getGrades();

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
    public String queryGrade(String[] parametersArray) {
        Collection<Grade> grades = dataRepository.getGrades();

        return queryService.query(
                grades,
                parametersArray,
                (grade, fieldName) -> {
                    switch (fieldName) {
                        case "studentId": return grade.getStudentId();
                        case "courseCode": return grade.getCourseCode();
                        case "semester": return grade.getSemester();
                        case "grade": return String.valueOf(grade.getNumericGrade());
                        default: return null;
                    }
                }
        );
    }

    @Override
    public String addGrade(String[] parametersArray, String dataFolderPath) {
        Map<String, Student> students = dataRepository.getStudents();
        Map<String, Course> courses = dataRepository.getCourses();
        List<Grade> grades = dataRepository.getGrades();

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

            dataRepository.addGradeToStudent(studentId, grade);

            return "1 record added";
        } catch (IOException e) {
            return "error: data file not found or could not be written";
        } catch (IllegalArgumentException e) {
            return "error: " + e.getMessage();
        }
    }

}
