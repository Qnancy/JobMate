package cn.edu.zju.cs.jobmate.enums;

/**
 * Company type enumeration for Company entities.
 *
 * @see cn.edu.zju.cs.jobmate.models.Company
 */
public enum CompanyType {

    STATE("state"),
    PRIVATE("private"),
    FOREIGN("foreign");

    private final String value;

    CompanyType(String value) {
        this.value = value;
    }

    String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CompanyType." + this.name();
    }
}
