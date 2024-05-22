package ru.gnivc.portalservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.portalservice.model.CustomUser;

@Repository
public interface UserDao extends JpaRepository<CustomUser, Long> {
}
