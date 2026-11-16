package br.com.fiap.lotamenos.controller;

import br.com.fiap.lotamenos.bo.ViagemBO;
import br.com.fiap.lotamenos.dto.AceiteRequest;
import br.com.fiap.lotamenos.dto.PlanejarViagemRequest;
import br.com.fiap.lotamenos.dto.ViagemResponse;
import br.com.fiap.lotamenos.exception.ErroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/viagens")
@Tag(name = "Viagens", description = "Planejamento da janela ideal de saída e registro do aceite da sugestão")
public class ViagemController {

    private final ViagemBO viagemBO;

    public ViagemController(ViagemBO viagemBO) {
        this.viagemBO = viagemBO;
    }

    @PostMapping("/planejar")
    @Operation(summary = "Planeja a viagem e sugere o melhor horário de saída",
            description = "Testa os horários de 15 em 15 min da janela (máx. 3 h), calcula o índice de cada estação "
                    + "do trecho e escolhe o horário com menor lotação no ponto mais crítico. Para IDOSO e "
                    + "MOBILIDADE_REDUZIDA só aceita horários com nível ≤ MODERADO.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    examples = @ExampleObject(name = "Artur Alvim → Sé no pico da manhã", value = """
                            {"usuarioId": 1, "origemId": 17, "destinoId": 6,
                             "janelaInicio": "06:00", "janelaFim": "08:00", "horarioDesejado": "07:00"}
                            """))))
    @ApiResponse(responseCode = "201", description = "Consulta registrada com a sugestão")
    @ApiResponse(responseCode = "404", description = "Usuário ou estação não encontrados",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Estação fora do recorte, origem = destino ou janela inválida",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<ViagemResponse> planejar(@Valid @RequestBody PlanejarViagemRequest request) {
        ViagemResponse resposta = viagemBO.planejar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/viagens/{id}")
                .buildAndExpand(resposta.consultaId()).toUri();
        return ResponseEntity.created(uri).body(resposta);
    }

    @GetMapping
    @Operation(summary = "Lista as consultas de viagem", description = "Mais recentes primeiro.")
    public List<ViagemResponse> listar() {
        return viagemBO.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma consulta de viagem pelo id")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ViagemResponse buscar(@PathVariable Long id) {
        return viagemBO.buscar(id);
    }

    @PutMapping("/{id}/aceite")
    @Operation(summary = "Registra se o usuário aceitou a sugestão",
            description = "Alimenta o indicador de impacto (GET /impacto).")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ViagemResponse registrarAceite(@PathVariable Long id, @Valid @RequestBody AceiteRequest request) {
        return viagemBO.registrarAceite(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma consulta de viagem")
    @ApiResponse(responseCode = "204", description = "Consulta excluída")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        viagemBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
