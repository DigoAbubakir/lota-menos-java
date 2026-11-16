package br.com.fiap.lotamenos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class LotaMenosApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));

        System.setProperty("oracle.jdbc.timezoneAsRegion", "false");
        SpringApplication.run(LotaMenosApplication.class, args);
    }
}
