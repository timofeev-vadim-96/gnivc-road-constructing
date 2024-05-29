package ru.gnivc.portalservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.portalservice.dto.input.UserDto;
import ru.gnivc.portalservice.dto.output.CompanyCardDto;
import ru.gnivc.portalservice.dto.output.CompanyUserDto;
import ru.gnivc.portalservice.dto.output.SimpleCompanyDto;
import ru.gnivc.portalservice.model.CompanyEntity;
import ru.gnivc.portalservice.service.CompanyService;
import ru.gnivc.portalservice.service.UserService;
import ru.gnivc.portalservice.util.ClientRole;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/v1/company")
public class CompanyController {
    private final CompanyService companyService;
    private final UserService userService;

    /**
     * Creating a company
     */
    @PostMapping()
    public ResponseEntity<CompanyEntity> createCompany(@RequestParam String inn,
                                              @RequestParam String email){
        CompanyEntity company = companyService.createCompany(inn, email);
        if (company == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(company, HttpStatus.CREATED);
    }

    /**
     * Updating the company
     */
    @PutMapping()
    public ResponseEntity<Void> updateCompany(@RequestParam long id){
        return null;
    }

    /**
     * Deleting a company
     */
    @DeleteMapping()
    public ResponseEntity<Void> removeCompany(@RequestParam long id){
        return null;
    }

    /**
     * Viewing a company
     */
    @GetMapping()
    public ResponseEntity<CompanyCardDto> getCompany(@RequestParam String companyId){
        CompanyCardDto companyCard = companyService.getCompanyCard(companyId);
        if (companyCard != null){
            return new ResponseEntity<>(companyCard, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The company with id = " + companyId + " was not found");
        }
    }

    /**
     * Getting a list of companies with basic information
     */
    @GetMapping("/list")
    public ResponseEntity<List<SimpleCompanyDto>> getCompanies(@RequestParam String companyId){ //solely for defining user roles on the Gateway side
        List<SimpleCompanyDto> companies = companyService.getCompanies();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    /**
     * Getting a list of roles available in companies
     */
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles(@RequestParam String companyId){ //solely for defining user roles on the Gateway side
        List<String> roles = companyService.getClientsRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    /**
     * Getting a list of company employees and their roles
     */
    @GetMapping("/users")
    public ResponseEntity<List<CompanyUserDto>> getCompanyUsers(@RequestParam String companyId){
        List<CompanyUserDto> users = userService.getCompanyUsers(companyId);
        if (users != null){
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The company with id = " + companyId + " was not found");
        }
    }

    /**
     * Registration of new users by the ADMIN
     */
    @PostMapping("/admin/user")
    public ResponseEntity<String> registerCompanyUserByAdmin(
            @RequestParam(name = "companyId") String companyId,
            @Valid @RequestBody UserDto userDto){
        if (userDto.getClientRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user's role in the company is not defined");
        }
        else return userService.createClientUser(companyId, userDto);
    }

    /**
     * Registration of new users (drivers) by the LOGIST
     */
    @PostMapping("/logist/user")
    public ResponseEntity<String> registerCompanyUserByLogist(
            @RequestParam(name = "companyId") String companyId,
            @Valid @RequestBody UserDto userDto){
        if (userDto.getClientRole() == null || !userDto.getClientRole().equals(ClientRole.ROLE_DRIVER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The role of the user registered by the " +
                    "logistician in the company should not be different from driver");
        }
        else return userService.createClientUser(companyId, userDto);
    }
}
