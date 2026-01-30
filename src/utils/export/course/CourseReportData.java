package utils.export.course;

import entities.Course;
import utils.export.ExportableInterface;

import java.util.HashMap;
import java.util.Map;

public class CourseReportData implements ExportableInterface {
    private Course course;
    private int filedGrades;

    public CourseReportData(Course course, int filedGrades) {
        this.course = course;
        this.filedGrades = filedGrades;
    }

    @Override
    public String[] getCSVHeaders() {
        return new String[]{"code", "title", "credits", "filedGrades"};
    }

    @Override
    public String[] getCSVRow() {
        return new String[]{
                course.getCode(),
                course.getTitle(),
                String.valueOf(course.getCredits()),
                String.valueOf(filedGrades)
        };
    }

    @Override
    public Map<String, String> toJSONObject() {
        Map<String, String> map = new HashMap<>();
        map.put("code", course.getCode());
        map.put("title", course.getTitle());
        map.put("credits", String.valueOf(course.getCredits()));
        map.put("filedGrades", String.valueOf(filedGrades));
        return map;
    }

    public Map<String, String> toXMLObject() {
        return toJSONObject();  // XML uses same key-value structure as json
    }


    public int getFiledGrades() { return filedGrades; }

    public Course getCourse() { return course; }
}
