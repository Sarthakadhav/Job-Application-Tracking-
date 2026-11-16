public class StudentProfile extends BaseEntity {

    private String name;
    private String department;
    private String skills; // skills are stored with ; between them, e.g. Java;SQL

    public StudentProfile(int id, String name, String department, String skills) {
        super(id);
        this.name = name;
        this.department = department;
        this.skills = skills;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    // checks if the student has the given skill (ignores upper/lower case)
    public boolean hasSkill(String skill) {
        String[] skillList = skills.split(";");
        for (String s : skillList) {
            if (s.trim().equalsIgnoreCase(skill.trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toCsv() {
        return getId() + "," + name + "," + department + "," + skills;
    }

    @Override
    public String toString() {
        return "ID: " + getId() + " | Name: " + name + " | Department: " + department
                + " | Skills: " + skills.replace(";", ", ");
    }
}
