package demo.android.com.simpson;

/**
 * Created by Admin on 09-05-2018.
 */

public class Student {
    private String name, id, email, password;

    public Student(){}

    public Student(String name, String id, String email, String password) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
