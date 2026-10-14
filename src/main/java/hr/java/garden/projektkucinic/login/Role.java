package hr.java.garden.projektkucinic.login;

import java.io.Serializable;

public enum Role implements Serializable {
    ADMIN("Admin"),
    GARDENER("Gardener"),
    VISITOR("Visitor");
    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }

    public static Role getRoleFromName(String roleName) {
        char firstChar = Character.toUpperCase(roleName.charAt(0));
        String restOfrRole = roleName.substring(1).toLowerCase();
        String newRole = firstChar + restOfrRole;
        for (Role r : Role.values()) {
            if (r.getRoleName().compareTo(newRole) == 0) {
                return r;
            }
        }
        return Role.VISITOR;
    }

}
