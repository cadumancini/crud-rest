package com.cadumancini.crudrest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor // automatically creates a constructor with all class attributes as args
@NoArgsConstructor // automatically creates an empty constructor (no args)
@Data // automatically creates toString, equals, hashCode, getters and setters
@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "The contact's name field is mandatory.")
    private String name;

    @NotEmpty(message = "The contact's email field is mandatory.")
    @Email(message = "The e-mail must be a valid address.")
    private String email;

    public Contact(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
