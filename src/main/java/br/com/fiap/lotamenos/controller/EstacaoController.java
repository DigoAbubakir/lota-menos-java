package br.com.fiap.lotamenos.controller;

import br.com.fiap.lotamenos.bo.EstacaoBO;
import br.com.fiap.lotamenos.dto.EstacaoRequest;
import br.com.fiap.lotamenos.dto.EstacaoResponse;
import br.com.fiap.lotamenos.dto.LotacaoAtualResponse;
import br.com.fiap.lotamenos.exception.ErroResponse;
import br.com.fiap.lotamenos.model.Sentido;
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
@RequestMapping("/estacoes")
@Tag(name = "Estações", description = "Estações e lotação atual (Índice LotaMenos)")
public class EstacaoController {

    private final EstacaoBO estacaoBO;

    public EstacaoController(EstacaoBO estacaoBO) {
        this.estacaoBO = estacaoBO;
    }

    @GetMapping
    @Operation(summary = "Lista as estações", description = "Ordenadas por nome.")
    public List<EstacaoResponse> listar() {
        return estacaoBO.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma estação pelo id")
    @ApiResponse(responseCode = "404", description = "Estação não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public EstacaoResponse buscar(@PathVariable Long id) {
        return estacaoBO.buscar(id);
    }

    @GetMapping("/{id}/lotacao-atual")
    @Operation(summary = "Lotação da estação agora",
            description = "Índice LotaMenos (0–100) e nível, combinando o histórico da faixa atual com os "
                    + "relatos dos últimos 30 minutos ponderados pela reputação. Só para estações da Linha 3.")
    @ApiResponse(responseCode = "200", description = "Índice calculado")
    @ApiResponse(responseCode = "422", description = "Estação fora do recorte da Linha 3",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public LotacaoAtualResponse lotacaoAtual(
            @PathVariable Long id,
            @Parameter(description = "Sentido do trem", example = "SENTIDO_BARRA_FUNDA")
            @RequestParam Sentido sentido) {
        return estacaoBO.lotacaoAtual(id, sentido);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma estação")
    @ApiResponse(responseCode = "201", description = "Estação criada (header Location com a URL)")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Nome já existe",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<EstacaoResponse> criar(@Valid @RequestBody EstacaoRequest request) {
        EstacaoResponse criada = estacaoBO.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma estação")
    @ApiResponse(responseCode = "404", description = "Estação não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public EstacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody EstacaoRequest request) {
        return estacaoBO.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma estação")
    @ApiResponse(responseCode = "204", description = "Estação excluída")
    @ApiResponse(responseCode = "409", description = "Estação possui relatos/históricos/consultas vinculados",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        estacaoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
