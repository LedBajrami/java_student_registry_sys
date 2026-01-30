package utils.export.transcript;

import utils.export.ExportableInterface;

import java.util.HashMap;
import java.util.Map;

public class TranscriptReportData implements ExportableInterface {
    private String studentId;
    private String studentName;
    private String level;
    private String semester;
    private String courseCode;
    private String courseName;
    private int credits;
    private String letterGrade;

    public TranscriptReportData(
            String studentId,
            String studentName,
            String level,
            String semester,
            String courseCode,
            String courseName,
            int credits,
            String letterGrade
    ) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.level = level;
        this.semester = semester;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.letterGrade = letterGrade;
    }

    @Override
    public String[] getCSVHeaders() {
        return new String[]{
                "Student ID", "Student Name", "Level", "Semester", "Course Code", "Course Name", "Credits", "Grade"
        };
    }

    @Override
    public String[] getCSVRow() {
        return new String[]{
                studentId,
                studentName,
                level,
                semester,
                courseCode,
                courseName,
                String.valueOf(credits),
                letterGrade
        };
    }

    @Override
    public Map<String, String> toJSONObject() {
        Map<String, String> map = new HashMap<>();
        map.put("studentId", studentId);
        map.put("studentName", studentName);
        map.put("level", level);
        map.put("semester", semester);
        map.put("courseCode", courseCode);
        map.put("courseName", courseName);
        map.put("credits", String.valueOf(credits));
        map.put("grade", letterGrade);
        return map;
    }

    public Map<String, String> toXMLObject() {
        return toJSONObject();  // XML uses same key-value structure as json
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getLevel() { return level; }
    public String getSemester() { return semester; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public String getLetterGrade() { return letterGrade; }
}

