package services.CourseService;

import entities.Course;
import entities.Grade;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CourseServiceInterface {
    public void loadCourses(List<String> courseLines) throws IOException;

    public String findCourse(String courseCode);

    public String queryCourse(String[] parametersArray);

    public String addCourse(String[] parametersArray, String dataFolderPath);

    public String reportTopCourses(int value, String fileName) throws IOException;
}
