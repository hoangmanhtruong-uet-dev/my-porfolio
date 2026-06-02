package com.love.portfolio.model;

import lombok.Data;
import java.util.List;

@Data
public class Question {
    private String question;
    private List<String> options;
    private int correct;
    private String explanation;
}
