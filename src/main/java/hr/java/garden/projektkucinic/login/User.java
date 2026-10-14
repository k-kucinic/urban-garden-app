package hr.java.garden.projektkucinic.login;

import hr.java.garden.projektkucinic.model.UrbanGarden;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private Role role;
    private UrbanGarden garden;

    public User(String username, String password, Role role, UrbanGarden garden) {
        this.username = username;
        if (password.length() > 15) {
            this.password = password;
        } else {
            this.password = PasswordHashing.getHashedPassword(password);
        }
        this.role = role;
        this.garden = garden;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UrbanGarden getGarden() {
        return garden;
    }

    public void setGarden(UrbanGarden garden) {
        this.garden = garden;
    }
}
