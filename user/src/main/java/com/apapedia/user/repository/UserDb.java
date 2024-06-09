package com.apapedia.user.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.apapedia.user.model.UserModel;

@Repository
public interface UserDb extends JpaRepository<UserModel, UUID> {

    UserModel findByUsernameIgnoreCaseAndIsDeletedFalse(String username);

    UserModel findByIdAndIsDeletedFalse(UUID id);

    UserModel findByUsernameIgnoreCase(String username);

    UserModel findByEmailIgnoreCase(String email);
}