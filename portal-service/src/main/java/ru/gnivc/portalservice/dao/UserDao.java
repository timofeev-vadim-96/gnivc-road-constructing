package ru.gnivc.portalservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.portalservice.model.UserEntity;
import xxx.yyy.annotation.AfterReturningLogger;

import java.util.Optional;

@Repository
@AfterReturningLogger
public interface UserDao extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
}
