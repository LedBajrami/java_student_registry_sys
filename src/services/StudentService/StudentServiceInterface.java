package services.StudentService;

import entities.Course;
import entities.Grade;
import entities.Student;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface StudentServiceInterface {
    public void loadStudents(List<String> studentLines, Map<String, Student> students) throws IOException;

    public String findStudent(Map<String, Student> students, Map<String, Course> courses, List<Grade> grades, String studentId);
}
