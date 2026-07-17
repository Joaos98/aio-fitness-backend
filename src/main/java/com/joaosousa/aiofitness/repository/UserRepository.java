package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
