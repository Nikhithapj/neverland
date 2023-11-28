package com.example.library.repository;

import com.example.library.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SizeRepository extends JpaRepository<Size,Long> {

    Size findById(long id);
//    @Modifying
//    @Query



}
