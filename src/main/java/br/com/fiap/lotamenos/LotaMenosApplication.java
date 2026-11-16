package br.com.fiap.lotamenos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Ponto de entrada da API LotaMenos.
 */
@SpringBootApplication
public class LotaMenosApplication {

    public static void main(String[] args) {
        // O Render roda em UTC: sem isso, a lógica de faixas de horário ficaria 3 h adiantada.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        // Evita ORA-01882 (timezone region not found) em alguns servidores Oracle.
        System.setProperty("oracle.jdbc.timezoneAsRegion", "false");
        SpringApplication.run(LotaMenosApplication.class, args);
    }
}
