package entities;

public class Grade {
    private final String studentId;
    private final String courseCode;
    private final String semester;
    private final int numericGrade;

    public Grade(String studentId, String courseCode, String semester, int numericGrade) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        }
        this.studentId = studentId.trim();

        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        this.courseCode = courseCode.trim();

        if (!isValidSemesterFormat(semester)) {
            throw new IllegalArgumentException("Invalid semester format");
        }
        this.semester = semester;

        if (numericGrade < 0 || numericGrade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
        this.numericGrade = numericGrade;
    }

    private boolean isValidSemesterFormat(String semester) {
        if (semester == null) return false;

        if (semester.startsWith("Spring") && semester.length() == 10) {
            return semester.substring(6).matches("\\d{4}");
        } else if (semester.startsWith("Fall") && semester.length() == 8) {
            return semester.substring(4).matches("\\d{4}");
        }
        return false;
    }

    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public String getSemester() { return semester; }
    public int getNumericGrade() { return numericGrade; }

    @Override
    public String toString() {
        return studentId + ", " + courseCode + ", " + semester + ", " + numericGrade;
    }
}