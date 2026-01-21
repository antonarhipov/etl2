package org.example.etl2;

import org.springframework.boot.SpringApplication;

public class TestEtl2Application {

    public static void main(String[] args) {
        SpringApplication.from(Etl2Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
