package ck.ckeller.wgutermtracker;

public class CourseNote {

    private int courseNoteId;
    private int courseId;
    private String courseNoteText;

    public int getCourseNoteId() {
        return courseNoteId;
    }

    public void setCourseNoteId(int courseNoteId) {
        this.courseNoteId = courseNoteId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseNoteText() {
        return courseNoteText;
    }

    public void setCourseNoteText(String courseNoteText) {
        this.courseNoteText = courseNoteText;
    }
}
