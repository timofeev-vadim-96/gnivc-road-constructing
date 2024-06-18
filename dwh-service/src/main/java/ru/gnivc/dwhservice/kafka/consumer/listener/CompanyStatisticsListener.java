package ru.gnivc.dwhservice.kafka.consumer.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.gnivc.dwhservice.dao.CompanyDao;
import ru.gnivc.dwhservice.dao.CompanyStatisticsDao;
import ru.gnivc.dwhservice.dto.StatisticsByCompanyDto;
import ru.gnivc.dwhservice.mapper.CompanyStatisticsMapper;
import ru.gnivc.dwhservice.model.CompanyEntity;
import ru.gnivc.dwhservice.model.CompanyStatisticsEntity;

import java.util.Map;
import java.util.Optional;

/**
 * Слушатель типа Kafka
 */
@Component
@Slf4j
public class CompanyStatisticsListener {
    private final JsonMapper jsonMapper;

    private final CompanyDao companyDao;

    private final CompanyStatisticsDao statisticsDao;

    public CompanyStatisticsListener(CompanyDao companyDao, CompanyStatisticsDao statisticsDao) {
        this.jsonMapper = new JsonMapper();
        this.companyDao = companyDao;
        this.statisticsDao = statisticsDao;
    }

    @KafkaListener(
            topics = "company_statistics",
            containerFactory = "companyStatisticsListenerContainerFactory",
            groupId = "company_statistics_group")
    void listen(ConsumerRecord<String, String> record) {
        String data = record.value();
        log.info("Received company_statistics from Kafka: {}", data);

        Map<String, StatisticsByCompanyDto> companiesStatistics;
        try {
            companiesStatistics = jsonMapper.readValue(data, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Exception when trying to parse JSON message from Kafka.", e);
        }

        saveCompaniesStatistics(companiesStatistics);
    }

    private void saveCompaniesStatistics(Map<String, StatisticsByCompanyDto> companiesStatisticsMap) {
        for (String key : companiesStatisticsMap.keySet()) {
            Optional<CompanyEntity> companyOptional = companyDao.findByName(key);
            CompanyEntity company = companyOptional.orElseGet(() -> companyDao.save(
                    CompanyEntity.builder()
                            .name(key)
                            .build()));

            Optional<CompanyStatisticsEntity> companyStatisticsOptional = statisticsDao.findByCompany(company);
            CompanyStatisticsEntity companyStatistics;
            StatisticsByCompanyDto dto = companiesStatisticsMap.get(key);
            if (companyStatisticsOptional.isEmpty()) {
                companyStatistics = CompanyStatisticsMapper.mapFromDtoToEntity(company, dto);
            } else {
                companyStatistics = CompanyStatisticsMapper
                        .updateCompanyStatisticsEntity(companyStatisticsOptional.get(), dto);
            }
            statisticsDao.save(companyStatistics);
        }
    }
}
