package ru.gnivc.portalservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.portalservice.dto.input.UserDto;
import ru.gnivc.portalservice.dto.output.CompanyCardDto;
import ru.gnivc.portalservice.dto.output.CompanyUserDto;
import ru.gnivc.portalservice.dto.output.SimpleCompanyDto;
import ru.gnivc.portalservice.model.CompanyEntity;
import ru.gnivc.portalservice.service.CompanyService;
import ru.gnivc.portalservice.service.CompanyUserService;
import ru.gnivc.portalservice.util.ClientRole;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/v1/company")
public class CompanyController {
    private final CompanyService companyService;

    private final CompanyUserService userService;

    /**
     * Creating a company
     */
    @PostMapping()
    public ResponseEntity<CompanyEntity> createCompany(
            @RequestParam String inn,
            @RequestParam String email) {
        return companyService.createCompany(inn, email);
    }

    /**
     * Updating the company
     */
    @PutMapping()
    public ResponseEntity<CompanyEntity> updateCompany(@RequestParam String inn,
                                                       @RequestParam String companyName) {
        return companyService.updateCompany(inn, companyName);
    }

    /**
     * Deleting a company
     */
    @DeleteMapping()
    public ResponseEntity<Void> removeCompany(@RequestParam String companyName) {
        return companyService.remove(companyName);
    }

    /**
     * Viewing a company
     */
    @GetMapping()
    public ResponseEntity<CompanyCardDto> getCompany(@RequestParam String companyName) {
        CompanyCardDto companyCard = companyService.getCompanyCard(companyName);
        if (companyCard != null) {
            return new ResponseEntity<>(companyCard, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The company with id = " + companyName + " was not found");
        }
    }

    /**
     * Getting a list of companies with basic information
     */
    @GetMapping("/list")
    public ResponseEntity<List<SimpleCompanyDto>> getCompanies(@RequestParam String companyName) {
        List<SimpleCompanyDto> companies = companyService.getCompanies();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    /**
     * Getting a list of roles available in companies
     */
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles(@RequestParam String companyName) {
        List<String> roles = companyService.getClientsRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    /**
     * Getting a list of company employees and their roles
     */
    @GetMapping("/users")
    public ResponseEntity<List<CompanyUserDto>> getCompanyUsers(@RequestParam String companyName) {
        List<CompanyUserDto> users = userService.getCompanyUsers(companyName);
        if (users != null) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The company with id = " + companyName + " was not found");
        }
    }

    /**
     * Registration of new users by the ADMIN
     */
    @PostMapping("/admin/user")
    public ResponseEntity<String> registerCompanyUserByAdmin(
            @RequestParam(name = "companyName") String companyName,
            @Valid @RequestBody UserDto userDto) {
        if (userDto.getClientRole() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The user's role in the company is not defined");
        } else {
            return userService.createClientUser(companyName, userDto);
        }
    }

    /**
     * Registration of new users (drivers) by the LOGIST
     */
    @PostMapping("/logist/user")
    public ResponseEntity<String> registerCompanyUserByLogist(
            @RequestParam(name = "companyName") String companyName,
            @Valid @RequestBody UserDto userDto) {
        if (userDto.getClientRole() == null || !userDto.getClientRole().equals(ClientRole.ROLE_DRIVER)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The role of the user registered by the " +
                    "logistician in the company should not be different from driver");
        } else {
            return userService.createClientUser(companyName, userDto);
        }
    }
}
