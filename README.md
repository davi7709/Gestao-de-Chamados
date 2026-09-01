# Gestão de Chamados — API

API REST para gestão de chamados, desenvolvida como desafio técnico. Permite cadastrar, listar, filtrar e atualizar chamados, além de identificar automaticamente os que estão há muito tempo sem atendimento.

Front-end React que consome esta API: [chamados-frontend](https://github.com/davi7709/Chamados-Frontend) .

## Tecnologias utilizadas

- **Java 17**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **H2 Database** (arquivo local, não em memória)
- **Maven**
- **JUnit 5 + Mockito** para testes unitários

## Pré-requisitos

- JDK 17 ou superior
- Maven (ou use o `./mvnw` incluso, se aplicável)

## Como rodar

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O H2 console fica disponível em `http://localhost:8080/h2-console`:

- JDBC URL: `jdbc:h2:file:./data/chamados;AUTO_SERVER=TRUE`
- Usuário: `sa`
- Senha: (em branco)

## Como rodar os testes

```bash
mvn test
```

Cobertura da camada de service (com Mockito, sem depender de banco real): criação, busca por id (encontrado e não encontrado), listagem, filtro por status, edição, alteração de status, e a regra de atrasados — incluindo o caso de duas prioridades diferentes com o mesmo tempo parado, para provar que o limite realmente varia por prioridade.

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/chamados` | Cadastra um chamado novo (status inicial sempre `NOVO`) |
| GET | `/api/chamados` | Lista todos os chamados |
| GET | `/api/chamados?status=NOVO` | Filtra chamados por status |
| GET | `/api/chamados/{id}` | Busca um chamado específico |
| PUT | `/api/chamados/{id}` | Edita dados gerais (título, descrição, solicitante, prioridade) |
| PATCH | `/api/chamados/{id}/status` | Altera apenas o status do chamado |
| GET | `/api/chamados/atrasados` | Lista chamados sem atendimento há mais tempo do que o aceitável para sua prioridade |

### Exemplo de corpo de requisição (criar/editar)

```json
{
  "titulo": "Impressora não liga",
  "descricao": "Testando o cadastro",
  "solicitante": "Davi",
  "prioridade": "ALTA"
}
```

### Exemplo de corpo de requisição (alterar status)

```json
{
  "status": "EM_ANDAMENTO"
}
```

## Decisões técnicas

**Banco de dados: H2 em arquivo, não em memória.**
Evita exigir instalação de um SGBD externo, mas em modo arquivo (`jdbc:h2:file:...`) em vez de memória — os dados persistem entre reinicializações da aplicação, atendendo ao requisito de "salvar em banco de dados" de forma realista.

**Separação entre entidade JPA e DTOs da API.**
A entidade `Chamado` nunca é exposta diretamente pelos endpoints. `ChamadoRequest` (record) para entrada e `ChamadoResponse` (record) para saída isolam o contrato da API de detalhes internos de persistência.

**Editar dados gerais e alterar status são operações separadas.**
`PUT /{id}` edita título, descrição, solicitante e prioridade. `PATCH /{id}/status` só muda o status. São ações de negócio diferentes de propósito ("corrigir informação" vs. "avançar o fluxo do chamado"), o que facilita adicionar regras específicas de transição de status no futuro sem afetar a edição geral.

**`dataUltimaAtualizacao` controlada manualmente, sem `@PreUpdate` automático.**
Atualizada explicitamente dentro de cada método do service que representa uma mudança relevante (alterar status, editar dados), em vez de um `@PreUpdate` genérico que dispararia em qualquer UPDATE — dá controle mais preciso sobre o que conta como "o chamado foi mexido" para efeito da regra de atrasados.

**Chamados novos sempre nascem com status `NOVO`.**
O `ChamadoRequest` não tem campo `status` — o cliente da API não escolhe o status inicial. A regra é resolvida dentro da própria entidade (construtor / `@PrePersist`), centralizada em um único lugar.

**Tratamento de erro centralizado com `@RestControllerAdvice`.**
Erros de validação (`@Valid`) e de recurso não encontrado (`ChamadoNaoEncontradoException`) são capturados globalmente e convertidos em respostas HTTP padronizadas (`400`/`404`), em vez de deixar o Spring devolver `500` genérico com stack trace exposto.

### O extra: identificando chamados atrasados

Um chamado é considerado atrasado quando o tempo desde sua última atualização (`dataUltimaAtualizacao`) ultrapassa um limite que varia por prioridade. A regra vive diretamente no enum `Prioridade`, associando cada valor ao seu limite em horas:

| Prioridade | Limite sem atualização |
|---|---|
| Crítica | 4 horas |
| Alta | 8 horas |
| Média | 24 horas |
| Baixa | 48 horas |

**Por que por prioridade, e não um limite único para todos?** Um chamado crítico parado por poucas horas já é grave; um de baixa prioridade parado pelo mesmo tempo é normal. Um limite único trataria os dois casos como equivalentes.

**Por que baseado em `dataUltimaAtualizacao`, e não em `dataAbertura`?** Um chamado pode ter sido aberto há uma semana mas atualizado há 1 hora — o que importa para a regra de atraso é há quanto tempo ninguém mexe nele, não há quanto tempo ele existe.

**Por que a regra vive no enum, e não em `application.properties`/`@Value`?** É uma modelagem mais orientada a objetos — o limite de SLA é um dado que pertence à prioridade em si, não uma configuração de infraestrutura. Também evita depender de anotações do Spring nesse ponto específico.

Chamados `RESOLVIDO` e `ENCERRADO` nunca entram nessa checagem — não faz sentido marcar como atrasado algo que já foi finalizado.

## Possíveis evoluções (fora do escopo deste desafio)

- Validação de transições de status (ex.: impedir voltar de `ENCERRADO` para `NOVO`).
- Paginação na listagem de chamados.
- Autenticação/autorização para os endpoints.
- Notificação automática (e-mail/webhook) quando um chamado entra na lista de atrasados.
- Deploy público (mantido apenas local neste desafio, dado o prazo).
