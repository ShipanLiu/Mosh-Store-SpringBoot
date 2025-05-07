package com.codewithmosh.store.repository;

import com.codewithmosh.store.entity.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {

    // Map<Email : User>
    private Map<String, User> users = new HashMap();


    @Override
    public void save(User user) {
        users.put(user.getEmail(), user);

        System.out.println("ALL saved users: " + users.values());
    }

    @Override
    public User findByEmail(String email) {
        return users.getOrDefault(email, null);
    }
}
