package com.chronos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "nominees")
public class Nominee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nomineeId;

    private String name;

    @Column(nullable = false)
    private String email;

    private String phone;

    private String relation; // e.g., "Wife", "Brother", "Lawyer"

    // --- ACCESS TOKEN ---
    // This token is generated ONLY when the Dead Man's Switch triggers.
    // The nominee uses this token to download the files.
    private String accessToken;

    // --- RELATIONSHIP ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // --- CONSTRUCTORS ---
    public Nominee() {}

    public Nominee(String name, String email, String phone, String relation, User user) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.relation = relation;
        this.user = user;
    }

    // --- GETTERS AND SETTERS ---
    public Long getNomineeId() { return nomineeId; }
    public void setNomineeId(Long nomineeId) { this.nomineeId = nomineeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}