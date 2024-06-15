package ru.gnivc.logistservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.gnivc.logistservice.dao.CompanyDao;
import ru.gnivc.logistservice.dao.CustomQueriesDao;
import ru.gnivc.logistservice.dto.output.StatisticByCompanyDto;
import ru.gnivc.logistservice.kafka.producer.kafka.KafkaProducer;
import ru.gnivc.logistservice.model.CompanyEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class MetricService {
    private final CustomQueriesDao customDao;
    private final CompanyDao companyDao;
    private final KafkaProducer kafkaProducer;
    private static final int SCHEDULING_DELAY = 1;

    @Scheduled(fixedDelay = SCHEDULING_DELAY, timeUnit = TimeUnit.HOURS)
    private void sendMetricsToDwh(){
        Map<String, StatisticByCompanyDto> companiesStatistics = getCompaniesStatistics();
        ResponseEntity<Void> response = kafkaProducer.sendCompanyStatistics(companiesStatistics);
        if (response.getStatusCode() == HttpStatus.CREATED){
            log.info("Statistics on companies sent to dwh-ms: " + companiesStatistics);
        }
    }

    private Map<String, StatisticByCompanyDto> getCompaniesStatistics(){
        Map<String, StatisticByCompanyDto> companiesStatistics = customDao.getCompaniesStatisticsToday();
        fillMapWithCompaniesWithoutStat(companiesStatistics);

        Map<String, Integer> tasksQuantityByCompany = customDao.getCompaniesTaskQuantity();
        for (String key: companiesStatistics.keySet()){
            Integer tasksQuantity = tasksQuantityByCompany.get(key);
            companiesStatistics.get(key).setTasks(tasksQuantity);
        }
        return companiesStatistics;
    }

    /**
     * If the company does not have event statistics for today, add an empty one
     */
    private void fillMapWithCompaniesWithoutStat(Map<String, StatisticByCompanyDto> companiesStatistics){
        List<CompanyEntity> companies = companyDao.findAll();
        for (CompanyEntity company: companies){
            if (!companiesStatistics.containsKey(company.getCompanyName())){
                companiesStatistics.put(company.getCompanyName(), new StatisticByCompanyDto());
            }
        }
    }
}
