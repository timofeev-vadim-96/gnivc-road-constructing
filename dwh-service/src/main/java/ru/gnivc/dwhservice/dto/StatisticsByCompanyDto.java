package ru.gnivc.dwhservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StatisticsByCompanyDto {
    private int tripCreated;

    private int tripStarted;

    private int tripEnded;

    private int tripCanceled;

    private int tripAccident;

    private int tasks;
}
