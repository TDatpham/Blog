package com.xjq.blog.model;

import javax.persistence.*;

@Entity
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    public Subscriber() {
    }

    public Subscriber(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    // Getter Setter đầy đủ
}


