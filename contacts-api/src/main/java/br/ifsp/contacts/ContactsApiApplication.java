package br.ifsp.contacts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
* Classe principal do projeto Spring
* anotação @SpringBootApplication habilita as configurações automaticas do spring
* e indica que esta classe que vai iniciar a aplicação*/

@SpringBootApplication
public class ContactsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactsApiApplication.class, args);
	}

}
