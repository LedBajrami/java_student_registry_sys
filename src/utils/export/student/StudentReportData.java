package utils.export.student;

import entities.Student;
import utils.export.ExportableInterface;

import java.util.HashMap;
import java.util.Map;

public class StudentReportData implements ExportableInterface {
    private Student student;
    private double gpa;
    private int totalCredits;

    public StudentReportData(Student student, double gpa, int totalCredits) {
        this.student = student;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
    }

    @Override
    public String[] getCSVHeaders() {
        return new String[]{"id", "name", "surname", "email", "level", "gpa", "credits"};
    }

    @Override
    public String[] getCSVRow() {
        return new String[]{
                student.getId(),
                student.getName(),
                student.getSurname(),
                student.getEmail() != null ? student.getEmail() : "",
                student.getLevel().toString(),
                String.format("%.2f", gpa),
                String.valueOf(totalCredits)
        };
    }

    @Override
    public Map<String, String> toJSONObject() {
        Map<String, String> map = new HashMap<>();
        map.put("id", student.getId());
        map.put("name", student.getName());
        map.put("surname", student.getSurname());
        map.put("email", student.getEmail() != null ? student.getEmail() : "");
        map.put("level", student.getLevel().toString());
        map.put("gpa", String.format("%.2f", gpa));
        map.put("totalCredits", String.valueOf(totalCredits));
        return map;
    }

    @Override
    public Map<String, String> toXMLObject() {
        return toJSONObject();  // XML uses same key-value structure as json
    }

    public double getGpa() { return gpa; }


    public int getTotalCredits() { return totalCredits; }

    public Student getStudent() { return student; }
}

