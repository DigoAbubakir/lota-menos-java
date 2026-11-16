# Roteiro do PDF da entrega — LotaMenos (DDD · Java)

> Esqueleto na **ordem exata da rubrica**. Substitua os campos `<...>`, cole as imagens indicadas e exporte em PDF.

---

## 1. Capa, índice, descritivo, justificativa e objetivos

### 1.1 Capa
- **Projeto:** LotaMenos — previsão de lotação do metrô para quem viaja no pico
- **Global Solution FIAP 2026.2 — Domain Driven Design (Java)**
- **Integrantes:**
  - Lucas de Almeida Sales da Silva – RM 564458
  - Gabriel Carvalho Simões da Silva – RM 563169
  - Rodrigo Abubakir – RM 561479
  - João Pedro Palmeira – RM 563542
  - Andre Ricardo Spinola Castor – RM 563964
- **Turma:** 2ESPX · **Data:** 16/11/2026

### 1.2 Índice
1. Descritivo da solução · Justificativa · Objetivos
2. Diagrama de Classes
3. Protótipos de tela · Tabela de endpoints · DER
4. Instruções de execução

### 1.3 Descritivo da solução
O **LotaMenos** é uma CitizenTech para quem usa metrô e trem em São Paulo no horário de pico. O usuário informa a estação de origem, a estação de destino e a janela de horário em que pode sair de casa. A API calcula a lotação prevista em cada estação do trajeto e sugere o melhor horário de saída dentro dessa janela.

A previsão usa o **Índice LotaMenos** (0 a 100), que combina dois elementos:

- o **histórico de lotação** por estação, sentido, tipo de dia e faixa de 15 minutos;
- os **relatos em tempo real** dos próprios passageiros.

Cada relato pesa conforme a **reputação** de quem relata (que sobe quando o relato é coerente com a realidade) e perde força com o tempo: depois de 30 minutos, deixa de contar. A viagem é avaliada pelo **ponto mais crítico** do trajeto, ou seja, a estação mais cheia.

Pessoas idosas e com mobilidade reduzida recebem apenas sugestões com lotação no máximo moderada; quando isso não é possível, a API devolve um alerta. Um **indicador de impacto** mede quantas sugestões foram aceitas e quantos pontos de lotação o cidadão deixou de enfrentar.

O recorte é a **Linha 3-Vermelha** inteira (18 estações, de Palmeiras-Barra Funda a Corinthians-Itaquera), com as conexões para a Linha 1-Azul e a CPTM. Todos os dados são simulados para fins acadêmicos.

A solução é uma **API REST em Java 21 / Spring Boot 3.3**, organizada nas camadas model, DAO, BO, controller e exception. Ela usa o banco **Oracle da FIAP**, está documentada com **Swagger** e foi publicada no **Render** via Docker.

### 1.4 Justificativa
A Linha 3-Vermelha é uma das mais carregadas do sistema metroferroviário de São Paulo e liga a Zona Leste, de grande densidade residencial, ao Centro, onde se concentram os empregos. No pico da manhã, o passageiro não tem como saber que sair 30 ou 60 minutos antes ou depois pode significar uma viagem muito menos cheia. O resultado é tempo perdido em plataformas lotadas, desconforto, risco à saúde e exclusão de quem tem mais dificuldade de se locomover, como idosos e pais com carrinho de bebê.

Ao dar previsibilidade e incentivar pequenas mudanças de horário, o LotaMenos ajuda a distribuir a demanda ao longo do tempo e extrai mais da infraestrutura que já existe. Isso conecta o projeto aos **ODS 9** (inovação e infraestrutura), **10** (redução das desigualdades) e **11** (cidades sustentáveis).

### 1.5 Objetivos
**Objetivo geral:** reduzir a lotação enfrentada por quem usa a Linha 3-Vermelha no pico, sugerindo o melhor horário de saída com base em histórico e relatos em tempo real.

**Objetivos específicos:**
- Calcular um índice de lotação (0–100) por estação, sentido e faixa de 15 minutos.
- Incorporar relatos dos passageiros com peso proporcional à reputação e decaimento no tempo.
- Sugerir, dentro da janela do usuário, o horário com menor lotação no ponto mais crítico do trajeto.
- Garantir sugestões acessíveis (no máximo lotação moderada) para idosos e pessoas com mobilidade reduzida.
- Medir o impacto: % de sugestões aceitas e redução média de pontos de lotação.
- Disponibilizar a API documentada (Swagger) e publicada na nuvem (Render).

---

## 2. Diagrama de Classes

Cole aqui a imagem do diagrama gerado a partir de [`docs/diagrama-classes.md`](diagrama-classes.md). Para gerar a imagem, cole o bloco Mermaid em <https://mermaid.live> e exporte em PNG ou SVG.

Comente brevemente as camadas:
- **model:** entidades JPA mapeadas para as tabelas `T_LM_*`, mais os enums.
- **dao:** interfaces Spring Data JPA.
- **bo:** regras de negócio (Índice LotaMenos, janela ideal, reputação, impacto).
- **controller:** endpoints REST.
- **exception:** exceções de domínio e o `GlobalExceptionHandler`.

---

## 3. Protótipos de tela, tabela de endpoints e DER

### 3.1 Protótipos de tela
*(Desenhe no Figma/Canva/papel e cole as imagens. Abaixo estão os campos e o endpoint de cada tela.)*

**Tela 1 — Planejar viagem**
- Campos: Estação de origem (lista das 18 estações da Linha 3, vinda de `GET /linhas/{id}/estacoes`) · Estação de destino (mesma lista) · Data (padrão hoje) · Posso sair entre `[HH:mm]` e `[HH:mm]` (máx. 3 h) · Gostaria de sair às `[HH:mm]` (opcional) · Botão **"Encontrar melhor horário"**.
- Chama: `POST /viagens/planejar` com `usuarioId` (usuário logado), `origemId`, `destinoId`, `janelaInicio`, `janelaFim`, `horarioDesejado`, `data`.

**Tela 2 — Resultado da previsão**
- Mostra:
  - um cartão "Seu horário" (07:00 · índice 96 · LOTADO, em vermelho) e um cartão "Sugestão" (06:00 · índice 50 · MODERADO, em amarelo);
  - um selo "−46 pontos de lotação";
  - a estação mais cheia do trecho (ex.: "Tatuapé");
  - a dica em texto ("Saindo às 06:00 em vez de 07:00 você evita o pico...");
  - um aviso de acessibilidade em destaque quando `alertaAcessibilidade = true`.
- Botões **"Vou nesse horário"** e **"Prefiro meu horário"**.
- Chama: os dados vêm da resposta de `POST /viagens/planejar`. Os botões chamam `PUT /viagens/{id}/aceite` com `{"aceita": true}` ou `{"aceita": false}`.

**Tela 3 — Relatar lotação**
- Campos: Estação onde estou (lista) · Sentido (Barra Funda / Itaquera) · Como está? quatro botões grandes com ícones: **Vazio · Moderado · Cheio · Lotado** · Botão **"Enviar relato"**.
- Mostra também a "Lotação agora" da estação escolhida (índice e nível) e a reputação do usuário depois do envio.
- Chama: `GET /estacoes/{id}/lotacao-atual?sentido=...` para exibir a lotação atual e `POST /relatos` para enviar. Em caso de 409, mostrar "Você já relatou essa estação há menos de 10 minutos".

### 3.2 Tabela de endpoints
Copie a tabela de [`docs/endpoints.md`](endpoints.md) (método, URL, descrição, body de exemplo, resposta de sucesso, erros). Se quiser, inclua 2 ou 3 exemplos reais de [`docs/exemplos-requisicoes.md`](exemplos-requisicoes.md) ou prints do Swagger.

### 3.3 DER
Cole a imagem do DER gerado a partir de [`docs/der.md`](der.md) (Mermaid `erDiagram`) ou exportado do Oracle SQL Developer Data Modeler depois de rodar `database/01_ddl.sql`.

---

## 4. Instruções de execução

### 4.1 Ferramentas e versões
| Ferramenta | Versão | Link |
|---|---|---|
| IntelliJ IDEA | 2024+ (Community ou Ultimate) | <https://www.jetbrains.com/idea/> |
| JDK | 21 (Eclipse Temurin) | <https://adoptium.net/> |
| Maven | 3.9.9 (já incluso via Maven Wrapper) | <https://maven.apache.org/> |
| Spring Boot | 3.3.13 | <https://spring.io/projects/spring-boot> |
| Oracle Database (FIAP) | `oracle.fiap.com.br:1521:ORCL` | — |
| Oracle SQL Developer | 23+ | <https://www.oracle.com/database/sqldeveloper/> |
| Git / GitHub | 2.x | <https://github.com/> |
| Render | plano Free (Docker) | <https://render.com/> |
| Swagger (springdoc-openapi) | 2.6.0 | `/swagger-ui.html` |

### 4.2 Passo a passo
1. **Clonar o repositório:** `git clone <LINK_GITHUB>` e abrir a pasta no IntelliJ (*File → Open*).
2. **JDK 21:** *File → Project Structure → SDK → 21*.
3. **Rodar sem banco (perfil dev):** *Run → Edit Configurations → LotaMenosApplication → Active profiles = `dev`* → Run. Acesse <http://localhost:8080>.
4. **Criar o banco Oracle:** no SQL Developer, conectado ao Oracle FIAP, execute como script (F5) `database/01_ddl.sql` e, em seguida, `database/02_dml.sql`.
5. **Rodar com Oracle:** em *Environment variables*, defina `DB_USER=RMxxxxx;DB_PASSWORD=suaSenha` e deixe o perfil vazio → Run.
6. **Testes:** `mvnw.cmd test`.
7. **Deploy:** link `<LINK_DEPLOY>` (Render, Docker). O primeiro acesso após inatividade pode levar ~1 minuto.
