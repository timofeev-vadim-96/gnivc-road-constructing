package ru.gnivc.logistservice.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.dto.output.StatisticByCompanyDto;
import ru.gnivc.logistservice.dto.output.TripDto;
import ru.gnivc.logistservice.model.TaskEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomQueriesDao {
    private final JdbcTemplate jdbcTemplate;
    private final TaskDao taskDao;

    public Map<String, StatisticByCompanyDto> getCompaniesStatisticsToday() {
        Map<String, StatisticByCompanyDto> companyStatistics = new HashMap<>();
        LocalDate today = LocalDate.now();
        String formattedDate = "'" + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'";

        jdbcTemplate.query("select company_name, event, count(*)\n" +
                        "from (select companies.company_name                                       as company_name,\n" +
                        "             te.event                                                     as event,\n" +
                        "             row_number() over (partition by trips.id order by time desc) as rn\n" +
                        "      from trip_events as te\n" +
                        "               inner join trips\n" +
                        "                          on te.trip = trips.id\n" +
                        "               inner join tasks\n" +
                        "                          on trips.task = tasks.id\n" +
                        "               inner join companies\n" +
                        "                          on tasks.company = companies.id\n" +
                        "      where te.time > " + formattedDate +
                        ") as res\n" +
                        "where res.rn = 1\n" +
                        "group by company_name, event;",
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

    public Optional<TripDto> findTripByIdWithStatus(long tripId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("select res.id, res.task, res.creation_time, res.start_time, res.end_time, res.event from\n" +
                "(select trips.id, trips.task, trips.creation_time, trips.start_time, trips.end_time, te.event as event,\n" +
                "row_number() over (partition by trips.id order by te.time desc) as rn\n" +
                "from trip_events as te\n" +
                "inner join trips\n" +
                "on te.trip = trips.id\n" +
                "where trips.id = " + tripId +
                ") as res\n" +
                "where rn = 1", (res, rowNum) -> {

            TaskEntity task = taskDao.findById(res.getLong("task")).get();

            return TripDto.builder()
                    .id(res.getLong("id"))
                    .task(task)
                    .creationTime(res.getObject("creation_time", LocalDateTime.class))
                    .startTime(res.getObject("start_time", LocalDateTime.class))
                    .endTime(res.getObject("end_time", LocalDateTime.class))
                    .currentStatus(res.getString("event"))
                    .build()
                    ;
        }));
    }
}
