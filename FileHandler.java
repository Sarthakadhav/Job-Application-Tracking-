import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// Saves the data to csv files and loads it back when the program starts.
public class FileHandler {

    private static final String STUDENT_FILE = "students.csv";
    private static final String JOB_FILE = "jobs.csv";
    private static final String APPLICATION_FILE = "applications.csv";

    // ---------- saving ----------

    public static void saveAll(PlacementManager manager) {
        try (PrintWriter out = new PrintWriter(new FileWriter(STUDENT_FILE))) {
            for (StudentProfile s : manager.getStudents()) {
                out.println(s.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Could not save students: " + e.getMessage());
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(JOB_FILE))) {
            for (JobPost j : manager.getJobs()) {
                out.println(j.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Could not save jobs: " + e.getMessage());
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(APPLICATION_FILE))) {
            for (Application a : manager.getApplications()) {
                out.println(a.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Could not save applications: " + e.getMessage());
        }
    }

    // ---------- loading ----------

    // students and jobs must be loaded first because applications need them
    public static void loadAll(PlacementManager manager) {
        loadStudents(manager);
        loadJobs(manager);
        loadApplications(manager);
    }

    private static void loadStudents(PlacementManager manager) {
        File file = new File(STUDENT_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length < 4) {
                    System.out.println("Skipping a bad line in " + STUDENT_FILE);
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0]);
                    manager.loadStudent(new StudentProfile(id, parts[1], parts[2], parts[3]));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping a bad line in " + STUDENT_FILE);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read students file: " + e.getMessage());
        }
    }

    private static void loadJobs(PlacementManager manager) {
        File file = new File(JOB_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length < 5) {
                    System.out.println("Skipping a bad line in " + JOB_FILE);
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0]);
                    int openings = Integer.parseInt(parts[4]);
                    manager.loadJob(new JobPost(id, parts[1], parts[2], parts[3], openings));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping a bad line in " + JOB_FILE);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read jobs file: " + e.getMessage());
        }
    }

    private static void loadApplications(PlacementManager manager) {
        File file = new File(APPLICATION_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length < 5) {
                    System.out.println("Skipping a bad line in " + APPLICATION_FILE);
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0]);
                    StudentProfile student = manager.getStudentById(Integer.parseInt(parts[1]));
                    JobPost job = manager.getJobById(Integer.parseInt(parts[2]));
                    if (student == null || job == null) {
                        System.out.println("Skipping an application with a missing student or job.");
                        continue;
                    }

                    ArrayList<String> rounds = new ArrayList<>();
                    if (!parts[4].isEmpty()) {
                        String[] roundList = parts[4].split("\\|");
                        for (String r : roundList) {
                            rounds.add(r);
                        }
                    }
                    manager.loadApplication(new Application(id, student, job, rounds, parts[3]));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping a bad line in " + APPLICATION_FILE);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read applications file: " + e.getMessage());
        }
    }
}
