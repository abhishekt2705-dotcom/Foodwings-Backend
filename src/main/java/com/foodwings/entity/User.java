package com.foodwings.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 15)
    private String phone;

    private String profilePhoto;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    public User() {}

    public User(String name, String email, String password, String phone, String profilePhoto, boolean active, Set<Role> roles, List<Address> addresses) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.profilePhoto = profilePhoto;
        this.active = active;
        if (roles != null) this.roles = roles;
        if (addresses != null) this.addresses = addresses;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }

    public static UserBuilder builder() { return new UserBuilder(); }

    public static class UserBuilder {
        private String name;
        private String email;
        private String password;
        private String phone;
        private String profilePhoto;
        private boolean active = true;
        private Set<Role> roles = new HashSet<>();
        private List<Address> addresses = new ArrayList<>();

        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder phone(String phone) { this.phone = phone; return this; }
        public UserBuilder profilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; return this; }
        public UserBuilder active(boolean active) { this.active = active; return this; }
        public UserBuilder roles(Set<Role> roles) { if (roles != null) this.roles = roles; return this; }
        public UserBuilder addresses(List<Address> addresses) { if (addresses != null) this.addresses = addresses; return this; }

        public User build() {
            return new User(name, email, password, phone, profilePhoto, active, roles, addresses);
        }
    }
}
