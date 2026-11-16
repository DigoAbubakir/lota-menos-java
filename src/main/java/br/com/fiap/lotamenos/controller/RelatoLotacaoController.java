package br.com.fiap.lotamenos.controller;

import br.com.fiap.lotamenos.bo.RelatoLotacaoBO;
import br.com.fiap.lotamenos.dto.RelatoRequest;
import br.com.fiap.lotamenos.dto.RelatoResponse;
import br.com.fiap.lotamenos.dto.RelatoUpdateRequest;
import br.com.fiap.lotamenos.exception.ErroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/relatos")
@Tag(name = "Relatos", description = "Relatos de lotação em tempo real enviados pelos passageiros")
public class RelatoLotacaoController {
    private final RelatoLotacaoBO relatoBO;

    public RelatoLotacaoController(RelatoLotacaoBO relatoBO) {
        this.relatoBO = relatoBO;
    }

    @GetMapping
    @Operation(summary = "Lista relatos", description = "Filtros opcionais por estação e pelos últimos N minutos.")
    public List<RelatoResponse> listar(
            @Parameter(description = "Id da estação", example = "8") @RequestParam(required = false) Long estacaoId,
            @Parameter(description = "Somente relatos dos últimos N minutos", example = "30")
            @RequestParam(required = false) Integer ultimosMinutos) {
        return relatoBO.listar(estacaoId, ultimosMinutos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um relato pelo id")
    @ApiResponse(responseCode = "404", description = "Relato não encontrado",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public RelatoResponse buscar(@PathVariable Long id) {
        return relatoBO.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Relata a lotação de uma estação",
            description = "Atualiza a reputação do usuário: +0.05 se o relato for coerente com o índice atual "
                    + "(diferença ≤ 20), senão −0.10. Antispam: 1 relato por estação a cada 10 minutos.")
    @ApiResponse(responseCode = "201", description = "Relato registrado (header Location com a URL)")
    @ApiResponse(responseCode = "409", description = "Relato duplicado (menos de 10 min)",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Estação fora do recorte ou data no futuro",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<RelatoResponse> registrar(@Valid @RequestBody RelatoRequest request) {
        RelatoResponse criado = relatoBO.registrar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Corrige nível e sentido de um relato", description = "Não recalcula a reputação.")
    @ApiResponse(responseCode = "404", description = "Relato não encontrado",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public RelatoResponse atualizar(@PathVariable Long id, @Valid @RequestBody RelatoUpdateRequest request) {
        return relatoBO.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um relato")
    @ApiResponse(responseCode = "204", description = "Relato excluído")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        relatoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
