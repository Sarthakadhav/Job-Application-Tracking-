public class JobPost extends BaseEntity {

    private String company;
    private String role;
    private String eligibility;
    private int openings;

    public JobPost(int id, String company, String role, String eligibility, int openings) {
        super(id);
        this.company = company;
        this.role = role;
        this.eligibility = eligibility;
        this.openings = openings;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public int getOpenings() {
        return openings;
    }

    public void setOpenings(int openings) {
        this.openings = openings;
    }

    @Override
    public String toCsv() {
        return getId() + "," + company + "," + role + "," + eligibility + "," + openings;
    }

    @Override
    public String toString() {
        return "ID: " + getId() + " | Company: " + company + " | Role: " + role
                + " | Eligibility: " + eligibility + " | Openings: " + openings;
    }
}
