# Contacts API — Aula 4: APIs e Microsserviços

API REST desenvolvida como exercício da disciplina de APIs e Microsserviços (ADS - IFSP). Permite gerenciar contatos e endereços com persistência em MySQL, paginação, documentação via Swagger e testes unitários.

---

## Tecnologias

- Java 25
- Spring Boot 4.0.4
- Spring Data JPA / Hibernate
- MySQL
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito

---

## Pré-requisitos

- JDK 25
- MySQL rodando em `localhost:3306`

---

## Configuração do Banco de Dados

Crie o banco de dados no MySQL (ou deixe o Hibernate criar automaticamente):

```sql
CREATE DATABASE IF NOT EXISTS contacts_db;
```

Configure as credenciais em `contacts-api/src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=sua_senha
```

---

## Executando a Aplicação

Todos os comandos devem ser executados dentro do diretório `contacts-api/`.

```bash
# Compilar o projeto
./mvnw clean package

# Iniciar a aplicação
./mvnw spring-boot:run

# Executar os testes
./mvnw test
```

---

## Documentação da API (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

---

## Endpoints

### Contatos — `/api/contacts`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/contacts` | Lista todos os contatos (paginado) |
| GET | `/api/contacts/{id}` | Busca contato por ID |
| GET | `/api/contacts/search?name=...` | Busca contatos por nome |
| GET | `/api/contacts/{id}/addresses` | Lista endereços de um contato |
| POST | `/api/contacts` | Cria novo contato |
| PUT | `/api/contacts/{id}` | Atualiza contato completo |
| PATCH | `/api/contacts/{id}` | Atualiza parcialmente o contato |
| DELETE | `/api/contacts/{id}` | Remove contato |

### Endereços — `/api/addresses`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/addresses` | Lista todos os endereços (paginado) |
| GET | `/api/addresses/{id}` | Busca endereço por ID |
| POST | `/api/addresses` | Cria novo endereço |
| PUT | `/api/addresses/{id}` | Atualiza endereço completo |
| DELETE | `/api/addresses/{id}` | Remove endereço |

### Paginação e Ordenação

Todos os endpoints de listagem aceitam os parâmetros:

| Parâmetro | Exemplo | Descrição |
|-----------|---------|-----------|
| `page` | `?page=0` | Número da página (começa em 0) |
| `size` | `?size=10` | Quantidade de itens por página |
| `sort` | `?sort=nome,asc` | Campo e direção de ordenação |

---

## Exemplos de Requisição

### Criar contato
```json
POST /api/contacts
{
  "nome": "Maria Silva",
  "telefone": "11999999999",
  "email": "maria@email.com"
}
```

### Criar endereço
```json
POST /api/addresses
{
  "rua": "Rua das Flores, 100",
  "cidade": "São Paulo",
  "estado": "SP",
  "cep": "01310-100",
  "contactId": 1
}
```

### Atualização parcial de contato
```json
PATCH /api/contacts/1
{
  "telefone": "11888888888"
}
```

---

## Tratamento de Erros

| Código | Situação |
|--------|----------|
| 200 | Operação realizada com sucesso |
| 400 | Dados inválidos (validação falhou) |
| 404 | Recurso não encontrado |
| 500 | Erro interno do servidor |

Exemplo de resposta de erro:
```json
{
  "Erro": "Contato com ID: 5 nao encontrado"
}
```

---

## Estrutura do Projeto

```
contacts-api/src/main/java/br/ifsp/contacts/
├── config/         → SwaggerConfig
├── controller/     → ContactController, AddressController
├── dto/            → DTOs de request e response
├── exception/      → ResourceNotFoundException, GlobalExceptionHandler
├── model/          → Entidades Contact e Address
└── repository/     → ContactRepository, AddressRepository
```

---

## Testes

Os testes unitários cobrem controllers e tratamento de exceções, sem necessidade de contexto Spring ou banco de dados:

```bash
./mvnw test
```

Classes de teste:
- `ContactControllerTest` — testa todos os endpoints de contatos
- `AddressControllerTest` — testa todos os endpoints de endereços
- `GlobalExceptionHandlerTest` — testa os handlers de exceção (400, 404, 500)
- `ResourceNotFoundExceptionTest` — testa a exceção customizada
- `ContactsApiApplicationTests` — verifica inicialização do contexto Spring

---

## Exercícios Implementados

| # | Exercício | Status |
|---|-----------|--------|
| 1 | Implementação de DTOs | ✅ |
| 2 | Persistência em MySQL | ✅ |
| 3 | Paginação e Ordenação | ✅ |
| 4 | Documentação com Swagger | ✅ |