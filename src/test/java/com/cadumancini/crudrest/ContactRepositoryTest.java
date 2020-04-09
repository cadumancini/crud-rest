package com.cadumancini.crudrest;

		import com.cadumancini.crudrest.model.Contact;
		import com.cadumancini.crudrest.repository.ContactRepository;
		import org.junit.jupiter.api.BeforeEach;
		import org.junit.jupiter.api.Test;
		import org.junit.runner.RunWith;
		import org.springframework.beans.factory.annotation.Autowired;
		import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
		import org.springframework.test.context.junit4.SpringRunner;

		import javax.validation.ConstraintViolation;
		import javax.validation.Validation;
		import javax.validation.Validator;
		import java.util.Set;

		import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class ContactRepositoryTest {

	@Autowired
	private ContactRepository repository;

	private Validator validator;

	@BeforeEach
	public void setup() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	public void createShouldPersistData() {
		Contact contact = new Contact("Carlos", "carlos@test.com");
		repository.save(contact);

		Contact found = repository.findByName("Carlos");

		assertThat(contact.getId()).isNotNull();
		assertThat(contact.getName()).isEqualTo(found.getName());
		assertThat(contact.getEmail()).isEqualTo(found.getEmail());
	}

	@Test
	public void deleteShouldRemoveData() {
		Contact contact = new Contact("TestUser", "test@test.com");
		repository.save(contact);
		repository.deleteById(contact.getId());

		Contact found = repository.findByName("TestUser");

		assertThat(found).isNull();
	}

	@Test
	public void updateShouldChangeAndPersistData() {
		Contact contact = new Contact("Carlos", "carlos@test.com");
		repository.save(contact);
		contact.setName("Carlos_2");
		contact.setEmail("carlos_2@test.com");
		repository.save(contact);

		Contact found = repository.findByName("Carlos_2");

		assertThat(contact.getId()).isNotNull();
		assertThat(contact.getName()).isEqualTo(found.getName());
		assertThat(contact.getEmail()).isEqualTo(found.getEmail());
	}

	@Test
	public void createWhenNameIsNullShouldThrowException() {
		Contact contact = new Contact();
		contact.setEmail("carlos@test");
		Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
		assertThat("The contact's name field is mandatory.").isEqualTo(violations.iterator().next().getMessage());

		assertThat(violations.size()).isEqualTo(1);
	}

	@Test
	public void createWhenEmailIsNullShouldThrowException() {
		Contact contact = new Contact();
		contact.setName("Carlos");
		Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
		assertThat("The contact's email field is mandatory.").isEqualTo(violations.iterator().next().getMessage());

		assertThat(violations.size()).isEqualTo(1);
	}

	@Test
	public void createWhenEmailIsNotValidShouldThrowException() {
		Contact contact = new Contact();
		contact.setName("Carlos");
		contact.setEmail("test");
		Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
		assertThat("The e-mail must be a valid address.").isEqualTo(violations.iterator().next().getMessage());

		assertThat(violations.size()).isEqualTo(1);
	}

	@Test
	public void createWhenNameAndEmailAreNullShouldThrowException() {
		Contact contact = new Contact();
		Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
		assertThat(violations.size()).isEqualTo(2);
	}

	@Test
	public void createWhenContactIsValidShouldNotThrowException() {
		Contact contact = new Contact("Carlos", "carlos@test.com");
		Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
		assertThat(violations.size()).isEqualTo(0);
	}
}
