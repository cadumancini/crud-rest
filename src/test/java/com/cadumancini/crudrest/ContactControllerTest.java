package com.cadumancini.crudrest;

import com.cadumancini.crudrest.model.Contact;
import com.cadumancini.crudrest.repository.ContactRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ContactControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @MockBean
    private ContactRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void listContactsShouldReturnStatusCode200() {
        List<Contact> contacts = asList(new Contact(1L, "Carlos", "carlos@test.com"),
                new Contact(2L, "Carlos2", "carlos2@test.com"),
                new Contact(3L, "Carlos3", "carlos3@test.com"));
        BDDMockito.when(repository.findAll()).thenReturn(contacts);
        ResponseEntity<String> response = restTemplate.getForEntity("/contacts/", String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void listContactsShouldReturnStatusCode200WhenEmpty() {
        BDDMockito.when(repository.findAll()).thenReturn(null);
        ResponseEntity<String> response = restTemplate.getForEntity("/contacts/", String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getContactByIdShouldReturnStatusCode200() {
        Contact contact = new Contact(1L, "Carlos", "carlos@test.com");
        BDDMockito.when(repository.findById(contact.getId())).thenReturn(Optional.of(contact));
        ResponseEntity<Contact> response = restTemplate.getForEntity("/contacts/{id}", Contact.class, contact.getId());
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getContactByIdShouldReturnStatusCode404WhenContactDoesNotExists() {
        ResponseEntity<Contact> response = restTemplate.getForEntity("/contacts/{id}", Contact.class, -1L);
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deleteWhenContactExistsShouldReturnStatusCode200() {
        Contact contact = new Contact(1L, "Carlos", "carlos@test.com");
        BDDMockito.when(repository.findById(contact.getId())).thenReturn(Optional.of(contact));
        BDDMockito.doNothing().when(repository).deleteById(1L);
        ResponseEntity<String> exchange = restTemplate.exchange("/contacts/{id}", HttpMethod.DELETE, null, String.class, 1L);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deleteWhenContactDoesNotExistShouldReturnStatusCode404() {
        ResponseEntity<String> exchange = restTemplate.exchange("/contacts/{id}", HttpMethod.DELETE, null, String.class, -1L);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deleteWithMockMvcWhenContactDoesNotExistShouldReturnStatusCode404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/contacts/{id}", -1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createShouldPersistDataAndReturnStatusCode200() {
        Contact contact = new Contact(1L, "Carlos", "carlos@test.com");
        BDDMockito.when(repository.save(contact)).thenReturn(contact);
        ResponseEntity<Contact> response = restTemplate.postForEntity("/contacts/", contact, Contact.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isNotNull();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateShouldChangeDataAndReturnStatusCode200() throws Exception {
        Contact contact = new Contact(1L, "Carlos", "carlos@test.com");
        BDDMockito.when(repository.findById(contact.getId())).thenReturn(Optional.of(contact));
        BDDMockito.when(repository.save(contact)).thenReturn(contact);
        contact.setName("Carlos2");
        mockMvc.perform(MockMvcRequestBuilders
                .put("/contacts/{id}", contact.getId())
                .content(asJsonString(contact))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Carlos2"));
    }
}
