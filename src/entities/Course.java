package entities;

public class Course {
    private final String code;
    private final String title;
    private final int credits;
    private final Level level;

    public Course(String code, String title, int credits) {
        if (code == null || !code.matches("[A-Z]{2,4}[0-9]{3}")) {
            throw new IllegalArgumentException("Invalid course code format");
        }
        this.code = code;

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title.trim();

        if (credits < 2 || credits > 4) {
            throw new IllegalArgumentException("Credits must be 2, 3, or 4");
        }
        this.credits = credits;

        this.level = computeLevelFromCode(code);
    }

    private Level computeLevelFromCode(String code) {
        char firstDigit = code.charAt(code.length() - 3);
        if (firstDigit >= '1' && firstDigit <= '4') {
            return Level.UG;
        } else {
            return Level.G;
        }
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public Level getLevel() { return level; }

    @Override
    public String toString() {
        return code + ", " + title + ", " + credits;
    }
}