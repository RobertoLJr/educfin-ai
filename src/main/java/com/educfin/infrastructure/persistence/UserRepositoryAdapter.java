package com.educfin.infrastructure.persistence;

import com.educfin.application.exception.EmailAlreadyInUseException;
import com.educfin.application.port.out.UserRepository;
import com.educfin.domain.user.Email;
import com.educfin.domain.user.User;
import com.educfin.domain.user.UserId;
import com.educfin.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.educfin.infrastructure.persistence.repository.SpringDataUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter que implementa a porta {@link UserRepository} usando Spring Data JPA.
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;

    public UserRepositoryAdapter(SpringDataUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        try {
            var saved = jpaRepository.saveAndFlush(UserPersistenceMapper.toEntity(user));
            return UserPersistenceMapper.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyInUseException();
        }
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
}
