package com.sykora.socialnetworkapp.repository;

import com.sykora.socialnetworkapp.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByGithubId(String email);
}