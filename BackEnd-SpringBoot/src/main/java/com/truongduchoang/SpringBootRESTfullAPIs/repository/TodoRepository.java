package com.truongduchoang.SpringBootRESTfullAPIs.repository;

import java.util.List;
// import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.truongduchoang.SpringBootRESTfullAPIs.models.Todo;



@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>{
    List<Todo> findByUserName(String userName);

}
