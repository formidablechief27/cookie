package com.example.chief.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tutorial")
public class Tutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private String code;

    // Constructors, getters, and setters

    public Tutorial() {
        // Default constructor
    }

    public Tutorial(String text, String code) {
        this.text = text;
        this.code = code;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // toString method (optional)

    @Override
    public String toString() {
        return "Tutorial{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
