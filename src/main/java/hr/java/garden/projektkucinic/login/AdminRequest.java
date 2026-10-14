package hr.java.garden.projektkucinic.login;

import java.io.Serializable;

public class AdminRequest implements Serializable {
    private String username;
    private Role requestedRole;
    private boolean approved;

    public AdminRequest(String username, Role role, boolean approved) {
        this.requestedRole = role;
        this.username = username;
        this.approved = approved;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRequestedRole() {
        return requestedRole;
    }

    public void setRequestedRole(Role requestedRole) {
        this.requestedRole = requestedRole;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}

