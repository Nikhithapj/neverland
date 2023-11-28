package com.example.library.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
public class DailyEarnings {
    private Date date;

    public DailyEarnings(Date date, double earnings) {
        this.date = date;
        this.earnings = earnings;
    }

    private double earnings;


}
