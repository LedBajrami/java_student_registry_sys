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

    public String queryStudent(Map<String, Student> students, String[] parametersArray);

    public String addStudent(Map<String, Student> students, String[] parametersArray, String dataFolderPath);

    public String reportTopStudents(Map<String, Student> students, Map<String, Course> courses, List<Grade> grades, int value, String fileName) throws IOException;

    public String reportTranscript(Map<String, Student> students, Map<String, Course> courses, List<Grade> grades, String studentId, String fileName) throws IOException;
}
