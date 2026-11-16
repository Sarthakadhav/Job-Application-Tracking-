import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

// Keeps all the data in memory and has the main features of the system.
public class PlacementManager {

    private HashMap<Integer, StudentProfile> students = new HashMap<>();
    private HashMap<Integer, JobPost> jobs = new HashMap<>();
    private ArrayList<Application> applications = new ArrayList<>();

    private int nextStudentId = 1;
    private int nextJobId = 1;
    private int nextApplicationId = 1;

    // ---------- adding new records ----------

    public StudentProfile registerStudent(String name, String department, String skills) {
        StudentProfile student = new StudentProfile(nextStudentId, name, department, skills);
        students.put(nextStudentId, student);
        nextStudentId++;
        return student;
    }

    public JobPost addJob(String company, String role, String eligibility, int openings) {
        JobPost job = new JobPost(nextJobId, company, role, eligibility, openings);
        jobs.put(nextJobId, job);
        nextJobId++;
        return job;
    }

    public Application submitApplication(int studentId, int jobId)
            throws RecordNotFoundException, DuplicateApplicationException {

        StudentProfile student = students.get(studentId);
        if (student == null) {
            throw new RecordNotFoundException("Student with ID " + studentId + " was not found.");
        }

        JobPost job = jobs.get(jobId);
        if (job == null) {
            throw new RecordNotFoundException("Job with ID " + jobId + " was not found.");
        }

        for (Application a : applications) {
            if (a.getStudent().getId() == studentId && a.getJob().getId() == jobId) {
                throw new DuplicateApplicationException(
                        student.getName() + " has already applied for this job.");
            }
        }

        Application application = new Application(nextApplicationId, student, job);
        applications.add(application);
        nextApplicationId++;
        return application;
    }

    // ---------- loading records from the files ----------
    // these keep the next id correct so new ids do not clash with old ones

    public void loadStudent(StudentProfile student) {
        students.put(student.getId(), student);
        if (student.getId() >= nextStudentId) {
            nextStudentId = student.getId() + 1;
        }
    }

    public void loadJob(JobPost job) {
        jobs.put(job.getId(), job);
        if (job.getId() >= nextJobId) {
            nextJobId = job.getId() + 1;
        }
    }

    public void loadApplication(Application application) {
        applications.add(application);
        if (application.getId() >= nextApplicationId) {
            nextApplicationId = application.getId() + 1;
        }
    }

    // ---------- finding records ----------

    public StudentProfile getStudentById(int id) {
        return students.get(id);
    }

    public JobPost getJobById(int id) {
        return jobs.get(id);
    }

    public Application findApplication(int id) throws RecordNotFoundException {
        for (Application a : applications) {
            if (a.getId() == id) {
                return a;
            }
        }
        throw new RecordNotFoundException("Application with ID " + id + " was not found.");
    }

    // ---------- lists of records ----------

    public ArrayList<StudentProfile> getStudents() {
        ArrayList<StudentProfile> list = new ArrayList<>(students.values());
        list.sort((a, b) -> a.getId() - b.getId());
        return list;
    }

    public ArrayList<JobPost> getJobs() {
        ArrayList<JobPost> list = new ArrayList<>(jobs.values());
        list.sort((a, b) -> a.getId() - b.getId());
        return list;
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    // ---------- search, filter and sort using lambda ----------

    public ArrayList<StudentProfile> searchStudentsBySkill(String skill) {
        ArrayList<StudentProfile> result = new ArrayList<>();
        Predicate<StudentProfile> hasSkill = s -> s.hasSkill(skill);
        for (StudentProfile s : getStudents()) {
            if (hasSkill.test(s)) {
                result.add(s);
            }
        }
        return result;
    }

    public ArrayList<Application> filterApplicationsByStatus(String status) {
        ArrayList<Application> result = new ArrayList<>();
        Predicate<Application> sameStatus = a -> a.getStatus().equals(status);
        for (Application a : applications) {
            if (sameStatus.test(a)) {
                result.add(a);
            }
        }
        return result;
    }

    public ArrayList<StudentProfile> getStudentsSortedByName() {
        ArrayList<StudentProfile> list = getStudents();
        list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return list;
    }

    public ArrayList<JobPost> getJobsSortedByCompany() {
        ArrayList<JobPost> list = getJobs();
        list.sort((a, b) -> a.getCompany().compareToIgnoreCase(b.getCompany()));
        return list;
    }

    public ArrayList<JobPost> getJobsSortedByOpenings() {
        ArrayList<JobPost> list = getJobs();
        // more openings come first
        list.sort((a, b) -> b.getOpenings() - a.getOpenings());
        return list;
    }

    // ---------- placement summary ----------

    private int countApplications(Predicate<Application> condition) {
        int count = 0;
        for (Application a : applications) {
            if (condition.test(a)) {
                count++;
            }
        }
        return count;
    }

    // a student is placed if at least one of his applications is Selected
    private boolean isPlaced(int studentId) {
        for (Application a : applications) {
            if (a.getStudent().getId() == studentId && a.getStatus().equals(Application.SELECTED)) {
                return true;
            }
        }
        return false;
    }

    public void printSummary() {
        System.out.println("----- Placement Summary -----");
        System.out.println("Total students     : " + students.size());
        System.out.println("Total job posts    : " + jobs.size());
        System.out.println("Total applications : " + applications.size());
        System.out.println("  Applied          : " + countApplications(a -> a.getStatus().equals(Application.APPLIED)));
        System.out.println("  Shortlisted      : " + countApplications(a -> a.getStatus().equals(Application.SHORTLISTED)));
        System.out.println("  Selected         : " + countApplications(a -> a.getStatus().equals(Application.SELECTED)));
        System.out.println("  Rejected         : " + countApplications(a -> a.getStatus().equals(Application.REJECTED)));

        int placed = 0;
        for (StudentProfile s : students.values()) {
            if (isPlaced(s.getId())) {
                placed++;
            }
        }
        System.out.println("Students placed    : " + placed);
        if (students.size() > 0) {
            double percentage = placed * 100.0 / students.size();
            System.out.println("Placement %        : " + String.format("%.1f", percentage) + "%");
        }

        // number of selected students for each company
        HashMap<String, Integer> selections = new HashMap<>();
        for (Application a : applications) {
            if (a.getStatus().equals(Application.SELECTED)) {
                String company = a.getJob().getCompany();
                if (selections.containsKey(company)) {
                    selections.put(company, selections.get(company) + 1);
                } else {
                    selections.put(company, 1);
                }
            }
        }
        System.out.println("Selections by company:");
        if (selections.isEmpty()) {
            System.out.println("  No student has been selected yet.");
        } else {
            selections.forEach((company, count) -> System.out.println("  " + company + " : " + count));
        }
    }
}
