package com.codewithmosh.store.repository;

import com.codewithmosh.store.entity.User;

public interface UserRepository {

    void save(User user);

    User findByEmail(String email);
}
