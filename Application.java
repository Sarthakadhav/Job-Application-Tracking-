import java.util.ArrayList;

public class Application extends BaseEntity {

    // the different statuses an application can have
    public static final String APPLIED = "Applied";
    public static final String SHORTLISTED = "Shortlisted";
    public static final String SELECTED = "Selected";
    public static final String REJECTED = "Rejected";

    private StudentProfile student;
    private JobPost job;
    private ArrayList<String> interviewRounds;
    private String status;

    // used when a student applies for a job for the first time
    public Application(int id, StudentProfile student, JobPost job) {
        super(id);
        this.student = student;
        this.job = job;
        this.interviewRounds = new ArrayList<>();
        this.status = APPLIED;
    }

    // used when the application is loaded back from the file
    public Application(int id, StudentProfile student, JobPost job,
                       ArrayList<String> interviewRounds, String status) {
        super(id);
        this.student = student;
        this.job = job;
        this.interviewRounds = interviewRounds;
        this.status = status;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public JobPost getJob() {
        return job;
    }

    public void setJob(JobPost job) {
        this.job = job;
    }

    public ArrayList<String> getInterviewRounds() {
        return interviewRounds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // adds one interview round as a line of text
    public void addRound(String roundName, String date, String result) {
        interviewRounds.add(roundName + " on " + date + " - " + result);
    }

    @Override
    public String toCsv() {
        // rounds are joined with | so they fit in one column
        String rounds = "";
        for (int i = 0; i < interviewRounds.size(); i++) {
            if (i > 0) {
                rounds = rounds + "|";
            }
            rounds = rounds + interviewRounds.get(i);
        }
        return getId() + "," + student.getId() + "," + job.getId() + "," + status + "," + rounds;
    }

    @Override
    public String toString() {
        String text = "Application ID: " + getId()
                + " | Student: " + student.getName() + " (ID " + student.getId() + ")"
                + " | Job: " + job.getCompany() + " - " + job.getRole() + " (ID " + job.getId() + ")"
                + " | Status: " + status;
        if (interviewRounds.isEmpty()) {
            text = text + "\n    No interview rounds recorded.";
        } else {
            for (int i = 0; i < interviewRounds.size(); i++) {
                text = text + "\n    Round " + (i + 1) + ": " + interviewRounds.get(i);
            }
        }
        return text;
    }
}
