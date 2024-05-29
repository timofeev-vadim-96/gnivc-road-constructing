package ru.gnivc.portalservice.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.gnivc.portalservice.dto.output.CompanyUserDto;
import ru.gnivc.portalservice.dto.output.SimpleCompanyDto;
import ru.gnivc.portalservice.model.CompanyEntity;
import ru.gnivc.portalservice.util.ClientRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomQueriesDao {
    private final CompanyDao companyDao;
    private final JdbcTemplate jdbcTemplate;

    public Optional<List<CompanyUserDto>> getCompanyUsers(String companyName) {
        Optional<CompanyEntity> id = companyDao.findByName(companyName);
        if (id.isEmpty()) return Optional.empty();
        else {
            return Optional.of(jdbcTemplate.query("" +
                    "select users.id, users.email, users.first_name, users.last_name, " +
                    "company_user.user_role from users " +
                    "left join company_user " +
                    "on users.id = company_user.user_id " +
                    "where company_user.company_id = " + id, new RowMapper<CompanyUserDto>() {
                @Override
                public CompanyUserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new CompanyUserDto(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("user_role"));
                }
            }));
        }
    }

    public List<SimpleCompanyDto> getCompanies() {
        return jdbcTemplate.query("" +
                "select id, name, inn from companies", new RowMapper<SimpleCompanyDto>() {
            @Override
            public SimpleCompanyDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new SimpleCompanyDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("inn")
                );
            }
        });
    }

    public Optional<Integer> countCompanyUsersWithSpecificRole(String companyName, ClientRole role) {
        Optional<CompanyEntity> id = companyDao.findByName(companyName);
        if (id.isEmpty()) return Optional.empty();
        else {
            String userRole = role.name();
            return Optional.ofNullable(jdbcTemplate.queryForObject("" +
                    "select count(*) from users " +
                    "left join company_user " +
                    "on users.id = company_user.user_id " +
                    "where company_user.company_id = " + id +
                    " and company_user.user_role = " + userRole, Integer.class));
        }
    }
}
