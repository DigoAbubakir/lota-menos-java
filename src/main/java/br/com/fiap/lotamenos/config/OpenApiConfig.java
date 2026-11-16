package br.com.fiap.lotamenos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI lotaMenosOpenApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/").description("Servidor atual")))
                .info(new Info()
                        .title("LotaMenos API")
                        .version("1.0.0")
                        .description("""
                                CitizenTech para quem usa metrô e trem em São Paulo no horário de pico.
                                Calcula a lotação prevista (histórico + relatos em tempo real dos passageiros) \
                                e sugere o melhor horário de saída dentro de uma janela.

                                **Recorte:** Linha 3-Vermelha (18 estações) + conexões com a Linha 1 e a CPTM.
                                **ODS:** 9, 10 e 11. **Todos os dados são simulados** para fins acadêmicos.

                                Global Solution FIAP 2026.2 — Domain Driven Design (Java).""")
                        .contact(new Contact().name("Equipe Lota Menos"))
                        .license(new License().name("Uso acadêmico")));
    }
}
