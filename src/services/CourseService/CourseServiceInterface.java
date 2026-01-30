package services.CourseService;

import entities.Course;
import entities.Grade;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CourseServiceInterface {
    public void loadCourses(List<String> courseLines, Map<String, Course> courses) throws IOException;

    public String findCourse(Map<String, Course> courses, String courseCode);

    public String queryCourse(Map<String, Course> courses, String[] parametersArray);

    public String addCourse(Map<String, Course> courses, String[] parametersArray, String dataFolderPath);

    public String reportTopCourses(Map<String, Course> courses, List<Grade> grades, int value, String fileName) throws IOException;
}
