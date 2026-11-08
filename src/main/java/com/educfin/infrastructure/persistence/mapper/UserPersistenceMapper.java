package com.educfin.infrastructure.persistence.mapper;

import com.educfin.domain.user.Email;
import com.educfin.domain.user.User;
import com.educfin.domain.user.UserId;
import com.educfin.infrastructure.persistence.entity.UserJpaEntity;

/**
 * Converte entre o modelo de domínio e o modelo de persistência.
 */
public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getId().value(),
                user.getName(),
                user.getEmail().value(),
                user.getPasswordHash(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
        return User.restore(
                UserId.of(entity.getId()),
                entity.getName(),
                Email.of(entity.getEmail()),
                entity.getPasswordHash(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
