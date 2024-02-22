package com.example.chief.model;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contests")
public class Contests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start", nullable = false)
    private LocalDateTime start;

    @Column(name = "ed", nullable = false)
    private LocalDateTime end;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "count")
    private int count;
    
    @Column(name = "st")
    private int st;

    // Constructors, getters, setters, and other methods

    public Contests() {
        // Default constructor
    }

    public Contests(String title, LocalDateTime start, LocalDateTime end) {
    	this.title = title;
        this.start = start;
        this.end = end;
    }

    // Other constructors as needed

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
    	return title;
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEd() {
        return end;
    }

    public void setEd(LocalDateTime end) {
        this.end = end;
    }
    
    public int getCount() {
        return count;
    }

    // Setter for count
    public void setCount(int count) {
        this.count = count;
    }
    
    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }


    // Other methods as needed
}
