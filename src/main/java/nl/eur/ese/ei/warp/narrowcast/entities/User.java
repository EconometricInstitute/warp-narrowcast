package nl.eur.ese.ei.warp.narrowcast.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {

    @Id
    @Column(name="login")
    private String login;
    @Column(name="name")
    private String name;
    protected User() {}

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }
}
