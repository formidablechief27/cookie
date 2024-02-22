package com.example.chief.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "testcases")
public class Tests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private int testcaseNumber;

    @Column(name = "input", columnDefinition = "LONGTEXT")
    private String input;

    @Column(name = "output", columnDefinition = "LONGTEXT")
    private String output;

    @Column(name = "explain", columnDefinition = "TEXT")
    private String explain;


    public Tests() {
        // Default constructor
    }

    public Tests(int testcaseNumber, String input, String output, String explain) {
        this.testcaseNumber = testcaseNumber;
        this.input = input;
        this.output = output;
        this.explain = explain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestcaseNumber() {
        return testcaseNumber;
    }

    public void setTestcaseNumber(int testcaseNumber) {
        this.testcaseNumber = testcaseNumber;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}

