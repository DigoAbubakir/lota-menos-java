package br.com.fiap.lotamenos.controller;

import br.com.fiap.lotamenos.bo.HistoricoLotacaoBO;
import br.com.fiap.lotamenos.dto.HistoricoRequest;
import br.com.fiap.lotamenos.dto.HistoricoResponse;
import br.com.fiap.lotamenos.exception.ErroResponse;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
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
@RequestMapping("/historicos")
@Tag(name = "Históricos", description = "Histórico de lotação por faixa de 15 minutos (base do Índice LotaMenos)")
public class HistoricoLotacaoController {

    private final HistoricoLotacaoBO historicoBO;

    public HistoricoLotacaoController(HistoricoLotacaoBO historicoBO) {
        this.historicoBO = historicoBO;
    }

    @GetMapping
    @Operation(summary = "Lista históricos",
            description = "Filtros opcionais. Sem filtros retorna todos os ~8.200 registros — prefira filtrar.")
    public List<HistoricoResponse> listar(
            @Parameter(description = "Id da estação", example = "8") @RequestParam(required = false) Long estacaoId,
            @Parameter(description = "Sentido", example = "SENTIDO_BARRA_FUNDA")
            @RequestParam(required = false) Sentido sentido,
            @Parameter(description = "Tipo de dia", example = "UTIL") @RequestParam(required = false) TipoDia tipoDia) {
        return historicoBO.listar(estacaoId, sentido, tipoDia);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um histórico pelo id")
    @ApiResponse(responseCode = "404", description = "Histórico não encontrado",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public HistoricoResponse buscar(@PathVariable Long id) {
        return historicoBO.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um registro de histórico")
    @ApiResponse(responseCode = "201", description = "Histórico criado (header Location com a URL)")
    @ApiResponse(responseCode = "422", description = "Registro duplicado ou estação fora da linha",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<HistoricoResponse> criar(@Valid @RequestBody HistoricoRequest request) {
        HistoricoResponse criado = historicoBO.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um registro de histórico")
    @ApiResponse(responseCode = "404", description = "Histórico não encontrado",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public HistoricoResponse atualizar(@PathVariable Long id, @Valid @RequestBody HistoricoRequest request) {
        return historicoBO.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um registro de histórico")
    @ApiResponse(responseCode = "204", description = "Histórico excluído")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        historicoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
