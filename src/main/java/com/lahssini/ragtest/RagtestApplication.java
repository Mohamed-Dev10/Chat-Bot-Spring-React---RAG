package com.lahssini.ragtest;

import com.lahssini.ragtest.entities.Person;
import com.lahssini.ragtest.repositories.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class RagtestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagtestApplication.class, args);
    }
    @Bean
    public CommandLineRunner commandLineRunner(PersonRepository personRepository){
        return args -> {
            for(int i=0;i<100;i++){
                Person person= Person.builder()
                        .name(UUID.randomUUID().toString().substring(0,8))
                        .email(UUID.randomUUID().toString().substring(0,8)+"@gmail.com")
                        .build();
                personRepository.save(person);
            }
        };
    }

}
