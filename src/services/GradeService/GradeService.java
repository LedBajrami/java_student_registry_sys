package services.GradeService;

import entities.Course;
import entities.Grade;
import entities.Student;
import utils.GradeCalculator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GradeService implements GradeServiceInterface {
    @Override
    public void loadGrades(List<String> gradeLines, List<Grade> grades) throws IOException {
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
    public String findGrade(List<Grade> grades, Map<String, Student> students, Map<String, Course> courses, String studentId, String courseCode) {
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

}
