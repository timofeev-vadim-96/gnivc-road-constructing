package ru.gnivc.dwhservice.mapper;

import org.springframework.stereotype.Component;
import ru.gnivc.dwhservice.dto.StatisticsByCompanyDto;
import ru.gnivc.dwhservice.model.CompanyEntity;
import ru.gnivc.dwhservice.model.CompanyStatisticsEntity;

@Component
public class CompanyStatisticsMapper {

    public static StatisticsByCompanyDto convertEntityToDto(CompanyStatisticsEntity entity) {
        return StatisticsByCompanyDto.builder()
                .tasks(entity.getTasks())
                .tripAccident(entity.getTripAccident())
                .tripCanceled(entity.getTripCanceled())
                .tripCreated(entity.getTripCreated())
                .tripEnded(entity.getTripEnded())
                .tripStarted(entity.getTripStarted())
                .build();
    }

    public static CompanyStatisticsEntity mapFromDtoToEntity(CompanyEntity company, StatisticsByCompanyDto dto) {
        CompanyStatisticsEntity companyStatistics;
        companyStatistics = CompanyStatisticsEntity.builder()
                .company(company)
                .tasks(dto.getTasks())
                .tripCreated(dto.getTripCreated())
                .tripEnded(dto.getTripEnded())
                .tripAccident(dto.getTripAccident())
                .tripStarted(dto.getTripStarted())
                .tripCanceled(dto.getTripCanceled())
                .build();
        return companyStatistics;
    }

    public static CompanyStatisticsEntity updateCompanyStatisticsEntity(
            CompanyStatisticsEntity companyStatistics,
            StatisticsByCompanyDto dto) {

        companyStatistics.setTasks(dto.getTasks());
        companyStatistics.setTripCreated(dto.getTripCreated());
        companyStatistics.setTripEnded(dto.getTripEnded());
        companyStatistics.setTripAccident(dto.getTripAccident());
        companyStatistics.setTripStarted(dto.getTripStarted());
        companyStatistics.setTripCanceled(dto.getTripCanceled());
        return companyStatistics;
    }
}
