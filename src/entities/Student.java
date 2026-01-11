package entities;

public class Student {
    private final String id;
    private final String name;
    private final String surname;
    private final String email; // Can be null or empty
    private final Level level;

    public Student(String id, String name, String surname, String email, Level level) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        }
        this.id = id.trim();

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name.trim();

        if (surname == null || surname.trim().isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be empty");
        }
        this.surname = surname.trim();

        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            this.email = email.trim();
        } else {
            this.email = null;
        }

        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }
        this.level = level;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public Level getLevel() { return level; }

    public String getFullName() {
        return name + " " + surname;
    }

    @Override
    public String toString() {
        String emailStr = (email != null && !email.isEmpty()) ? email : "";
        return id + ", " + name + ", " + surname + ", " + emailStr + ", " + level;
    }
}
