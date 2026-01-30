package registry;

import entities.Course;
import entities.Grade;
import entities.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataRepository {
    private Map<String, Student> students = new HashMap<>();
    private Map<String, Course> courses = new HashMap<>();
    private List<Grade> grades = new ArrayList<>();
    private Map<String, List<Grade>> gradesPerStudent = new HashMap<>();

    public Map<String, Student> getStudents() {
        return students;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public Map<String, List<Grade>> getGradesPerStudent() {
        return gradesPerStudent;
    }

    // this helps in performance in some commands, since no need to group every time, we just get the data from here
    public void buildGradesPerStudent() {
        this.gradesPerStudent = grades.stream()
                .collect(Collectors.groupingBy(Grade::getStudentId));
    }
}
