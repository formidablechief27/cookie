package com.example.chief.model;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "questions")
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int contestId;

    private String tags;

    private String questionName;
    private String question;
    private String inputFormat;
    private String outputFormat;
    private int testcaseStart;
    private int testcaseEnd;
    private int minPoints;
    private int pts;

    // Constructors, getters, and setters can be generated as well.

    public Questions() {
        // Default constructor
    }

    // Getters and setters for all fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }	

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public int getTestcaseStart() {
        return testcaseStart;
    }

    public void setTestcaseStart(int testcaseStart) {
        this.testcaseStart = testcaseStart;
    }

    public int getTestcaseEnd() {
        return testcaseEnd;
    }

    public void setTestcaseEnd(int testcaseEnd) {
        this.testcaseEnd = testcaseEnd;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }
    
    public int getPts() {
    	return pts;
    }
    
    public void setPts(int pts) {
    	this.pts = pts;
    }
}
