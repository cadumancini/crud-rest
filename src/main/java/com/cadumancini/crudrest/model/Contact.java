package com.cadumancini.crudrest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@AllArgsConstructor // automatically creates a constructor with all class attributes as args
@NoArgsConstructor // automatically creates an empty constructor (no args)
@Data // automatically creates toString, equals, hashCode, getters and setters
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String email;
}
