package demo.android.com.simpson;

/**
 * Created by Admin on 09-05-2018.
 */

public class Grades {
    private String course_id, course_name, grade, student_id;

    public Grades() {}

    public Grades(String course_id, String course_name, String grade, String student_id) {
        this.course_id = course_id;
        this.course_name = course_name;
        this.grade = grade;
        this.student_id = student_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public String getGrade() {
        return grade;
    }

    public String getStudent_id() {
        return student_id;
    }
}
