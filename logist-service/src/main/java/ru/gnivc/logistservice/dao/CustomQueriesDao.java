package ru.gnivc.logistservice.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.dto.output.StatisticByCompanyDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomQueriesDao {
    private final JdbcTemplate jdbcTemplate;

    public Map<String, StatisticByCompanyDto> getCompaniesStatisticsToday() {
        Map<String, StatisticByCompanyDto> companyStatistics = new HashMap<>();
        LocalDate today = LocalDate.now();
        String formattedDate = "'" + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'";

        jdbcTemplate.query("SELECT companies.company_name, trip_events.event, COUNT(*) AS count\n" +
                        "FROM trip_events\n" +
                        "         left join trips\n" +
                        "                   on trip_events.trip = trips.id\n" +
                        "         left join tasks\n" +
                        "                   on trips.task = tasks.id\n" +
                        "         left join companies\n" +
                        "                   on tasks.company = companies.id\n" +
                        "where trip_events.time > " + formattedDate + "\n" +
                        "GROUP BY companies.company_name, trip_events.event",
                (rs, rowNum) -> {
                    String companyName = rs.getString("company_name");
                    String event = rs.getString("event");
                    int count = rs.getInt("count");

                    StatisticByCompanyDto stats = companyStatistics.getOrDefault(companyName,
                            StatisticByCompanyDto.builder().build());

                    switch (event) {
                        case "TRIP_CREATED":
                            stats.setTripCreated(count);
                            break;
                        case "TRIP_STARTED":
                            stats.setTripStarted(count);
                            break;
                        case "TRIP_ENDED":
                            stats.setTripEnded(count);
                            break;
                        case "TRIP_CANCELED":
                            stats.setTripCanceled(count);
                            break;
                        case "TRIP_ACCIDENT":
                            stats.setTripAccident(count);
                            break;
                    }

                    companyStatistics.put(companyName, stats);

                    return null;
                });

        return companyStatistics;
    }

    public Map<String, Integer> getCompaniesTaskQuantity() {
        Map<String, Integer> tasksByCompanies = new HashMap<>();
        jdbcTemplate.query("select companies.company_name, count(*) as task_quantity from tasks\n" +
                "                left join companies\n" +
                "                on tasks.company = companies.id\n" +
                "                group by companies.company_name", (rs, rowNum) -> {
            String companyName = rs.getString("company_name");
            int taskQuantity = rs.getInt("task_quantity");

            tasksByCompanies.put(companyName, taskQuantity);

            return null;
        });
        return tasksByCompanies;
    }
}
