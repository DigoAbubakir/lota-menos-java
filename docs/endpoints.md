# Endpoints — LotaMenos API

Base URL: `http://localhost:8080` (local) ou https://lota-menos-java.onrender.com (Render). Documentação interativa em `/swagger-ui.html`.

Todos os erros seguem o formato `ErroResponse`:

```json
{ "timestamp": "2026-11-17T07:02:11", "status": 422, "erro": "Regra de negócio",
  "mensagem": "A estação Luz não faz parte do recorte da Linha 3.", "caminho": "/viagens/planejar", "campos": [] }
```

| Código | Quando |
|---|---|
| 400 | JSON mal formatado, campo inválido (lista em `campos`), parâmetro com tipo/enum inválido |
| 404 | Recurso ou rota inexistente |
| 405 | Método HTTP não suportado no caminho |
| 409 | Relato duplicado (antispam) · exclusão de registro com vínculos · violação de unicidade no banco |
| 422 | Regra de negócio (estação fora do recorte, janela inválida, duplicidade de nome/e-mail/número, data no futuro) |
| 500 | Erro inesperado (sem stack trace na resposta; registrado no log do servidor) |

---

## Linhas

| Método | URL | Descrição | Body de exemplo | Sucesso | Erros |
|---|---|---|---|---|---|
| GET | `/linhas` | Lista as linhas ordenadas pelo número | — | 200 `[{"id":2,"numero":3,"nome":"Vermelha","cor":"#EE372F","operadora":"METRO"}, ...]` | — |
| GET | `/linhas/{id}` | Busca uma linha | — | 200 `LinhaResponse` | 404 |
| GET | `/linhas/{id}/estacoes` | Estações da linha em ordem de percurso | — | 200 `[{"ordem":1,"estacaoId":1,"nome":"Palmeiras-Barra Funda","possuiIntegracao":true,"ativa":true}, ...]` | 404 |
| POST | `/linhas` | Cadastra uma linha | `{"numero":15,"nome":"Prata","cor":"#A9A9A9","operadora":"METRO"}` | 201 + `Location` | 400, 422 (número já existe) |
| PUT | `/linhas/{id}` | Atualiza uma linha | igual ao POST | 200 | 400, 404, 422 |
| DELETE | `/linhas/{id}` | Exclui uma linha | — | 204 | 404, 409 (tem estações/históricos/relatos) |

## Estações

| Método | URL | Descrição | Body de exemplo | Sucesso | Erros |
|---|---|---|---|---|---|
| GET | `/estacoes` | Lista as estações por nome | — | 200 `[{"id":8,"nome":"Brás","possuiIntegracao":true,"ativa":true}, ...]` | — |
| GET | `/estacoes/{id}` | Busca uma estação | — | 200 `EstacaoResponse` | 404 |
| GET | `/estacoes/{id}/lotacao-atual?sentido=SENTIDO_BARRA_FUNDA` | Índice LotaMenos e nível agora | — | 200 `{"estacao":"Brás","faixaHorario":"18:00","baseHistorica":50,"relatosConsiderados":1,"pesoRelatos":0.16,"indice":57,"nivel":"MODERADO", ...}` | 400 (sentido inválido/ausente), 404, 422 (fora da Linha 3) |
| POST | `/estacoes` | Cadastra uma estação | `{"nome":"Luz","possuiIntegracao":true,"ativa":true}` | 201 + `Location` | 400, 422 (nome já existe) |
| PUT | `/estacoes/{id}` | Atualiza uma estação | igual ao POST | 200 | 400, 404, 422 |
| DELETE | `/estacoes/{id}` | Exclui uma estação | — | 204 | 404, 409 (tem relatos/históricos/consultas) |

## Usuários

| Método | URL | Descrição | Body de exemplo | Sucesso | Erros |
|---|---|---|---|---|---|
| GET | `/usuarios` | Lista os usuários | — | 200 `[UsuarioResponse]` | — |
| GET | `/usuarios/{id}` | Busca um usuário | — | 200 `{"id":1,"nome":"Carlos Mendes","email":"carlos.mendes@exemplo.com","perfil":"TRABALHADOR","reputacao":1.2,"dataCadastro":"..."}` | 404 |
| POST | `/usuarios` | Cadastra (reputação inicial 1.0) | `{"nome":"Fernanda Teste","email":"fernanda.teste@exemplo.com","perfil":"TRABALHADOR"}` | 201 + `Location` | 400, 422 (e-mail já existe) |
| PUT | `/usuarios/{id}` | Atualiza nome, e-mail e perfil | igual ao POST | 200 | 400, 404, 422 |
| DELETE | `/usuarios/{id}` | Exclui um usuário | — | 204 | 404, 409 (tem relatos/consultas) |

## Relatos

| Método | URL | Descrição | Body de exemplo | Sucesso | Erros |
|---|---|---|---|---|---|
| GET | `/relatos?estacaoId=8&ultimosMinutos=30` | Lista relatos (filtros opcionais) | — | 200 `[RelatoResponse]` | 422 (`ultimosMinutos` ≤ 0) |
| GET | `/relatos/{id}` | Busca um relato | — | 200 `RelatoResponse` | 404 |
| POST | `/relatos` | Relata a lotação (atualiza a reputação) | `{"usuarioId":1,"estacaoId":8,"sentido":"SENTIDO_BARRA_FUNDA","nivel":"LOTADO"}` (`linhaId` e `dataHora` opcionais) | 201 + `Location` `{"id":13,"usuario":"...","reputacaoUsuario":1.05,"estacao":"Brás","nivel":"LOTADO","valorNivel":95, ...}` | 400, 404, 409 (antispam 10 min), 422 (fora do recorte / data no futuro) |
| PUT | `/relatos/{id}` | Corrige nível e sentido (não mexe na reputação) | `{"sentido":"SENTIDO_BARRA_FUNDA","nivel":"CHEIO"}` | 200 | 400, 404 |
| DELETE | `/relatos/{id}` | Exclui um relato | — | 204 | 404 |

## Históricos

| Método | URL | Descrição | Body de exemplo | Sucesso | Erros |
|---|---|---|---|---|---|
| GET | `/historicos?estacaoId=8&sentido=SENTIDO_BARRA_FUNDA&tipoDia=UTIL` | Lista o histórico (filtros opcionais) | — | 200 `[{"estacao":"Brás","faixaHorario":"07:30","indice":97,"nivel":"LOTADO", ...}]` | 400 (enum inválido) |
| GET | `/historicos/{id}` | Busca um registro | — | 200 `HistoricoResponse` | 404 |
| POST | `/historicos` | Cadastra um registro | `{"estacaoId":8,"linhaId":2,"sentido":"SENTIDO_BARRA_FUNDA","tipoDia":"UTIL","faixaHorario":"04:30","indice":15}` | 201 + `Location` | 400 (faixa fora de múltiplos de 15 min / índice fora de 0–100), 404, 422 (duplicado / estação fora da linha) |
| PUT | `/historicos/{id}` | Atualiza um registro | igual ao POST | 200 | 400, 404, 422 |
| DELETE | `/historicos/{id}` | Exclui um registro | — | 204 | 404 |

## Viagens

| Método | URL | Descrição | Body de exemplo | Sucesso | Erros |
|---|---|---|---|---|---|
| POST | `/viagens/planejar` | Calcula a janela ideal de saída e grava a consulta | `{"usuarioId":1,"origemId":17,"destinoId":6,"janelaInicio":"06:00","janelaFim":"08:00","horarioDesejado":"07:00","data":"2026-11-17"}` (`horarioDesejado` e `data` opcionais) | 201 + `Location` `{"consultaId":7,"origem":"Artur Alvim","destino":"Sé","sentido":"SENTIDO_BARRA_FUNDA","horarioDesejado":"07:00","indiceDesejado":96,"nivelDesejado":"LOTADO","horarioSugerido":"06:00","indiceSugerido":50,"nivelSugerido":"MODERADO","reducaoPontos":46,"estacaoCritica":"Tatuapé","alertaAcessibilidade":false,"dica":"Saindo às 06:00 em vez de 07:00 você evita o pico. Trecho mais cheio: Tatuapé.", ...}` | 400, 404, 422 (fora do recorte, origem = destino, janela inválida) |
| GET | `/viagens` | Lista as consultas (mais recentes primeiro) | — | 200 `[ViagemResponse]` | — |
| GET | `/viagens/{id}` | Busca uma consulta | — | 200 `ViagemResponse` | 404 |
| PUT | `/viagens/{id}/aceite` | Registra se aceitou a sugestão | `{"aceita": true}` | 200 `ViagemResponse` com `sugestaoAceita` | 400, 404 |
| DELETE | `/viagens/{id}` | Exclui uma consulta | — | 204 | 404 |

## Impacto

| Método | URL | Descrição | Body | Sucesso | Erros |
|---|---|---|---|---|---|
| GET | `/impacto` | Indicador de impacto da CitizenTech | — | 200 `{"totalConsultas":9,"totalSugestoesAceitas":5,"percentualAceitas":55.6,"reducaoMediaPontos":43.0,"totalRelatos":13,"usuariosAtivos":7}` | — |

## Outros

| Método | URL | Descrição |
|---|---|---|
| GET | `/` | Redireciona (302) para `/swagger-ui.html` |
| GET | `/swagger-ui.html` | Swagger UI |
| GET | `/v3/api-docs` | Especificação OpenAPI (JSON) |
| GET | `/actuator/health` | Health check (`{"status":"UP"}`), usado pelo Render |
| GET | `/actuator/info` | Informações da aplicação |
