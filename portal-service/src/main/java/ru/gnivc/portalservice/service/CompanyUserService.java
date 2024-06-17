package ru.gnivc.portalservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.portalservice.dto.input.UserDto;
import ru.gnivc.portalservice.dto.output.CompanyUserDto;

import java.util.List;

public interface CompanyUserService {
    ResponseEntity<String> createClientUser(String companyName, UserDto userDto);
    List<CompanyUserDto> getCompanyUsers(String companyName);
}
