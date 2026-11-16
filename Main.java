import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static PlacementManager manager = new PlacementManager();

    public static void main(String[] args) {
        FileHandler.loadAll(manager);
        System.out.println("=====================================");
        System.out.println("   Job Application Tracking System");
        System.out.println("=====================================");

        boolean running = true;
        while (running) {
            showMenu();
            int choice = readChoice("Enter your choice: ", 0, 13);
            System.out.println();

            try {
                switch (choice) {
                    case 1:
                        registerStudent();
                        break;
                    case 2:
                        printList(manager.getStudents(), "No students registered yet.");
                        break;
                    case 3:
                        addJob();
                        break;
                    case 4:
                        printList(manager.getJobs(), "No job posts added yet.");
                        break;
                    case 5:
                        submitApplication();
                        break;
                    case 6:
                        printList(manager.getApplications(), "No applications submitted yet.");
                        break;
                    case 7:
                        updateStatus();
                        break;
                    case 8:
                        recordRound();
                        break;
                    case 9:
                        searchBySkill();
                        break;
                    case 10:
                        filterByStatus();
                        break;
                    case 11:
                        showSortedLists();
                        break;
                    case 12:
                        manager.printSummary();
                        break;
                    case 13:
                        FileHandler.saveAll(manager);
                        System.out.println("Data saved to files.");
                        break;
                    case 0:
                        FileHandler.saveAll(manager);
                        System.out.println("Data saved. Thank you, goodbye!");
                        running = false;
                        break;
                }
            } catch (RecordNotFoundException | DuplicateApplicationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("------------- MENU -------------");
        System.out.println(" 1. Register student");
        System.out.println(" 2. View all students");
        System.out.println(" 3. Add job post");
        System.out.println(" 4. View all job posts");
        System.out.println(" 5. Submit application");
        System.out.println(" 6. View all applications");
        System.out.println(" 7. Update application status");
        System.out.println(" 8. Record interview round");
        System.out.println(" 9. Search students by skill");
        System.out.println("10. Filter applications by status");
        System.out.println("11. Sorted lists");
        System.out.println("12. Placement summary");
        System.out.println("13. Save data to files");
        System.out.println(" 0. Save and exit");
        System.out.println("--------------------------------");
    }

    // prints every item of a list, or a message if the list is empty
    private static void printList(ArrayList<?> list, String emptyMessage) {
        if (list.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Object item : list) {
            System.out.println(item);
        }
    }

    private static void registerStudent() {
        System.out.println("--- Register Student ---");
        String name = readText("Enter student name: ");
        String department = readText("Enter department: ");
        String skills = readSkills("Enter skills (separate with ;): ");

        StudentProfile student = manager.registerStudent(name, department, skills);
        System.out.println("Student registered with ID " + student.getId());
    }

    private static void addJob() {
        System.out.println("--- Add Job Post ---");
        String company = readText("Enter company name: ");
        String role = readText("Enter job role: ");
        String eligibility = readText("Enter eligibility: ");
        int openings = readPositiveInt("Enter number of openings: ");

        JobPost job = manager.addJob(company, role, eligibility, openings);
        System.out.println("Job post added with ID " + job.getId());
    }

    private static void submitApplication()
            throws RecordNotFoundException, DuplicateApplicationException {
        System.out.println("--- Submit Application ---");
        int studentId = readInt("Enter student ID: ");
        int jobId = readInt("Enter job ID: ");

        Application application = manager.submitApplication(studentId, jobId);
        System.out.println("Application submitted with ID " + application.getId());
    }

    private static void updateStatus() throws RecordNotFoundException {
        System.out.println("--- Update Application Status ---");
        int applicationId = readInt("Enter application ID: ");
        Application application = manager.findApplication(applicationId);

        String status = chooseStatus();
        application.setStatus(status);
        System.out.println("Status updated to " + status);
    }

    private static void recordRound() throws RecordNotFoundException {
        System.out.println("--- Record Interview Round ---");
        int applicationId = readInt("Enter application ID: ");
        Application application = manager.findApplication(applicationId);

        String roundName = readText("Enter round name (e.g. Technical, HR): ");
        String date = readDate("Enter interview date (dd-mm-yyyy): ");
        System.out.println("1. Pass");
        System.out.println("2. Fail");
        int resultChoice = readChoice("Enter result: ", 1, 2);

        String result;
        if (resultChoice == 1) {
            result = "Pass";
        } else {
            result = "Fail";
        }

        application.addRound(roundName, date, result);
        System.out.println("Interview round recorded.");
    }

    private static void searchBySkill() {
        System.out.println("--- Search Students by Skill ---");
        String skill = readText("Enter skill to search: ");
        printList(manager.searchStudentsBySkill(skill), "No student has this skill.");
    }

    private static void filterByStatus() {
        System.out.println("--- Filter Applications by Status ---");
        String status = chooseStatus();
        printList(manager.filterApplicationsByStatus(status), "No applications with status " + status + ".");
    }

    private static void showSortedLists() {
        System.out.println("--- Sorted Lists ---");
        System.out.println("1. Students by name");
        System.out.println("2. Jobs by company name");
        System.out.println("3. Jobs by openings (highest first)");
        int choice = readChoice("Enter your choice: ", 1, 3);
        System.out.println();

        if (choice == 1) {
            printList(manager.getStudentsSortedByName(), "No students registered yet.");
        } else if (choice == 2) {
            printList(manager.getJobsSortedByCompany(), "No job posts added yet.");
        } else {
            printList(manager.getJobsSortedByOpenings(), "No job posts added yet.");
        }
    }

    // shows the four statuses and returns the one the user picks
    private static String chooseStatus() {
        System.out.println("1. " + Application.APPLIED);
        System.out.println("2. " + Application.SHORTLISTED);
        System.out.println("3. " + Application.SELECTED);
        System.out.println("4. " + Application.REJECTED);
        int choice = readChoice("Choose status: ", 1, 4);

        if (choice == 1) {
            return Application.APPLIED;
        } else if (choice == 2) {
            return Application.SHORTLISTED;
        } else if (choice == 3) {
            return Application.SELECTED;
        } else {
            return Application.REJECTED;
        }
    }


    // ---------- reading input from the console ----------

    private static Scanner sc = new Scanner(System.in);

    public static int readInt(String message) {
        while (true) {
            System.out.print(message);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public static int readPositiveInt(String message) {
        while (true) {
            int number = readInt(message);
            if (number > 0) {
                return number;
            }
            System.out.println("The number must be greater than 0.");
        }
    }

    // keeps asking until the choice is between min and max
    public static int readChoice(String message, int min, int max) {
        while (true) {
            int choice = readInt(message);
            if (choice >= min && choice <= max) {
                return choice;
            }
            System.out.println("Invalid choice. Enter a number from " + min + " to " + max + ".");
        }
    }

    public static String readText(String message) {
        while (true) {
            System.out.print(message);
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("This field cannot be empty.");
            } else {
                // comma and | are used in the data files, so they are removed here
                return line.replace(",", " ").replace("|", " ");
            }
        }
    }

    // reads skills separated by ; and cleans the extra spaces
    public static String readSkills(String message) {
        while (true) {
            String line = readText(message);
            String[] parts = line.split(";");
            String skills = "";
            for (String part : parts) {
                part = part.trim();
                if (!part.isEmpty()) {
                    if (!skills.isEmpty()) {
                        skills = skills + ";";
                    }
                    skills = skills + part;
                }
            }
            if (!skills.isEmpty()) {
                return skills;
            }
            System.out.println("Please enter at least one skill.");
        }
    }

    // reads a date in dd-MM-yyyy format
    public static String readDate(String message) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
        while (true) {
            System.out.print(message);
            String line = sc.nextLine().trim();
            try {
                LocalDate.parse(line, format);
                return line;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use the format dd-mm-yyyy, for example 15-12-2026.");
            }
        }
    }
}
