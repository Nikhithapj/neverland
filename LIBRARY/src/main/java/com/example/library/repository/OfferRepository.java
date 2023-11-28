package com.example.library.repository;

import com.example.library.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer,Long> {

    Offer findById(long id);
}
