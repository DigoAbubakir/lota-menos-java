# LotaMenos API 🚇

> Global Solution FIAP 2026.2 · Domain Driven Design (Java)

## Ficha da CitizenTech

| Campo | Descrição |
|---|---|
| **Nome** | LotaMenos |
| **Equipe** | CPTM — 2ESPX |
| **Cidade e bairro** | São Paulo – eixo da Linha 3-Vermelha do Metrô, da Zona Leste (Corinthians-Itaquera) ao Centro (Sé / República) e à Barra Funda |
| **Cidadão beneficiado** | Trabalhador que usa a Linha 3-Vermelha (e conexões com a Linha 1-Azul e a CPTM) no horário de pico. Personas secundárias: idosa e mãe com carrinho de bebê (perfis com mobilidade reduzida) |
| **Problema em uma frase** | Quem pega metrô e trem todo dia em SP não sabe quando nem onde vai estar lotado, e por isso perde tempo e viaja espremido. |
| **Como funciona** | O usuário informa origem, destino e a janela de horário em que pode sair. A API calcula a lotação prevista de cada estação do trajeto (histórico por faixa de 15 min + relatos em tempo real dos passageiros, ponderados pela reputação de quem relata) e sugere o melhor horário de saída dentro da janela. Os passageiros também relatam a lotação em tempo real. |
| **O que cada parte faz** | **Java** = API REST e regras de negócio (este repositório) · **Database** = modelo relacional Oracle (tabelas `T_LM_*`) · **Data Science** = modelos de previsão de lotação · **Dynamic Programming** = rota menos cheia com grafos · **Network** = rede do ponto físico de atendimento · **AR/VR** = vídeo imersivo da experiência de viagem · **Agile** = backlog e protótipos |
| **Link do repositório** | https://github.com/DigoAbubakir/lota-menos-java |
| **Indicador de impacto** | Redução média de pontos de lotação nas sugestões aceitas e % de sugestões aceitas — calculados ao vivo por `GET /impacto` |
| **ODS conectados** | **ODS 11 – Cidades e comunidades sustentáveis:** distribui a demanda do transporte público ao longo do tempo e torna a viagem mais humana. **ODS 9 – Indústria, inovação e infraestrutura:** usa dados e participação cidadã para extrair mais da infraestrutura de trilhos que já existe. **ODS 10 – Redução das desigualdades:** idosos e pessoas com mobilidade reduzida recebem só sugestões com lotação no máximo moderada, com alerta quando isso não é possível. |

## Equipe CPTM — Integrantes

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

## Tecnologias

Java 21 · Spring Boot 3.3.13 · Maven (wrapper incluso) · Oracle FIAP (H2 em memória no perfil `dev`) · Swagger · JUnit 5 · Docker/Render

## Como rodar

**Windows:** dois cliques no `INICIAR.bat`. O navegador abre no Swagger quando a API estiver pronta.

**IntelliJ:** abra a pasta, use o JDK 21 e rode a configuração **LotaMenos (dev - H2)**. Depois acesse <http://localhost:8080>.

O perfil `dev` usa banco em memória com os dados já carregados, então não precisa de Oracle nem de senha.

**Testes:** `mvnw.cmd test`

## Documentação

[Endpoints](docs/endpoints.md) · [Diagrama de classes](docs/diagrama-classes.md) · [DER](docs/der.md) · [Exemplos de requisições](docs/exemplos-requisicoes.md) · Scripts Oracle em [`database/`](database/)

> Todos os dados são simulados, para fins acadêmicos.
