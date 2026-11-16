# LotaMenos API 🚇

> Global Solution FIAP 2026.2 · Domain Driven Design (Java)

## Ficha da CitizenTech

| Campo | Descrição |
|---|---|
| **Nome** | LotaMenos |
| **Cidade e bairro** | São Paulo – eixo da Linha 3-Vermelha do Metrô, da Zona Leste (Corinthians-Itaquera) ao Centro (Sé / República) e à Barra Funda |
| **Cidadão beneficiado** | Trabalhador que usa a Linha 3-Vermelha (e conexões com a Linha 1-Azul e a CPTM) no horário de pico. Personas secundárias: idosa e mãe com carrinho de bebê (perfis com mobilidade reduzida) |
| **Problema em uma frase** | Quem pega metrô e trem todo dia em SP não sabe quando nem onde vai estar lotado, e por isso perde tempo e viaja espremido. |
| **Como funciona** | O usuário informa origem, destino e a janela de horário em que pode sair. A API calcula a lotação prevista de cada estação do trajeto (histórico por faixa de 15 min + relatos em tempo real dos passageiros, ponderados pela reputação de quem relata) e sugere o melhor horário de saída dentro da janela. Os passageiros também relatam a lotação em tempo real. |
| **O que cada parte faz** | **Java** = API REST e regras de negócio (este repositório) · **Database** = modelo relacional Oracle (tabelas `T_LM_*`) · **Data Science** = modelos de previsão de lotação · **Dynamic Programming** = rota menos cheia com grafos · **Network** = rede do ponto físico de atendimento · **AR/VR** = vídeo imersivo da experiência de viagem · **Agile** = backlog e protótipos |
| **Link do repositório** | https://github.com/DigoAbubakir/lota-menos-java |
| **Indicador de impacto** | Redução média de pontos de lotação nas sugestões aceitas e % de sugestões aceitas — calculados ao vivo por `GET /impacto` |
| **ODS conectados** | **ODS 11 – Cidades e comunidades sustentáveis:** distribui a demanda do transporte público ao longo do tempo e torna a viagem mais humana. **ODS 9 – Indústria, inovação e infraestrutura:** usa dados e participação cidadã para extrair mais da infraestrutura de trilhos que já existe. **ODS 10 – Redução das desigualdades:** idosos e pessoas com mobilidade reduzida recebem só sugestões com lotação no máximo moderada, com alerta quando isso não é possível. |

## Integrantes

| Nome | RM |
|---|---|
| Lucas de Almeida Sales da Silva | RM 564458 |
| Gabriel Carvalho Simões da Silva | RM 563169 |
| Rodrigo Abubakir | RM 561479 |
| João Pedro Palmeira | RM 563542 |
| Andre Ricardo Spinola Castor | RM 563964 |

## Link do deploy

https://lota-menos-java.onrender.com → abre direto no Swagger (`GET /` redireciona para `/swagger-ui.html`).

> ⚠️ O plano gratuito do Render "dorme" após ~15 min sem uso. A primeira requisição depois disso pode levar **cerca de 1 minuto**. Abra o link um pouco antes da apresentação.

---

## Tecnologias e versões

| Tecnologia | Versão |
|---|---|
| Java (JDK) | 21 (Temurin) |
| Spring Boot | 3.3.13 (Web, Data JPA, Validation, Actuator) |
| Maven | 3.9.9 (via Maven Wrapper `mvnw` / `mvnw.cmd`) |
| Banco de dados | Oracle Database da FIAP (`ojdbc11`) · H2 em memória no perfil `dev` |
| Documentação | springdoc-openapi 2.6.0 (Swagger UI) |
| Testes | JUnit 5, Mockito, AssertJ, MockMvc |
| Deploy | Docker (multi-stage) no Render |

Sem Lombok, de propósito: getters, setters e construtores estão escritos à mão, o que evita configurar o plugin no IntelliJ de cada integrante.

## Arquitetura em camadas

```
br.com.fiap.lotamenos
├── LotaMenosApplication   (main: fixa o fuso America/Sao_Paulo)
├── model        entidades JPA + enums  ↔ tabelas Oracle T_LM_*
├── dto          records de request/response (as entidades nunca saem do controller)
├── dao          interfaces Spring Data JPA (LinhaDAO, EstacaoDAO, ...)
├── bo           regras de negócio (LotacaoBO, ViagemBO, RelatoLotacaoBO, ImpactoBO, ...)
├── controller   endpoints REST (GET, POST, PUT, DELETE)
├── exception    exceções de domínio + GlobalExceptionHandler (@RestControllerAdvice)
└── config       OpenAPI, CORS, Clock, redirect "/" → Swagger, DevDataSeeder, GeradorHistoricoSimulado
```

Regras de arquitetura: o **Controller só chama o BO** e o **BO só chama o DAO**. O controller nunca acessa o DAO. Os BOs com escrita usam `@Transactional`. A validação de entrada fica nos DTOs, com Bean Validation e mensagens em português.

Diagramas: [diagrama de classes](docs/diagrama-classes.md) · [DER](docs/der.md) · [endpoints](docs/endpoints.md) · [exemplos reais](docs/exemplos-requisicoes.md) · [roteiro do PDF](docs/roteiro-pdf.md)

## Regras de negócio (resumo)

1. **Índice LotaMenos (0–100)** — `LotacaoBO`
   - **Base:** histórico da faixa de 15 min (horário arredondado para baixo) e do tipo de dia (útil, sábado ou domingo). Sem histórico, a base é 30.
   - **Relatos:** se o horário consultado estiver a no máximo 30 min de agora, entram os relatos dos últimos 30 min, com peso = `reputação × max(0, 1 − minutos/30)`.
   - **Combinação:** `w = min(0.6, 0.15 × Σpesos)` e `índice = round(base × (1 − w) + médiaRelatos × w)`.
   - **Níveis:** VAZIO 0–39 · MODERADO 40–64 · CHEIO 65–84 · LOTADO 85–100.
2. **Lotação da viagem** — origem e destino precisam estar na Linha 3 e ser diferentes. O sentido vem da ordem das estações. O índice da viagem é o **maior** índice do trecho (da origem até a estação anterior ao destino), e essa é a *estação crítica*.
3. **Janela ideal de saída** — `ViagemBO`
   - **Validação da janela:** o fim precisa ser depois do início, a janela tem no máximo 3 h e o horário desejado precisa estar dentro dela.
   - **Escolha:** testa os horários de 15 em 15 min e fica com o de menor índice. Em empate, vence o mais próximo do horário desejado; persistindo o empate, o mais cedo.
   - **Acessibilidade:** IDOSO e MOBILIDADE_REDUZIDA só aceitam horários com nível até MODERADO. Se nenhum horário atender, a API sugere o melhor disponível e marca `alertaAcessibilidade = true`.
   - **Resultado:** a API gera uma dica em português e grava a consulta.
4. **Relatos e reputação** — `RelatoLotacaoBO`
   - **Escopo:** só na Linha 3, e a data não pode estar no futuro.
   - **Antispam:** o mesmo usuário e a mesma estação só podem ter 1 relato a cada 10 min (409).
   - **Reputação:** o relato é comparado ao índice atual calculado *sem ele*. Se a diferença for de até 20, a reputação sobe 0.05 (máximo 1.5); senão, cai 0.10 (mínimo 0.3).
5. **Impacto** — `ImpactoBO`: total de consultas, sugestões aceitas, % aceitas, redução média de pontos nas aceitas, total de relatos e usuários ativos (com relato ou consulta nos últimos 30 dias).

## Endpoints

| Recurso | Endpoints |
|---|---|
| `/linhas` | `GET` · `GET /{id}` · `POST` · `PUT /{id}` · `DELETE /{id}` · `GET /{id}/estacoes` |
| `/estacoes` | `GET` · `GET /{id}` · `POST` · `PUT /{id}` · `DELETE /{id}` · `GET /{id}/lotacao-atual?sentido=` |
| `/usuarios` | `GET` · `GET /{id}` · `POST` · `PUT /{id}` · `DELETE /{id}` |
| `/relatos` | `GET ?estacaoId=&ultimosMinutos=` · `GET /{id}` · `POST` · `PUT /{id}` · `DELETE /{id}` |
| `/historicos` | `GET ?estacaoId=&sentido=&tipoDia=` · `GET /{id}` · `POST` · `PUT /{id}` · `DELETE /{id}` |
| `/viagens` | `POST /planejar` · `GET` · `GET /{id}` · `PUT /{id}/aceite` · `DELETE /{id}` |
| `/impacto` | `GET` |

Status: `201` + header `Location` no POST · `200` no GET/PUT · `204` no DELETE · erros `400`, `404`, `409`, `422`, `500` sempre no formato `ErroResponse`. Detalhes em [docs/endpoints.md](docs/endpoints.md).

---

## Como rodar

### Pré-requisitos
- JDK 21. No IntelliJ: *File → Project Structure → SDK → Add SDK → Download JDK → 21 (Eclipse Temurin)*.
- Não precisa instalar o Maven: use o wrapper `mvnw.cmd` (Windows) ou `./mvnw` (Linux/Mac).

### 1) Perfil `dev` — H2 em memória (não precisa do Oracle)

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev      # Windows
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev        # Linux/Mac
```

Abra <http://localhost:8080> (redireciona para o Swagger). O `DevDataSeeder` carrega os mesmos dados dos scripts Oracle. Console do H2: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:lotamenos`, usuário `sa`, senha vazia).

No IntelliJ: *Run → Edit Configurations → LotaMenosApplication → Active profiles: `dev`*.

### 2) Banco Oracle da FIAP — criar as tabelas e os dados

1. Abra o **Oracle SQL Developer** e conecte-se ao Oracle da FIAP: host `oracle.fiap.com.br`, porta `1521`, SID `ORCL`, usuário `RMxxxxx`.
2. *Ferramentas → Preferências → Ambiente → Codificação* = **UTF-8**, para os acentos de Sé, Tatuapé etc. gravarem certo.
3. Abra `database/01_ddl.sql` e execute **como script (F5)**.
4. Abra `database/02_dml.sql` e execute **como script (F5)**. O script termina com `COMMIT`.
5. Para conferir, execute `database/03_consultas.sql`. Esperado: 6 linhas, 18 estações, 26 vínculos linha-estação, 8.208 históricos, 6 usuários, 12 relatos e 6 consultas.

### 3) Perfil padrão — API apontando para o Oracle da FIAP

As credenciais **nunca** ficam em arquivo: elas vão em variáveis de ambiente.

| Variável | Exemplo |
|---|---|
| `DB_URL` | `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL` (este já é o padrão) |
| `DB_USER` | `RM12345` |
| `DB_PASSWORD` | sua senha do Oracle FIAP |

- **No IntelliJ:** *Run → Edit Configurations → LotaMenosApplication → Environment variables* → `DB_USER=RM12345;DB_PASSWORD=suaSenha`. Deixe *Active profiles* vazio.
- **No terminal (PowerShell):**
  ```powershell
  $env:DB_USER="RM12345"; $env:DB_PASSWORD="suaSenha"; .\mvnw.cmd spring-boot:run
  ```

### 4) Testes

```bash
mvnw.cmd test
```

São 39 testes: unitários de `LotacaoBO`, `ViagemBO`, `RelatoLotacaoBO` e do gerador de dados, mais um teste de integração `@SpringBootTest` (perfil dev) que chama `/viagens/planejar` via MockMvc.

---

## Deploy no Render

1. Suba o repositório no GitHub (veja abaixo).
2. Em <https://render.com>: **New → Web Service → conecte sua conta do GitHub → escolha o repositório**.
3. Em **Language/Runtime** escolha **Docker**. O Render detecta o `Dockerfile`. Plano: **Free**. Região: Virginia ou Ohio.
4. Em **Environment Variables** adicione `DB_URL`, `DB_USER` e `DB_PASSWORD` (Oracle FIAP).
5. Em **Advanced → Health Check Path** coloque `/actuator/health`.
6. Clique em **Deploy Web Service**. O primeiro build leva de 5 a 10 minutos.
7. Teste `https://<seu-servico>.onrender.com/` → deve abrir o Swagger.

O `render.yaml` também permite criar o serviço via *New → Blueprint*.

> 🔁 **Plano B:** se o Render não conseguir acessar o Oracle da FIAP (firewall, instabilidade), adicione a variável `SPRING_PROFILES_ACTIVE=dev`. A API sobe com H2 e os mesmos dados simulados, e o Swagger continua 100% funcional para a avaliação.

> 💤 O plano gratuito dorme após inatividade, e a primeira requisição pode levar ~1 minuto.

## Subir no GitHub

```bash
git remote add origin https://github.com/DigoAbubakir/lota-menos-java.git
git push -u origin main
```

---

## Observações

- **Todos os dados são simulados**, criados para fins acadêmicos e inspirados no padrão real de pico da Linha 3-Vermelha. Nenhum dado pessoal real é usado; os e-mails são `@exemplo.com`.
- O recorte é a Linha 3-Vermelha. As conexões (Sé ↔ Linha 1; Palmeiras-Barra Funda ↔ Linha 7; Brás ↔ Linhas 10, 11 e 12; Tatuapé ↔ Linhas 11 e 12; Corinthians-Itaquera ↔ Linha 11) aparecem como pontos de integração.
