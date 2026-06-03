package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "goal_progress")
public class GoalProgress {

    @Id
    private Integer goalId; // khớp với id trong goalsData của frontend

    private int progress;   // 0-100

    private String status;  // "planned" | "inprogress" | "done"

    private String startDate;

    private String endDate;
}
