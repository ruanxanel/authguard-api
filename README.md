# AuthGuard API

**API REST de gerenciamento de perfis com autenticação JWT, autorização por papéis e documentação interativa.**

O **AuthGuard** é um projeto Back-End desenvolvido em **Java 21 e Spring Boot 4**, com foco em boas práticas de arquitetura, segurança e testes automatizados. A autenticação é realizada pelo **Supabase Auth**, enquanto a API valida os tokens JWT e gerencia, no **PostgreSQL**, o perfil e as permissões de cada usuário.

## Funcionalidades

- Criação de perfil a partir da identidade do usuário autenticado.
- Consulta e atualização do próprio perfil.
- Listagem de usuários restrita a administradores (`ADMIN`).
- Autorização por papéis (`USER` e `ADMIN`) consultados no banco de dados.
- Validação de entrada e tratamento padronizado de exceções.
- Respostas JSON personalizadas para erros de autenticação (`401`) e autorização (`403`).
- Documentação OpenAPI com Swagger UI e suporte a autenticação Bearer JWT.
- Testes unitários e testes HTTP automatizados.

## Tecnologias

| Tecnologia | Utilização |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 4.1.1 | Framework da aplicação |
| Spring Web MVC | Endpoints REST |
| Spring Security | Autenticação e autorização |
| OAuth2 Resource Server | Validação dos JWTs |
| Supabase Auth | Emissão e gerenciamento de autenticação |
| Spring Data JPA / Hibernate | Persistência de dados |
| PostgreSQL | Banco de dados relacional |
| Docker Compose | Ambiente local do PostgreSQL |
| Jakarta Validation | Validação de entrada |
| Springdoc OpenAPI / Swagger UI | Documentação interativa |
| JUnit 5, Mockito e MockMvc | Testes automatizados |
| Maven | Gerenciamento de dependências e build |
| Lombok | Redução de código repetitivo |

## Arquitetura

A aplicação segue uma separação de responsabilidades em camadas:

```text
src/
├── main/
│   ├── java/com/ruan/authguard/
│   │   ├── config/       # Spring Security e OpenAPI
│   │   ├── controller/   # Endpoints HTTP
│   │   ├── dto/          # Dados de entrada, saída e erros
│   │   ├── entity/       # Entidades persistidas
│   │   ├── enums/        # Papéis de usuário (USER e ADMIN)
│   │   ├── exception/    # Exceções e tratamento global
│   │   ├── mapper/       # Conversão Entity → DTO
│   │   ├── repository/   # Acesso ao PostgreSQL
│   │   ├── security/     # Extração de identidade e handlers de segurança
│   │   └── service/      # Regras de negócio e permissões
│   └── resources/
│       └── application.yaml
└── test/java/com/ruan/authguard/
    ├── controller/       # Testes HTTP com MockMvc
    ├── security/         # Testes de extração da identidade JWT
    └── service/          # Testes unitários com Mockito
```

**Fluxo simplificado:** cliente → Spring Security (JWT) → Controller → Service → Repository → PostgreSQL.

### Autenticação e autorização

O Supabase Auth **emite o JWT**. O AuthGuard atua como **Resource Server**, validando tokens por meio da configuração JWT e das chaves públicas disponibilizadas pelo provedor (JWKS). A API não implementa um endpoint próprio de login ou emissão de JWT.

1. O cliente se autentica pelo Supabase Auth e obtém um access token.
2. Envia `Authorization: Bearer <token>` às rotas protegidas.
3. O Spring Security valida o token, e a aplicação extrai seu `sub` (UUID) e `email`.
4. Para operações administrativas, o `PermissaoService` consulta o papel (`Role`) do usuário no PostgreSQL.
5. A operação é autorizada ou rejeitada conforme a identidade e as permissões.

> **Importante:** a role `ADMIN` não é atribuída pelo cliente nem confiada simplesmente a uma claim do JWT; a autorização administrativa depende do registro persistido no banco de dados.

## Endpoints

Todos os endpoints abaixo exigem JWT válido.

| Método | Rota | Descrição | Acesso | Resposta de sucesso |
|---|---|---|---|---|
| `POST` | `/api/user/profile` | Criar o próprio perfil | Autenticado | `201 Created` |
| `GET` | `/api/user/profile` | Consultar o próprio perfil | Autenticado | `200 OK` |
| `PUT` | `/api/user/profile` | Atualizar o nome do perfil | Autenticado | `200 OK` |
| `GET` | `/api/admin/users` | Listar usuários cadastrados | `ADMIN` | `200 OK` |

### Exemplo — Criar perfil

**Requisição:** `POST /api/user/profile`

```http
Authorization: Bearer SEU_ACCESS_TOKEN
Content-Type: application/json
```

```json
{
  "nome": "Farao"
}
```

**Exemplo de resposta (`201`):**

```json
{
  "id": "af5f350e-ebe6-4f88-89ea-1fe773e00a11",
  "nome": "Farao",
  "email": "user@example.com",
  "role": "USER",
  "criadoEm": "2026-10-17T14:24:38.274811"
}
```

O `id` e o `email` são obtidos da identidade autenticada, e não enviados no corpo do cadastro.

### Exemplo — Atualizar perfil

**Requisição:** `PUT /api/user/profile`

```json
{
  "nome": "Novo nome"
}
```

O campo `nome` é obrigatório e deve conter entre **3 e 100 caracteres**, conforme as validações do DTO.

### Tratamento de erros

A aplicação padroniza erros com campos como:

```json
{
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Usuário não encontrado",
  "timestamp": "2026-10-23T12:00:00"
}
```

*Os valores acima são ilustrativos.* Entre os cenários tratados estão `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` e `409 Conflict`.

## Executando localmente

### Pré-requisitos

- **JDK 21**
- **Docker** com Docker Compose
- Acesso a um projeto **Supabase Auth** configurado para emissão dos JWTs esperados pela API
- Terminal com suporte aos comandos abaixo (exemplos para Linux/macOS)

### 1. Obter o projeto

```bash
git clone <URL_DO_REPOSITORIO_AUTHGUARD>
cd authguard
```

### 2. Configurar as variáveis de ambiente

Se o repositório incluir `.env.example`, utilize-o como referência:

```bash
cp .env.example .env
```

Configure no `.env` as variáveis consumidas pela aplicação e pelo Docker, por exemplo:

```dotenv
POSTGRES_USER=seu_usuario_local
POSTGRES_PASSWORD=sua_senha_local
```

Confira também as demais variáveis exigidas pelo seu arquivo Docker Compose. **Nunca versionar `.env`, senhas, access tokens ou chaves privadas.**

A configuração local usada durante o desenvolvimento apontou para `jdbc:postgresql://localhost:5434/authguard_db`. Caso sua configuração seja diferente, ajuste `application.yaml` e o mapeamento de portas do Docker Compose de forma consistente.

### 3. Iniciar o PostgreSQL

Se o projeto incluir um `compose.yaml` ou `docker-compose.yml` configurado:

```bash
docker compose up -d
docker compose ps
```

### 4. Carregar o `.env` e executar a API

O Maven não carrega automaticamente variáveis definidas apenas no arquivo `.env`. No mesmo terminal em que iniciará a aplicação:

```bash
set -a
source .env
set +a
./mvnw spring-boot:run
```

No Windows, configure as variáveis de ambiente pelo terminal ou pela IDE antes de executar o Maven.

### 5. Testar o projeto

```bash
./mvnw test
```

Para executar uma classe de testes específica:

```bash
./mvnw -Dtest=UsuarioControllerTest test
```

Os testes que carregam o contexto completo do Spring podem precisar do banco e das variáveis de ambiente locais.

## Swagger / OpenAPI

Com a API em execução, acesse:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Especificação OpenAPI:** http://localhost:8080/v3/api-docs

Para testar rotas protegidas no Swagger:

1. Autentique-se pelo Supabase e obtenha um access token válido.
2. Clique em **Authorize** na interface Swagger.
3. Informe somente o token no esquema Bearer, **sem** digitar `Bearer` novamente.
4. Selecione um endpoint, clique em **Try it out** e em **Execute**.

> A documentação Swagger é liberada localmente para facilitar o desenvolvimento. Para produção, avalie restringi-la por ambiente ou autenticação.

## Testes automatizados

O projeto possui testes para:

- Regras de negócio em `UsuarioService`, `PermissaoService` e `AdminService`.
- Autenticação e autorização de rotas administrativas (`401`, `403` e `200`).
- Criação, consulta e atualização de perfis via MockMvc.
- Validação de campos e respostas de erro (`400`, `404` e `409`).
- Extração de UUID e e-mail a partir do JWT.
- Rejeição de tokens malformados ou com assinatura fictícia.

**Nota:** testes que utilizam `jwt()` do Spring Security Test simulam um usuário autenticado; não substituem testes criptográficos com JWTs realmente assinados.

## Observações para produção

Antes de publicar a API, revise as variáveis de ambiente, a conexão PostgreSQL, os emissores e audiências aceitos nos JWTs, o algoritmo de assinatura e as permissões administrativas. Evite publicar com `spring.jpa.hibernate.ddl-auto: update` e `spring.jpa.show-sql: true` sem uma decisão consciente; para produção, prefira migrações de banco e configuração de logs adequada.

## Objetivo do projeto

O AuthGuard foi desenvolvido como parte da evolução prática em **desenvolvimento Back-End com Java e Spring Boot**, reunindo autenticação externa, autorização baseada em papéis, API REST, persistência relacional, documentação e testes automatizados em um único projeto.
