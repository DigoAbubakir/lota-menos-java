# Exemplos reais de requisições — LotaMenos API

Respostas **reais** obtidas na validação local em 16/11/2026 18:14 (horário de São Paulo), com o perfil `dev` (H2 em memória, dados simulados do `DevDataSeeder`).

- Base URL local: `http://localhost:8080` (no Render, troque pelo link do deploy).
- Os IDs dependem da carga: no perfil dev, Artur Alvim = 17, Sé = 6, Brás = 8; usuário 1 = Carlos Mendes (TRABALHADOR), 2 = Dona Maria Aparecida (IDOSO).
- Os valores de `lotacao-atual` mudam com o horário da chamada e com os relatos dos últimos 30 minutos.
- Swagger: `GET /` → 302 para `/swagger-ui.html` (validado: abre normalmente).

### 1. Listar linhas

```http
GET /linhas
```

**Resposta:** `HTTP/1.1 200 `

```json
[
  {
    "id": 1,
    "numero": 1,
    "nome": "Azul",
    "cor": "#0455A1",
    "operadora": "METRO"
  },
  {
    "id": 2,
    "numero": 3,
    "nome": "Vermelha",
    "cor": "#EE372F",
    "operadora": "METRO"
  },
  {
    "id": 3,
    "numero": 7,
    "nome": "Rubi",
    "cor": "#CA016B",
    "operadora": "CPTM"
  },
  {
    "id": 4,
    "numero": 10,
    "nome": "Turquesa",
    "cor": "#008B8B",
    "operadora": "CPTM"
  },
  {
    "id": 5,
    "numero": 11,
    "nome": "Coral",
    "cor": "#F04E23",
    "operadora": "CPTM"
  },
  {
    "id": 6,
    "numero": 12,
    "nome": "Safira",
    "cor": "#083E89",
    "operadora": "CPTM"
  }
]
```

### 2. Estações da Linha 3 em ordem

```http
GET /linhas/2/estacoes
```

**Resposta:** `HTTP/1.1 200 `

```json
[
  {
    "ordem": 1,
    "estacaoId": 1,
    "nome": "Palmeiras-Barra Funda",
    "possuiIntegracao": true,
    "ativa": true
  },
  {
    "ordem": 2,
    "estacaoId": 2,
    "nome": "Marechal Deodoro",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 3,
    "estacaoId": 3,
    "nome": "Santa Cecília",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 4,
    "estacaoId": 4,
    "nome": "República",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 5,
    "estacaoId": 5,
    "nome": "Anhangabaú",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 6,
    "estacaoId": 6,
    "nome": "Sé",
    "possuiIntegracao": true,
    "ativa": true
  },
  {
    "ordem": 7,
    "estacaoId": 7,
    "nome": "Pedro II",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 8,
    "estacaoId": 8,
    "nome": "Brás",
    "possuiIntegracao": true,
    "ativa": true
  },
  {
    "ordem": 9,
    "estacaoId": 9,
    "nome": "Bresser-Mooca",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 10,
    "estacaoId": 10,
    "nome": "Belém",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 11,
    "estacaoId": 11,
    "nome": "Tatuapé",
    "possuiIntegracao": true,
    "ativa": true
  },
  {
    "ordem": 12,
    "estacaoId": 12,
    "nome": "Carrão",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 13,
    "estacaoId": 13,
    "nome": "Penha",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 14,
    "estacaoId": 14,
    "nome": "Vila Matilde",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 15,
    "estacaoId": 15,
    "nome": "Guilhermina-Esperança",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 16,
    "estacaoId": 16,
    "nome": "Patriarca",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 17,
    "estacaoId": 17,
    "nome": "Artur Alvim",
    "possuiIntegracao": false,
    "ativa": true
  },
  {
    "ordem": 18,
    "estacaoId": 18,
    "nome": "Corinthians-Itaquera",
    "possuiIntegracao": true,
    "ativa": true
  }
]
```

### 3. Cadastrar usuário (TRABALHADOR)

```http
POST /usuarios
Content-Type: application/json

{"nome":"Fernanda Teste","email":"fernanda.teste@exemplo.com","perfil":"TRABALHADOR"}
```

**Resposta:** `HTTP/1.1 201 ` — `Location: http://localhost:8080/usuarios/7`

```json
{
  "id": 7,
  "nome": "Fernanda Teste",
  "email": "fernanda.teste@exemplo.com",
  "perfil": "TRABALHADOR",
  "reputacao": 1,
  "dataCadastro": "2026-11-16T18:13:36"
}
```

### 4. Lotação atual do Brás (antes do relato)

```http
GET /estacoes/8/lotacao-atual?sentido=SENTIDO_BARRA_FUNDA
```

**Resposta:** `HTTP/1.1 200 `

```json
{
  "estacaoId": 8,
  "estacao": "Brás",
  "sentido": "SENTIDO_BARRA_FUNDA",
  "dataHora": "2026-11-16T18:13:36",
  "faixaHorario": "18:00",
  "baseHistorica": 50,
  "relatosConsiderados": 1,
  "pesoRelatos": 0.16,
  "indice": 57,
  "nivel": "MODERADO"
}
```

### 5. Relatar lotação (Brás, LOTADO)

```http
POST /relatos
Content-Type: application/json

{"usuarioId":7,"estacaoId":8,"sentido":"SENTIDO_BARRA_FUNDA","nivel":"LOTADO"}
```

**Resposta:** `HTTP/1.1 201 ` — `Location: http://localhost:8080/relatos/13`

```json
{
  "id": 13,
  "usuarioId": 7,
  "usuario": "Fernanda Teste",
  "reputacaoUsuario": 0.9,
  "estacaoId": 8,
  "estacao": "Brás",
  "linhaId": 2,
  "numeroLinha": 3,
  "sentido": "SENTIDO_BARRA_FUNDA",
  "nivel": "LOTADO",
  "valorNivel": 95,
  "dataHora": "2026-11-16T18:13:36"
}
```

### 6. Repetir o relato em menos de 10 min → 409 (antispam)

```http
POST /relatos
Content-Type: application/json

{"usuarioId":7,"estacaoId":8,"sentido":"SENTIDO_BARRA_FUNDA","nivel":"LOTADO"}
```

**Resposta:** `HTTP/1.1 409 `

```json
{
  "timestamp": "2026-11-16T18:13:37",
  "status": 409,
  "erro": "Relato duplicado",
  "mensagem": "Você já relatou a lotação da estação Brás há menos de 10 minutos. Aguarde para relatar novamente.",
  "caminho": "/relatos",
  "campos": []
}
```

### 7. Planejar viagem Artur Alvim → Sé (TRABALHADOR, janela 06:00–08:00, desejado 07:00)

```http
POST /viagens/planejar
Content-Type: application/json

{"usuarioId":1,"origemId":17,"destinoId":6,"janelaInicio":"06:00","janelaFim":"08:00","horarioDesejado":"07:00","data":"2026-11-17"}
```

**Resposta:** `HTTP/1.1 201 ` — `Location: http://localhost:8080/viagens/7`

```json
{
  "consultaId": 7,
  "origem": "Artur Alvim",
  "destino": "Sé",
  "sentido": "SENTIDO_BARRA_FUNDA",
  "horarioDesejado": "07:00",
  "indiceDesejado": 96,
  "nivelDesejado": "LOTADO",
  "horarioSugerido": "06:00",
  "indiceSugerido": 50,
  "nivelSugerido": "MODERADO",
  "reducaoPontos": 46,
  "estacaoCritica": "Tatuapé",
  "alertaAcessibilidade": false,
  "dica": "Saindo às 06:00 em vez de 07:00 você evita o pico. Trecho mais cheio: Tatuapé.",
  "usuarioId": 1,
  "data": "2026-11-17",
  "sugestaoAceita": null
}
```

### 8. Planejar a mesma viagem com perfil IDOSO (Dona Maria)

```http
POST /viagens/planejar
Content-Type: application/json

{"usuarioId":2,"origemId":17,"destinoId":6,"janelaInicio":"06:00","janelaFim":"08:00","horarioDesejado":"07:00","data":"2026-11-17"}
```

**Resposta:** `HTTP/1.1 201 ` — `Location: http://localhost:8080/viagens/8`

```json
{
  "consultaId": 8,
  "origem": "Artur Alvim",
  "destino": "Sé",
  "sentido": "SENTIDO_BARRA_FUNDA",
  "horarioDesejado": "07:00",
  "indiceDesejado": 96,
  "nivelDesejado": "LOTADO",
  "horarioSugerido": "06:00",
  "indiceSugerido": 50,
  "nivelSugerido": "MODERADO",
  "reducaoPontos": 46,
  "estacaoCritica": "Tatuapé",
  "alertaAcessibilidade": false,
  "dica": "Saindo às 06:00 em vez de 07:00 você evita o pico. Trecho mais cheio: Tatuapé.",
  "usuarioId": 2,
  "data": "2026-11-17",
  "sugestaoAceita": null
}
```

### 9. IDOSO com janela só no pico (07:00–08:00) → alerta de acessibilidade

```http
POST /viagens/planejar
Content-Type: application/json

{"usuarioId":2,"origemId":17,"destinoId":6,"janelaInicio":"07:00","janelaFim":"08:00","horarioDesejado":"07:30","data":"2026-11-17"}
```

**Resposta:** `HTTP/1.1 201 ` — `Location: http://localhost:8080/viagens/9`

```json
{
  "consultaId": 9,
  "origem": "Artur Alvim",
  "destino": "Sé",
  "sentido": "SENTIDO_BARRA_FUNDA",
  "horarioDesejado": "07:30",
  "indiceDesejado": 97,
  "nivelDesejado": "LOTADO",
  "horarioSugerido": "08:00",
  "indiceSugerido": 93,
  "nivelSugerido": "LOTADO",
  "reducaoPontos": 4,
  "estacaoCritica": "Belém",
  "alertaAcessibilidade": true,
  "dica": "Saindo às 08:00 em vez de 07:30 você evita o pico. Trecho mais cheio: Belém. Atenção: nenhum horário da janela ficou com lotação moderada ou menor; se puder, amplie a janela ou prefira o vagão próximo ao assento preferencial.",
  "usuarioId": 2,
  "data": "2026-11-17",
  "sugestaoAceita": null
}
```

### 10. Aceitar a sugestão

```http
PUT /viagens/7/aceite
Content-Type: application/json

{"aceita":true}
```

**Resposta:** `HTTP/1.1 200 `

```json
{
  "consultaId": 7,
  "origem": "Artur Alvim",
  "destino": "Sé",
  "sentido": "SENTIDO_BARRA_FUNDA",
  "horarioDesejado": "07:00",
  "indiceDesejado": 96,
  "nivelDesejado": "LOTADO",
  "horarioSugerido": "06:00",
  "indiceSugerido": 50,
  "nivelSugerido": "MODERADO",
  "reducaoPontos": 46,
  "estacaoCritica": "Tatuapé",
  "alertaAcessibilidade": false,
  "dica": "Saindo às 06:00 em vez de 07:00 você evita o pico. Trecho mais cheio: Tatuapé.",
  "usuarioId": 1,
  "data": "2026-11-17",
  "sugestaoAceita": true
}
```

### 11. Indicador de impacto

```http
GET /impacto
```

**Resposta:** `HTTP/1.1 200 `

```json
{
  "totalConsultas": 9,
  "totalSugestoesAceitas": 5,
  "percentualAceitas": 55.6,
  "reducaoMediaPontos": 43,
  "totalRelatos": 13,
  "usuariosAtivos": 7
}
```

### 12. Erro 404 — estação inexistente

```http
GET /estacoes/9999
```

**Resposta:** `HTTP/1.1 404 `

```json
{
  "timestamp": "2026-11-16T18:13:38",
  "status": 404,
  "erro": "Recurso não encontrado",
  "mensagem": "Estação com id 9999 não encontrada.",
  "caminho": "/estacoes/9999",
  "campos": []
}
```

### 13. Erro 422 — janela inválida (fim antes do início)

```http
POST /viagens/planejar
Content-Type: application/json

{"usuarioId":1,"origemId":17,"destinoId":6,"janelaInicio":"08:00","janelaFim":"07:00"}
```

**Resposta:** `HTTP/1.1 422 `

```json
{
  "timestamp": "2026-11-16T18:13:39",
  "status": 422,
  "erro": "Regra de negócio",
  "mensagem": "O fim da janela (07:00) deve ser depois do início (08:00).",
  "caminho": "/viagens/planejar",
  "campos": []
}
```

### 14. Erro 422 — estação fora do recorte (Luz cadastrada sem vínculo com a Linha 3)

```http
POST /estacoes
Content-Type: application/json

{"nome":"Luz","possuiIntegracao":true,"ativa":true}
```

**Resposta:** `HTTP/1.1 201 ` — `Location: http://localhost:8080/estacoes/19`

```json
{
  "id": 19,
  "nome": "Luz",
  "possuiIntegracao": true,
  "ativa": true
}
```

### 14b. Planejar a partir da Luz

```http
POST /viagens/planejar
Content-Type: application/json

{"usuarioId":1,"origemId":19,"destinoId":6,"janelaInicio":"07:00","janelaFim":"08:00"}
```

**Resposta:** `HTTP/1.1 422 `

```json
{
  "timestamp": "2026-11-16T18:13:39",
  "status": 422,
  "erro": "Regra de negócio",
  "mensagem": "A estação Luz não faz parte do recorte da Linha 3.",
  "caminho": "/viagens/planejar",
  "campos": []
}
```

### 15. Erro 400 — validação de campos

```http
POST /usuarios
Content-Type: application/json

{"nome":"","email":"email-invalido","perfil":"TRABALHADOR"}
```

**Resposta:** `HTTP/1.1 400 `

```json
{
  "timestamp": "2026-11-16T18:13:39",
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "Um ou mais campos estão inválidos. Verifique a lista 'campos'.",
  "caminho": "/usuarios",
  "campos": [
    {
      "campo": "email",
      "mensagem": "Informe um e-mail válido."
    },
    {
      "campo": "nome",
      "mensagem": "O nome deve ter entre 2 e 100 caracteres."
    },
    {
      "campo": "nome",
      "mensagem": "Informe o nome."
    }
  ]
}
```

### 16. Erro 409 — excluir estação com relatos

```http
DELETE /estacoes/8
```

**Resposta:** `HTTP/1.1 409 `

```json
{
  "timestamp": "2026-11-16T18:13:40",
  "status": 409,
  "erro": "Conflito de dados",
  "mensagem": "Operação não permitida: o registro está vinculado a outros dados (ex.: estação com relatos) ou viola uma restrição de unicidade.",
  "caminho": "/estacoes/8",
  "campos": []
}
```

### 17. Excluir a estação Luz (sem vínculos) → 204

```http
DELETE /estacoes/19
```

**Resposta:** `HTTP/1.1 204 `

