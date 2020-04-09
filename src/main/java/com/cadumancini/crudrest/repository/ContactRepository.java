package com.cadumancini.crudrest.repository;

import com.cadumancini.crudrest.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository <Contact, Long> {
    public Contact findByName(String name);
}
