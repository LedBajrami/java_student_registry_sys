package services.CourseService;

import entities.Course;
import entities.Level;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CourseService implements CourseServiceInterface {
    @Override
    public void loadCourses(List<String> courseLines, Map<String, Course> courses) throws IOException {
        for (String line : courseLines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(", ");
            if (parts.length < 3) {
                throw new IOException("Invalid course data: " + line);
            }

            String code = parts[0].trim();
            String title = parts[1].trim();
            int credits = Integer.parseInt(parts[2].trim());

            courses.put(code, new Course(code, title, credits));
        }
    }

    @Override
    public String findCourse(Map<String, Course> courses, String courseCode) {
        if (!courses.containsKey(courseCode)) {
            return "no course found";
        }

        Course course = courses.get(courseCode);
        String courseLevel = course.getLevel() == Level.UG ? "undergraduate" : "graduate";
        return String.format("code: %s\ntitle: %s\ncredits: %d\nlevel: %s",
                course.getCode(),
                course.getTitle(),
                course.getCredits(),
                courseLevel
        );
    }
}
