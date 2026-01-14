package services.StudentService;

import entities.Course;
import entities.Grade;
import entities.Level;
import entities.Student;
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

}
