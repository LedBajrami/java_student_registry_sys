package services.CourseService;

import entities.Course;
import entities.Grade;
import entities.Level;
import entities.Student;
import registry.DataRepository;
import utils.FileHandler;
import utils.export.ExportFileHandler;
import utils.export.course.CourseReportData;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CourseService implements CourseServiceInterface {
    private final DataRepository dataRepository;
    private final ExportFileHandler exportableService;

    public CourseService(DataRepository dataRepository, ExportFileHandler exportableService) {
        this.dataRepository = dataRepository;
        this.exportableService = exportableService;
    }

    @Override
    public void loadCourses(List<String> courseLines) throws IOException {
        Map<String, Course> courses = dataRepository.getCourses();

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
    public String findCourse(String courseCode) {
        Map<String, Course> courses = dataRepository.getCourses();

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
    public String queryCourse(String[] parametersArray) {
        Map<String, Course> courses = dataRepository.getCourses();

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

    @Override
    public String addCourse(String[] parametersArray, String dataFolderPath) {
        Map<String, Course> courses = dataRepository.getCourses();

        Map<String, String> params = new HashMap<>();

        for (String param : parametersArray) {
            String[] parts = param.split("=", 2);
            if (parts.length == 2) {
                params.put(parts[0].trim().toLowerCase(), parts[1].trim());
            }
        }

        // Now you KNOW exactly which fields are present
        String code = params.get("code");
        String title = params.get("title");
        String credits = params.get("credits");

        // Check specifically which ones are missing
        if (code == null) {
            return "error: code field missing";
        }
        if (title == null) {
            return "error: title field missing";
        }
        if (credits == null) {
            return "error: credits field missing";
        }

        if (parametersArray.length != 3) {
            return "error: expected command - add course code, title, credits";
        }

        if (credits.isEmpty()) return "error: credits field missing";
        if (title.isEmpty() || code.isEmpty()) return "error: required field is empty. Expected command - add course code, title, credits";

        int intCredits;
        try {
            intCredits = Integer.parseInt(credits);
        } catch (NumberFormatException e) {
            return "error: credits must be a number";
        }

        if (courses.containsKey(code)) return "error: course with code " + code + " is already present";

        try {
            Course course = new Course(code, title, intCredits);

            FileHandler.appendLines(dataFolderPath + "/courses.txt", course.toString());
            courses.put(code, course);

            return "1 record added";
        } catch (IOException e) {
            return "error: data file not found or could not be written";
        } catch (IllegalArgumentException e) {
            return "error: " + e.getMessage();
        }
    }

    public String reportTopCourses(int value, String fileName) throws IOException {
        Map<String, Course> courses = dataRepository.getCourses();
        List<Grade> grades = dataRepository.getGrades();

        // Group grades by course code
        Map<String, List<Grade>> gradesByCourse = grades.stream()
                .collect(Collectors.groupingBy(Grade::getCourseCode));

        // filter, sort...
        List<CourseReportData> topCourses = gradesByCourse.entrySet().stream()
                .map(courseEntry -> {
                    Course course = courses.get(courseEntry.getKey());
                    if (course == null) return null;

                    int filedGrades = courseEntry.getValue().size();

                    return new CourseReportData(course, filedGrades);
                })
                .filter(data -> data != null)
                .filter(data -> data.getFiledGrades() > 0)
                .sorted(Comparator.comparingInt(CourseReportData::getFiledGrades).reversed())
                .limit(value)
                .collect(Collectors.toList());

        return exportableService.exportTo(topCourses, fileName);
    }
}
