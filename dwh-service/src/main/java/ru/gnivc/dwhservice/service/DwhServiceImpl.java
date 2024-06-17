package ru.gnivc.dwhservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.dwhservice.dao.CompanyDao;
import ru.gnivc.dwhservice.dao.CompanyStatisticsDao;
import ru.gnivc.dwhservice.dto.StatisticsByCompanyDto;
import ru.gnivc.dwhservice.mapper.CompanyStatisticsMapper;
import ru.gnivc.dwhservice.model.CompanyEntity;
import ru.gnivc.dwhservice.model.CompanyStatisticsEntity;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DwhServiceImpl implements DwhService {
    private final CompanyDao companyDao;
    private final CompanyStatisticsDao statisticsDao;

    public ResponseEntity<StatisticsByCompanyDto> getStatisticsByCompany(String companyName) {
        Optional<CompanyEntity> company = companyDao.findByName(companyName);
        if (company.isEmpty()) {
            String answer = "Company with name = " + companyName + " not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else {
            Optional<CompanyStatisticsEntity> companyStatistics = statisticsDao.findByCompany(company.get());
            if (companyStatistics.isEmpty()) {
                String answer = "There are no statistics for the company with name = " + companyName;
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
            } else {
                StatisticsByCompanyDto dto = CompanyStatisticsMapper.convertEntityToDto(companyStatistics.get());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            }
        }
    }
}
