package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotValidException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.validation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service("jpaUserService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (UserValidator.isValidUser(userDto)) {
            Person user = userMapper.userDtoToPerson(userDto);
            log.info("Mapped user: {}", user);
            Person savedUser = userRepository.save(user);
            log.info("Saved user: {}", savedUser);
            return userMapper.personToUserDto(savedUser);
        } else {
            throw new NotValidException("Not valid data: " + userDto);
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto existUser = getUserById(userDto.getId());

        if (userDto.getFullName() != null) {
            existUser.setFullName(userDto.getFullName());
        }

        if (userDto.getTitle() != null) {
            existUser.setTitle(userDto.getTitle());
        }

        if (userDto.getAge() != 0) {
            existUser.setAge(userDto.getAge());
        }

        if (UserValidator.isValidUser(existUser)) {
            existUser = userMapper.personToUserDto(userRepository.save(userMapper.userDtoToPerson(existUser)));
        }

        return existUser;
    }

    @Override
    public UserDto getUserById(Long id) {
        Person user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID=" + id + " not found!"));
        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        try {
            userRepository.deleteById(id);
            log.info("Deleted user with ID={}", id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User with ID=" + id + " not found!");
        }
    }
}
