package services.CourseService;

import entities.Course;
import entities.Level;

import java.io.IOException;
import java.util.ArrayList;
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

    @Override
    public String queryCourse(Map<String, Course> courses, String[] parametersArray) {
        ArrayList<Course> foundCourses = new ArrayList<>();

        for (Course course : courses.values()) {
            boolean matches = true;

            for (String parameter : parametersArray) {

                if (parameter.contains("=")) {
                    String[] commandParts = parameter.split("=");
                    String key = commandParts[0].trim();
                    String value = commandParts[1].trim();

                    switch (key) {
                        case "title":
                            if (!course.getTitle().equals(value)) matches = false;
                            break;
                        case "code":
                            if (!course.getCode().equals(value)) matches = false;
                            break;
                        case "credits":
                            // convert the credits to string to avoid problems
                            if (!String.valueOf(course.getCredits()).equals(value)) matches = false;
                            break;
                        default:
                            matches = false;
                            break;
                    }


                } else if (parameter.contains("~")) {
                    String[] commandParts = parameter.split("~");
                    String key = commandParts[0].trim();
                    String value = commandParts[1].trim();

                    switch (key) {
                        case "title":
                            if (!course.getTitle().contains(value)) matches = false;
                            break;
                        case "code":
                            if (!course.getCode().contains(value)) matches = false;
                            break;
                        case "credits":
                            // convert the credits to string to avoid problems
                            if (!String.valueOf(course.getCredits()).contains(value)) matches = false;
                            break;
                        default:
                            matches = false;
                            break;
                    }
                }
            }
            if (matches) foundCourses.add(course);
        }

        StringBuilder coursesString = new StringBuilder();
        for (Course course : foundCourses) {
            coursesString.append(course.toString()).append("\n");
        }

        return String.format("%d records found\n%s", foundCourses.size(), coursesString);
    }
}
