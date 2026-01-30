package services.StudentService;

import entities.Course;
import entities.Grade;
import entities.Student;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface StudentServiceInterface {
    public void loadStudents(List<String> studentLines) throws IOException;

    public String findStudent(String studentId);

    public String queryStudent(String[] parametersArray);

    public String addStudent(String[] parametersArray, String dataFolderPath);

    public String reportTopStudents(int value, String fileName) throws IOException;

    public String reportTranscript(String studentId, String fileName) throws IOException;
}
