# AuthGuard API

**API REST de gerenciamento de perfis com autenticação JWT, autorização por papéis e documentação interativa.**

O **AuthGuard** é um projeto Back-End desenvolvido com **Java 21 e Spring Boot 4.1.1**, com foco em arquitetura em camadas, segurança e testes automatizados. A autenticação é realizada pelo **Supabase Auth**, enquanto a API valida os JWTs e gerencia os perfis e as permissões dos usuários no **PostgreSQL**.

## API em produção

A API está hospedada no **Render**, usando **Docker**, com o banco PostgreSQL gerenciado pelo **Neon**.

- **API:** https://authguard-api-bmzi.onrender.com
- **Swagger UI:** https://authguard-api-bmzi.onrender.com/swagger-ui/index.html
- **OpenAPI JSON:** https://authguard-api-bmzi.onrender.com/v3/api-docs
- **Repositório:** https://github.com/ruanxanel/authguard-api

> **Observação:** no plano gratuito do Render, a API pode entrar em repouso após um período de inatividade. A primeira requisição pode demorar mais para responder. A rota raiz (`/`) não possui endpoint público; sem autenticação, ela pode retornar `401 Unauthorized`.

## Funcionalidades

- Criação de perfil a partir da identidade do usuário autenticado.
- Consulta e atualização do próprio perfil.
- Listagem de usuários restrita a administradores (`ADMIN`).
- Autorização por papéis (`USER` e `ADMIN`) consultados no banco de dados.
- Validação de entrada e tratamento padronizado de exceções.
- Respostas JSON personalizadas para erros de autenticação (`401`) e autorização (`403`).
- Documentação OpenAPI com Swagger UI e autenticação Bearer JWT.
- Testes unitários e testes HTTP automatizados.
- Execução com Docker e Docker Compose e deploy em nuvem.

## Tecnologias

| Tecnologia | Utilização |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 4.1.1 | Framework da aplicação |
| Spring Web MVC | Endpoints REST |
| Spring Security | Autenticação e autorização |
| OAuth2 Resource Server | Validação de JWTs |
| Supabase Auth | Emissão e gerenciamento de tokens de autenticação |
| Spring Data JPA / Hibernate | Persistência de dados |
| PostgreSQL | Banco de dados relacional |
| Docker e Docker Compose | Containerização da API e ambiente local |
| Neon | PostgreSQL gerenciado em nuvem |
| Render | Hospedagem da API |
| Jakarta Validation | Validação de entrada |
| Springdoc OpenAPI / Swagger UI | Documentação interativa |
| JUnit 5, Mockito e MockMvc | Testes automatizados |
| Maven | Gerenciamento de dependências e build |
| Lombok | Redução de código repetitivo |

## Arquitetura

A aplicação segue uma organização em camadas:

```text
src/
├── main/
│   ├── java/com/ruan/authguard/
│   │   ├── config/       # Spring Security e OpenAPI
│   │   ├── controller/   # Endpoints HTTP
│   │   ├── dto/          # Dados de entrada, saída e erros
│   │   ├── entity/       # Entidades persistidas
│   │   ├── enums/        # Papéis USER e ADMIN
│   │   ├── exception/    # Exceções e tratamento global
│   │   ├── mapper/       # Conversão Entity → DTO
│   │   ├── repository/   # Acesso ao PostgreSQL
│   │   ├── security/     # Identidade autenticada e handlers
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

O **Supabase Auth emite o JWT**. O AuthGuard atua como **OAuth2 Resource Server**, validando os tokens com as chaves públicas do provedor (JWKS). A aplicação não implementa um endpoint próprio de login ou emissão de tokens.

1. O cliente se autentica no Supabase Auth e obtém um access token.
2. Envia `Authorization: Bearer <token>` aos endpoints protegidos.
3. O Spring Security valida o JWT; a aplicação extrai `sub` (UUID) e `email`.
4. Para operações administrativas, o `PermissaoService` consulta a role do usuário no PostgreSQL.
5. A operação é autorizada ou rejeitada conforme a identidade e as permissões.

> A role `ADMIN` não é atribuída pelo cliente nem aceita apenas por uma claim do JWT. A autorização administrativa depende do registro persistido no banco de dados.

## Endpoints

Todos os endpoints abaixo exigem um JWT válido.

| Método | Rota | Descrição | Acesso | Sucesso |
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

**Exemplo de resposta (`201 Created`):**

```json
{
  "id": "af5f350e-ebe6-4f88-89ea-1fe773e00a11",
  "nome": "Farao",
  "email": "user@example.com",
  "role": "USER",
  "criadoEm": "2026-10-23T16:36:49.163947791"
}
```

O `id` e o `email` são extraídos da identidade autenticada, não enviados no corpo da requisição. Os valores da resposta acima são ilustrativos.

### Exemplo — Atualizar perfil

**Requisição:** `PUT /api/user/profile`

```json
{
  "nome": "Novo nome"
}
```

O campo `nome` é obrigatório e deve conter entre **3 e 100 caracteres**, conforme as validações do DTO.

### Tratamento de erros

Os erros são padronizados em JSON. Exemplo ilustrativo:

```json
{
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Usuário não encontrado",
  "timestamp": "2026-10-23T12:00:00"
}
```

Entre os cenários tratados estão `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` e `409 Conflict`.

## Executando localmente

### Pré-requisitos

- **Docker** com Docker Compose, para executar a aplicação em contêineres.
- **JDK 21**, se optar por executar o Spring Boot diretamente pelo Maven.
- Um projeto **Supabase Auth** capaz de emitir os JWTs esperados pela API.
- Terminal com suporte aos comandos abaixo (exemplos para Linux/macOS).

### 1. Clonar o repositório

```bash
git clone https://github.com/ruanxanel/authguard-api.git
cd authguard-api
```

### 2. Configurar variáveis de ambiente

Crie o arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

Confira as variáveis necessárias ao `compose.yaml`:

```dotenv
POSTGRES_DB=authguard_db
POSTGRES_USER=seu_usuario_local
POSTGRES_PASSWORD=sua_senha_local
```

Os valores acima são exemplos: escolha credenciais locais e **não envie o `.env` ao GitHub**.

### 3. Executar API e PostgreSQL com Docker Compose

```bash
docker compose up -d --build
docker compose ps
```

O Compose inicia o PostgreSQL e a API na mesma rede. No ambiente local, a API fica disponível em `http://localhost:8080`, enquanto a porta externa do PostgreSQL é `5434`.

Para acompanhar os logs:

```bash
docker compose logs -f authguard-api
```

Para encerrar os serviços sem apagar o volume do banco:

```bash
docker compose down
```

> **Cuidado:** `docker compose down -v` também remove o volume declarado do PostgreSQL e pode apagar os dados locais.

### 4. Alternativa: executar Spring Boot diretamente pelo Maven

Se quiser rodar a aplicação fora do contêiner, inicie somente o PostgreSQL e carregue as variáveis do `.env` no terminal:

```bash
docker compose up -d postgres
set -a
source .env
set +a
./mvnw spring-boot:run
```

A configuração padrão do `application.yaml` utiliza `jdbc:postgresql://localhost:5434/authguard_db`. Não inicie simultaneamente a API no Docker e pelo Maven na mesma porta `8080`.

### 5. Executar os testes

Com o PostgreSQL local disponível e as variáveis carregadas:

```bash
./mvnw test
```

Para executar uma classe específica:

```bash
./mvnw -Dtest=UsuarioControllerTest test
```

Alguns testes carregam o contexto Spring completo e podem depender das variáveis e do PostgreSQL local.

## Swagger / OpenAPI

Documentação interativa com suporte ao esquema Bearer JWT.

### Produção

- **Swagger UI:** https://authguard-api-bmzi.onrender.com/swagger-ui/index.html
- **OpenAPI JSON:** https://authguard-api-bmzi.onrender.com/v3/api-docs

### Desenvolvimento local

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

### Como testar

1. Obtenha um access token válido do Supabase Auth.
2. Acesse o Swagger UI e clique em **Authorize**.
3. Informe somente o token, sem repetir `Bearer`.
4. Escolha um endpoint, clique em **Try it out** e depois em **Execute**.

> Não compartilhe capturas que exibam o access token completo: ele é uma credencial de acesso.

## Testes automatizados

O projeto possui testes que cobrem:

- Regras de negócio de `UsuarioService`, `PermissaoService` e `AdminService`.
- Autenticação e autorização das rotas administrativas (`401`, `403` e `200`).
- Criação, consulta e atualização de perfis com MockMvc.
- Validações de entrada e respostas de erro (`400`, `404` e `409`).
- Extração de UUID e e-mail dos objetos JWT.
- Rejeição de tokens malformados e de um token com assinatura fictícia.

**Nota:** o `jwt()` do Spring Security Test simula um usuário autenticado, mas não substitui testes criptográficos com JWTs realmente assinados.

## Observações para produção

- O deploy utiliza variáveis de ambiente configuradas no Render, sem salvar senhas do Neon no repositório.
- O banco Neon é independente do PostgreSQL local; os dados não são transferidos automaticamente.
- Para ambientes com dados importantes, prefira migrações de banco (por exemplo, Flyway) a manter `ddl-auto=update` permanentemente.
- Avalie desativar `spring.jpa.open-in-view` e restringir a documentação pública do Swagger em produção.
- Mantenha a verificação do emissor, audiência, assinatura e algoritmo JWT compatível com o Supabase Auth.

## Objetivo do projeto

O AuthGuard foi desenvolvido como parte da evolução prática em **desenvolvimento Back-End com Java e Spring Boot**, reunindo autenticação externa, autorização baseada em papéis, API REST, persistência relacional, Docker, documentação e testes automatizados em um único projeto.
