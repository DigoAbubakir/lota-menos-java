package br.com.fiap.lotamenos.controller;

import br.com.fiap.lotamenos.bo.LinhaBO;
import br.com.fiap.lotamenos.dto.EstacaoNaLinhaResponse;
import br.com.fiap.lotamenos.dto.LinhaRequest;
import br.com.fiap.lotamenos.dto.LinhaResponse;
import br.com.fiap.lotamenos.exception.ErroResponse;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/linhas")
@Tag(name = "Linhas", description = "Linhas de metrô e CPTM do recorte (Linha 3-Vermelha e conexões)")
public class LinhaController {

    private final LinhaBO linhaBO;

    public LinhaController(LinhaBO linhaBO) {
        this.linhaBO = linhaBO;
    }

    @GetMapping
    @Operation(summary = "Lista as linhas", description = "Retorna todas as linhas ordenadas pelo número.")
    public List<LinhaResponse> listar() {
        return linhaBO.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma linha pelo id")
    @ApiResponse(responseCode = "200", description = "Linha encontrada")
    @ApiResponse(responseCode = "404", description = "Linha não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public LinhaResponse buscar(@PathVariable Long id) {
        return linhaBO.buscar(id);
    }

    @GetMapping("/{id}/estacoes")
    @Operation(summary = "Lista as estações da linha em ordem de percurso",
            description = "Para a Linha 3-Vermelha retorna as 18 estações, de Palmeiras-Barra Funda (1) "
                    + "a Corinthians-Itaquera (18). Nas linhas de conexão, só os pontos de integração do recorte.")
    @ApiResponse(responseCode = "404", description = "Linha não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public List<EstacaoNaLinhaResponse> listarEstacoes(@PathVariable Long id) {
        return linhaBO.listarEstacoes(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma linha")
    @ApiResponse(responseCode = "201", description = "Linha criada (header Location com a URL)")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Número de linha já existe",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<LinhaResponse> criar(@Valid @RequestBody LinhaRequest request) {
        LinhaResponse criada = linhaBO.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma linha")
    @ApiResponse(responseCode = "200", description = "Linha atualizada")
    @ApiResponse(responseCode = "404", description = "Linha não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public LinhaResponse atualizar(@PathVariable Long id, @Valid @RequestBody LinhaRequest request) {
        return linhaBO.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma linha")
    @ApiResponse(responseCode = "204", description = "Linha excluída")
    @ApiResponse(responseCode = "409", description = "Linha possui estações/históricos/relatos vinculados",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        linhaBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
