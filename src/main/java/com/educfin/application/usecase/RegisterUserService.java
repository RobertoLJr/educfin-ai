package com.educfin.application.usecase;

import com.educfin.application.dto.UserOutput;
import com.educfin.application.exception.EmailAlreadyInUseException;
import com.educfin.application.port.in.RegisterUserUseCase;
import com.educfin.application.port.out.PasswordHasher;
import com.educfin.application.port.out.UserRepository;
import com.educfin.domain.user.Email;
import com.educfin.domain.user.PasswordPolicy;
import com.educfin.domain.user.User;

import java.time.Clock;
import java.time.Instant;

public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public RegisterUserService(UserRepository userRepository, PasswordHasher passwordHasher, Clock clock) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    @Override
    public UserOutput execute(Input input) {
        Email email = Email.of(input.email());
        PasswordPolicy.validate(input.password());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException();
        }

        User user = User.create(
                input.name(),
                email,
                passwordHasher.hash(input.password()),
                Instant.now(clock)
        );

        return UserOutput.from(userRepository.save(user));
    }
}
