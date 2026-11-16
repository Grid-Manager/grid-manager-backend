# Grid Manager Backend

API REST para gestão de categorias de automobilismo, pilotos, temporadas, corridas, condições climáticas e resultados. A aplicação concentra regras de elegibilidade, licenciamento e pontuação por categoria, com autenticação baseada em JWT.

## Tecnologias

- Java 21 e Spring Boot 4
- Spring Web MVC, Spring Data JPA e Spring Security
- PostgreSQL 16 e Flyway
- MapStruct, Lombok e Jakarta Validation
- Springdoc OpenAPI / Swagger UI
- JUnit 5 e Mockito

## Pré-requisitos

- JDK 21 ou superior
- Docker Desktop com Docker Compose
- Git

O Maven Wrapper está incluído no repositório; não é necessário instalar Maven globalmente.

## Execução local

1. Inicie o PostgreSQL:

   ```powershell
   docker compose up -d
   ```

2. Defina uma chave JWT com pelo menos 32 caracteres. Não versione essa chave.

   ```powershell
   $env:GRID_MANAGER_JWT_SECRET = "uma-chave-local-com-pelo-menos-32-caracteres"
   ```

3. Inicie a aplicação:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

A API fica disponível em `http://localhost:8080`.

Para encerrar o banco local:

```powershell
docker compose down
```

## Configuração

As propriedades locais estão em `src/main/resources/application.properties`.

| Variável | Obrigatória | Uso |
| --- | --- | --- |
| `GRID_MANAGER_JWT_SECRET` | Sim, para registro e login | Chave de assinatura HMAC do JWT. Mínimo de 32 caracteres. |
| `GRID_MANAGER_OPENAI_API_KEY` | Não | Habilita a moderação remota de nomes de piloto quando configurada. |

Para habilitar a moderação remota, ajuste também `grid-manager.moderation.openai-enabled=true`. Em ambientes compartilhados, use variáveis de ambiente ou o mecanismo de secrets da plataforma; não coloque tokens e senhas no arquivo de propriedades.

## Banco de dados e migrações

O PostgreSQL é iniciado pelo `docker-compose.yml` com os valores locais abaixo:

| Propriedade | Valor local |
| --- | --- |
| Host | `localhost` |
| Porta | `5432` |
| Banco | `gridmanager_db` |
| Usuário | `gridmanager` |

As migrações ficam em `src/main/resources/db/migration` e são executadas no startup. O Hibernate opera com `ddl-auto=validate`, portanto não cria ou altera tabelas.

Não edite migrações já compartilhadas ou aplicadas. Toda alteração de schema deve gerar uma nova migração versionada, por exemplo `V7__add_example_field.sql`.

## Autenticação

Os endpoints abaixo são públicos:

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Cria uma conta com papel inicial `USER` e retorna um token JWT. |
| `POST` | `/api/v1/auth/login` | Autentica uma conta existente e retorna um token JWT. |

Exemplo de registro:

```json
{
  "email": "operador@gridmanager.local",
  "password": "senha-local-segura"
}
```

Todas as demais rotas exigem:

```http
Authorization: Bearer <accessToken>
```

Senhas são armazenadas com BCrypt. O token expira em duas horas por padrão; esse período é configurado por `grid-manager.jwt.expiration`.

## Documentação da API

Com a aplicação em execução:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificação OpenAPI: `http://localhost:8080/api-docs`

Use o botão **Authorize** da Swagger UI para informar um JWT e testar os endpoints protegidos.

## Endpoints disponíveis

| Recurso | Operações atuais |
| --- | --- |
| Categorias | Criar, consultar, listar paginado, atualizar, remover e configurar perfis de pontuação. |
| Pilotos | Criar, consultar, listar paginado, atualizar e remover. |
| Temporadas | Criar e consultar. |
| Corridas | Criar e consultar. |
| Clima | Criar, consultar, atualizar e remover. |
| Resultados | Criar e consultar. |
| Licenças | Consultar; a criação ocorre junto com o piloto. |

As listas de categorias e pilotos aceitam os parâmetros padrão do Spring Data:

```text
GET /api/v1/categories?page=0&size=20&sort=name,asc
GET /api/v1/pilots?page=0&size=20
```

As respostas paginadas seguem o contrato:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

## Regras de domínio relevantes

- Categoria possui nome e sigla únicos; a sigla é gravada em letras maiúsculas.
- Piloto deve ter entre 18 e 60 anos, número único e licença válida para a categoria.
- A licença padrão segue o formato `CATEGORIA-DATA-PILOTO`, por exemplo `F1-20260826-44`.
- Temporadas pertencem a uma categoria e só aceitam pilotos dessa mesma categoria.
- Corridas pertencem a uma temporada; data, horário, voltas e extensão do circuito são validados.
- Resultados de pilotos desclassificados ou que não terminaram recebem zero ponto.
- Pontuação é definida por categoria, tipo de corrida e data de vigência, incluindo bônus de volta mais rápida quando aplicável.
- Um perfil pode informar `seasonId` em `POST /api/v1/categories/{categoryId}/scoring-profiles`. O perfil vigente da temporada tem precedência; sem um perfil correspondente, aplica-se o perfil vigente da categoria.

Erros de validação, duplicidade e recurso inexistente são retornados como `ProblemDetail`, em conformidade com RFC 7807.

## Testes e qualidade

Com o PostgreSQL ativo, execute:

```powershell
.\mvnw.cmd clean test
```

Para uma verificação rápida de compilação:

```powershell
.\mvnw.cmd clean compile
```

## Estrutura do código

O projeto organiza o domínio por funcionalidade. Dentro de cada módulo estão controladores, serviços, repositórios, entidades, DTOs e mappers.

```text
src/main/java/com/Lino/grid_manager_back/
├── auth/           # registro, login e contas de usuário
├── category/       # categorias e perfis de pontuação
├── pilot/          # pilotos e moderação de nomes
├── season/         # temporadas
├── race/           # corridas
├── climate/        # condições climáticas
├── result/         # resultados e cálculo de pontos
├── license/        # consulta de licenças
└── infrastructure/ # segurança, erros e contratos transversais
```

## Contribuição

Consulte `AGENTS.md` antes de iniciar alterações. Em resumo:

- Crie branches a partir de `develop` usando `feature/`, `bugfix/`, `docs/` ou `chore/`.
- Use Conventional Commits.
- Atualize a branch com `develop` antes de abrir uma Pull Request.
- Execute testes e validações antes de enviar alterações.
- Não faça commit diretamente em `main` ou `develop`.

## Licença

Definição de licença pendente.
