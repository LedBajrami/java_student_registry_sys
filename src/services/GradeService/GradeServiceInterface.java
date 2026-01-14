package services.GradeService;

import entities.Course;
import entities.Grade;
import entities.Student;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface GradeServiceInterface {
    public void loadGrades(List<String> gradeLines, List<Grade> grades) throws IOException;

    public String findGrade(List<Grade> grades, Map<String, Student> students, Map<String, Course> courses, String studentId, String courseCode);
}
