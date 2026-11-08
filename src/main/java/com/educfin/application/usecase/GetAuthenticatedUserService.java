package com.educfin.application.usecase;

import com.educfin.application.dto.UserOutput;
import com.educfin.application.exception.UserNotFoundException;
import com.educfin.application.port.in.GetAuthenticatedUserUseCase;
import com.educfin.application.port.out.UserRepository;
import com.educfin.domain.user.UserId;

public class GetAuthenticatedUserService implements GetAuthenticatedUserUseCase {

    private final UserRepository userRepository;

    public GetAuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserOutput execute(Input input) {
        return userRepository.findById(UserId.of(input.userId()))
                .map(UserOutput::from)
                .orElseThrow(UserNotFoundException::new);
    }
}
