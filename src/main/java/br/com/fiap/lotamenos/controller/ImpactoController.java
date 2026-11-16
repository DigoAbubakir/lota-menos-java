package br.com.fiap.lotamenos.controller;

import br.com.fiap.lotamenos.bo.ImpactoBO;
import br.com.fiap.lotamenos.dto.ImpactoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/impacto")
@Tag(name = "Impacto", description = "Indicador de impacto da CitizenTech")
public class ImpactoController {
    private final ImpactoBO impactoBO;

    public ImpactoController(ImpactoBO impactoBO) {
        this.impactoBO = impactoBO;
    }

    @GetMapping
    @Operation(summary = "Indicador de impacto",
            description = "Total de consultas, % de sugestões aceitas, redução média de pontos de lotação nas "
                    + "aceitas, total de relatos e usuários ativos nos últimos 30 dias.")
    public ImpactoResponse calcular() {
        return impactoBO.calcular();
    }
}
