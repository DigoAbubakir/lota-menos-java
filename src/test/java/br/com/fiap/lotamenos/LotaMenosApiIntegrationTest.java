package br.com.fiap.lotamenos;

import br.com.fiap.lotamenos.dao.EstacaoDAO;
import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.UsuarioDAO;
import br.com.fiap.lotamenos.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class LotaMenosApiIntegrationTest {
    private static final String DATA_UTIL = "2030-03-12";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private EstacaoDAO estacaoDAO;
    @Autowired
    private UsuarioDAO usuarioDAO;
    @Autowired
    private LinhaDAO linhaDAO;

    private long estacao(String nome) {
        return estacaoDAO.findByNomeIgnoreCase(nome).orElseThrow().getId();
    }

    private long usuario(String email) {
        return usuarioDAO.findAll().stream().filter(u -> u.getEmail().equals(email))
                .map(Usuario::getId).findFirst().orElseThrow();
    }

    @Test
    @DisplayName("POST /viagens/planejar: Artur Alvim → Sé no pico da manhã sugere sair mais cedo")
    void planejarViagem() throws Exception {
        String body = """
                {"usuarioId": %d, "origemId": %d, "destinoId": %d,
                 "janelaInicio": "06:00", "janelaFim": "08:00", "horarioDesejado": "07:00", "data": "%s"}
                """.formatted(usuario("carlos.mendes@exemplo.com"), estacao("Artur Alvim"), estacao("Sé"), DATA_UTIL);

        mvc.perform(post("/viagens/planejar").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/viagens/")))
                .andExpect(jsonPath("$.origem").value("Artur Alvim"))
                .andExpect(jsonPath("$.destino").value("Sé"))
                .andExpect(jsonPath("$.sentido").value("SENTIDO_BARRA_FUNDA"))
                .andExpect(jsonPath("$.nivelDesejado").value("LOTADO"))
                .andExpect(jsonPath("$.horarioSugerido").value("06:00"))
                .andExpect(jsonPath("$.indiceSugerido").value(lessThan(65)))
                .andExpect(jsonPath("$.alertaAcessibilidade").value(false))
                .andExpect(jsonPath("$.dica").value(containsString("Saindo às 06:00 em vez de 07:00")));
    }

    @Test
    @DisplayName("POST /viagens/planejar com janela inválida → 422 no formato ErroResponse")
    void planejarComJanelaInvalida() throws Exception {
        String body = """
                {"usuarioId": %d, "origemId": %d, "destinoId": %d,
                 "janelaInicio": "08:00", "janelaFim": "07:00", "data": "%s"}
                """.formatted(usuario("carlos.mendes@exemplo.com"), estacao("Artur Alvim"), estacao("Sé"), DATA_UTIL);

        mvc.perform(post("/viagens/planejar").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.erro").value("Regra de negócio"))
                .andExpect(jsonPath("$.caminho").value("/viagens/planejar"));
    }

    @Test
    @DisplayName("Validação de campos → 400 com a lista de campos")
    void validacaoDeCampos() throws Exception {
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"invalido\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.length()").value(3));
    }

    @Test
    @DisplayName("Recurso inexistente → 404; rota inexistente → 404; GET / → Swagger")
    void erros404ERedirect() throws Exception {
        mvc.perform(get("/estacoes/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Estação com id 999999 não encontrada."));
        mvc.perform(get("/nao-existe")).andExpect(status().isNotFound());
        mvc.perform(get("/")).andExpect(redirectedUrl("/swagger-ui.html"));
    }

    @Test
    @DisplayName("GET /linhas/{id}/estacoes da Linha 3 → 18 estações em ordem")
    void estacoesDaLinha3() throws Exception {
        long linha3 = linhaDAO.findByNumero(3).orElseThrow().getId();
        mvc.perform(get("/linhas/" + linha3 + "/estacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(18))
                .andExpect(jsonPath("$[0].nome").value("Palmeiras-Barra Funda"))
                .andExpect(jsonPath("$[17].nome").value("Corinthians-Itaquera"));
    }
}
