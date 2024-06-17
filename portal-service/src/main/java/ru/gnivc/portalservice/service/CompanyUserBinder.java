package ru.gnivc.portalservice.service;

import ru.gnivc.portalservice.util.ClientRole;

public interface CompanyUserBinder {
    void bindUserWithCompany(String email, String companyName, ClientRole role);
}
