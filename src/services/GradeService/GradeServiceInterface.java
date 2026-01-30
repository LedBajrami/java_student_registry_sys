package services.GradeService;

import entities.Course;
import entities.Grade;
import entities.Student;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface GradeServiceInterface {
    public void loadGrades(List<String> gradeLines) throws IOException;

    public String findGrade(String studentId, String courseCode);

    public String queryGrade(String[] parametersArray);

    public String addGrade(String[] parametersArray, String dataFolderPath);
}


