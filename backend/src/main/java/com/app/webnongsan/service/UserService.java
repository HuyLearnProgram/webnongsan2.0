package com.app.webnongsan.service;

import com.app.webnongsan.domain.User;
import com.app.webnongsan.domain.response.PaginationDTO;
import com.app.webnongsan.domain.response.user.CreateUserDTO;
import com.app.webnongsan.domain.response.user.UpdateUserDTO;
import com.app.webnongsan.domain.response.user.UserDTO;
import com.app.webnongsan.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(User user) {
        //hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        return this.userRepository.save(user);
    }

    public boolean isExistedEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public boolean isExistedId(long id) {
        return this.userRepository.existsById(id);
    }

    public void delete(long id) {
        this.userRepository.deleteById(id);
    }

    public CreateUserDTO convertToCreateDTO(User user) {
        CreateUserDTO res = new CreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setStatus(user.getStatus());
        return res;
    }

    public UserDTO convertToUserDTO(User user) {
        UserDTO res = new UserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAddress(user.getAddress());
        res.setStatus(user.getStatus());
        res.setPhone(user.getPhone());
        res.setAvatarUrl(user.getAvatarUrl());
        return res;
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public PaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable) {
        Page<User> userPage = this.userRepository.findAll(pageable);

        PaginationDTO p = new PaginationDTO();
        PaginationDTO.Meta meta = new PaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());

        p.setMeta(meta);

        // remove sensitive data like password
        List<UserDTO> listUser = userPage.getContent()
                .stream().map(this::convertToUserDTO)
                .collect(Collectors.toList());

        p.setResult(listUser);
        return p;
    }

    public User update(User reqUser) {
        User currentUser = this.getUserById(reqUser.getId());
        if (currentUser != null) {
            //currentUser.setEmail(reqUser.getEmail());
            currentUser.setName(reqUser.getName());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setPhone(reqUser.getPhone());
            currentUser.setAvatarUrl(reqUser.getAvatarUrl());
            currentUser.setStatus(reqUser.getStatus());
            //currentUser.setPassword(passwordEncoder.encode(reqUser.getPassword()));
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public UpdateUserDTO convertToUpdateUserDTO(User user) {
        UpdateUserDTO u = new UpdateUserDTO();
        u.setId(user.getId());
        u.setEmail(user.getEmail());
        u.setName(user.getName());
        u.setPhone(user.getPhone());
        u.setAddress(user.getAddress());
        u.setStatus(user.getStatus());
        u.setAvatarUrl(user.getAvatarUrl());
        return u;
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}

