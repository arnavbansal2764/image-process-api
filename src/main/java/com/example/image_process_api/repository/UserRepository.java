package com.example.image_process_api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.image_process_api.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String>{
    public long count();
    public Optional<User> findByUsername(String username);
}
