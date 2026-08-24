# AGENTS.md - Diretrizes e Padrões do Projeto Grid Manager

Este documento contém as diretrizes obrigatórias de arquitetura, padrões de código e fluxo de trabalho Git que TODOS os agentes de IA devem seguir estritamente.

## 1. Fluxo de Trabalho e Git Flow

### 1.1 Branches
- **NUNCA** faça commits diretamente nas branches `main` ou `develop`.
- **Criação de Branch:** Antes de iniciar qualquer funcionalidade, crie e mude para uma nova branch a partir de `develop` usando a convenção: `feature/<nome-da-funcionalidade>` (ex: `feature/category-crud`).
- **Hotfixes:** Correções urgentes em produção devem ser feitas a partir de `main`, usando a convenção `hotfix/<nome-do-problema>` (ex: `hotfix/fix-null-pointer-order-service`). Ao finalizar, o hotfix deve ser mesclado tanto em `main` quanto em `develop` (via PR em ambos), para que a correção não se perca no próximo release.
- Outras convenções de branch permitidas quando aplicável: `bugfix/<nome>` (correção não urgente a partir de `develop`), `chore/<nome>` (tarefas de manutenção, dependências, configs), `docs/<nome>` (alterações de documentação).

### 1.2 Antes de abrir o PR
- **Sempre atualize a branch local** com as últimas mudanças de `develop` antes de abrir o Pull Request, preferencialmente via `rebase`:
  ```
  git fetch origin
  git rebase origin/develop
  ```
- Resolva todos os conflitos localmente. Nunca abra um PR com conflitos pendentes.
- Rode a suíte de testes e o linter/formatter localmente antes de subir a branch (ver seção 2). PR com testes quebrados ou build falhando não deve ser aberto.
- Garanta que o histórico de commits está limpo (evite commits do tipo "wip", "fix typo", "ajuste" — faça squash/fixup quando fizer sentido antes do PR).

### 1.3 Conventional Commits
Todas as mensagens de commit devem seguir o padrão [Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>(<escopo opcional>): <descrição curta no imperativo>

<corpo opcional explicando o quê e o porquê>

<rodapé opcional: BREAKING CHANGE, refs a issues, etc.>
```

**Tipos permitidos:**
| Tipo | Uso |
|---|---|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `refactor` | Alteração de código sem mudar comportamento externo |
| `test` | Adição ou ajuste de testes |
| `docs` | Alterações de documentação |
| `chore` | Manutenção, dependências, configs, build |
| `perf` | Melhoria de performance |
| `style` | Formatação, espaços, ponto e vírgula (sem alteração de lógica) |
| `ci` | Alterações em pipelines de CI/CD |

**Exemplos:**
```
feat(category): adiciona endpoint de criação de categoria
fix(order): corrige cálculo de total com desconto aplicado
refactor(mapper): extrai lógica de conversão duplicada para método utilitário
test(category-service): cobre cenário de nome duplicado
chore(deps): atualiza Spring Boot para 3.3.2
```
- Commits que introduzem breaking changes devem incluir `BREAKING CHANGE:` no rodapé, explicando o impacto.
- Prefira commits pequenos e atômicos — cada commit deve representar uma unidade lógica de mudança.

### 1.4 Pull Requests (PR)
- Ao finalizar o desenvolvimento, testes e validações de um módulo, **NÃO** realize o merge direto. O agente deve preparar a descrição detalhada e solicitar a abertura de um Pull Request com destino à branch `develop` (ou `main`, no caso de hotfix).
- **Template de descrição do PR** (obrigatório):

  ```markdown
  ## Contexto
  Breve explicação do problema/necessidade que motivou esta mudança.

  ## Mudanças
  - Lista objetiva do que foi implementado/alterado
  - Inclua novos endpoints, entidades, migrações, etc.

  ## Como testar
  Passos para validar manualmente a mudança (endpoints a chamar, payloads de exemplo, etc.)

  ## Checklist
  - [ ] Testes unitários e de integração cobrindo a mudança
  - [ ] Migração Flyway criada (se houve alteração de schema)
  - [ ] Documentação OpenAPI/Swagger atualizada
  - [ ] Branch atualizada com `develop` (rebase feito, sem conflitos)
  - [ ] Lint/formatação (Spotless) executada sem erros

  ## Observações adicionais
  Riscos, dívidas técnicas, decisões de design relevantes.
  ```

- O título do PR também deve seguir Conventional Commits (ex: `feat(category): CRUD completo de categorias`).

### 1.5 Após o merge
- A branch de feature/bugfix/hotfix deve ser **mantida** após o merge no destino (tanto local quanto remota), para organização.
- Exceção: branches de longa duração combinadas explicitamente com o time (ex: `release/*`) podem ser mantidas conforme necessidade do projeto.

## 2. Stack Tecnológica

- **Linguagem/Framework:** Java 17+ | Spring Boot 3+ | Spring Data JPA
- **Segurança:** Spring Security, com autenticação via JWT (stateless). Endpoints públicos devem ser explicitamente declarados na configuração de segurança; todo o restante é protegido por padrão. Senhas/segredos nunca em texto plano ou hardcoded — usar variáveis de ambiente.
- **Banco de Dados:** PostgreSQL 16 | Flyway Migration
- **Ambiente local:** o projeto deve subir via `docker-compose up`, com um `docker-compose.yml` provendo o serviço do PostgreSQL 16 (e demais dependências de infraestrutura, como Redis, se aplicável). A aplicação Spring Boot roda localmente (ou também containerizada, conforme perfil `dev`/`docker` configurado no `application.yml`).
- **Mapeamento e Boilerplate:** MapStruct | Lombok | Jakarta Validation
- **Documentação:** Springdoc OpenAPI (Swagger)
- **Testes:**
  - JUnit 5 + Mockito para testes unitários (camada de service, mappers).
  - Testcontainers com PostgreSQL para testes de integração (repository e endpoints via `MockMvc`/`WebTestClient`), garantindo paridade com o banco real em vez de H2.
  - Cobertura mínima esperada: 80% em `service/`. PRs que reduzem cobertura significativamente devem justificar o motivo na descrição.
  - Testes devem espelhar a estrutura de pacotes principal em `src/test/java/com/gridmanager/<feature>/`.
- **Formatação e Lint:** Spotless (com Google Java Format ou similar configurado no `pom.xml`/`build.gradle`) para formatação automática, executado como parte do build (`mvn spotless:check` deve passar antes de qualquer commit/PR). Opcionalmente, Checkstyle para regras adicionais de convenção de código.

## 3. Arquitetura Package-by-Feature

Todo o código deve estar agrupado pelo domínio da funcionalidade em `com.gridmanager.<feature>`:

- `controller/` -> Endpoints REST e anotações OpenAPI.
- `service/` -> Regras de negócio, validações de duplicidade e exceções. Toda a lógica transacional (`@Transactional`) fica restrita a esta camada — nunca no `controller/` ou `repository/`.
- `repository/` -> Interfaces do Spring Data JPA.
- `entity/` -> Mapeamento relacional JPA. **Entities nunca são expostas diretamente pela API** — a serialização de/para o cliente é sempre feita via DTOs.
- `dto/` -> Contratos de entrada e saída (Java Records).
- `mapper/` -> Conversões MapStruct.
- `exception/` -> Exceções de negócio customizadas do domínio (ex: `CategoryAlreadyExistsException`), lançadas pela camada `service/` e traduzidas globalmente (ver seção 5).
- `config/` -> Classes `@Configuration` específicas do módulo, quando aplicável (a maioria das configs globais — segurança, OpenAPI, CORS — fica em um pacote transversal `com.gridmanager.config`, fora dos pacotes de feature).

**Estrutura de exemplo:**
```
com.gridmanager.category/
├── controller/CategoryController.java
├── service/CategoryService.java
├── repository/CategoryRepository.java
├── entity/Category.java
├── dto/CreateCategoryRequest.java
├── dto/UpdateCategoryRequest.java
├── dto/CategoryResponse.java
├── mapper/CategoryMapper.java
└── exception/CategoryAlreadyExistsException.java
```

- **Testes:** espelham o mesmo pacote em `src/test/java/com/gridmanager/category/...`.
- **Paginação:** endpoints de listagem devem retornar respostas paginadas usando `org.springframework.data.domain.Page<T>`, convertido para um DTO de resposta paginada (`PagedResponse<T>` ou equivalente) contendo os metadados (`page`, `size`, `totalElements`, `totalPages`).
- **Filtros e busca:** uso de `Specification` (Spring Data JPA Specifications) para filtros dinâmicos e combináveis; parâmetros de busca recebidos via `@RequestParam` no `controller/`, nunca construídos manualmente com JPQL concatenado.
- **Versionamento de API:** todos os endpoints devem ser prefixados com `/api/v1/...`, prevendo evolução futura sem breaking changes.

## 4. Convenções de DTOs (Java Records)

- **DTOs de Criação (`Create<Entity>Request`):** Devem conter todas as validações rigorosas de preenchimento obrigatório (`@NotBlank`, `@NotNull`, `@Size`, etc.).
- **DTOs de Atualização (`Update<Entity>Request`):** Diferem dos DTOs de criação pois campos imutáveis (ex: chaves naturais ou códigos fixos) são omitidos. As validações de não-nulidade devem ser flexibilizadas para permitir atualização parcial (PATCH/PUT), e o Mapper deve ser configurado com `nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE`.
- **DTOs de Resposta (`<Entity>Response`):** Representam os dados expostos para os clientes da API. Nunca devem incluir campos sensíveis (senhas, tokens) ou vazar detalhes internos de implementação (ex: entidades JPA relacionadas em profundidade — usar sub-DTOs específicos).
- **DTOs de Listagem Paginada (`PagedResponse<T>`):** Wrapper padrão para respostas de listagem, contendo o conteúdo (`List<T>`) e os metadados de paginação (`page`, `size`, `totalElements`, `totalPages`).
- **DTOs de Filtro (`<Entity>FilterRequest`):** Quando um endpoint de listagem aceitar múltiplos critérios de busca, os parâmetros devem ser agrupados em um DTO de filtro dedicado (via `@ModelAttribute` ou parâmetros individuais), nunca soltos e sem tipagem clara no controller.
- Todos os DTOs devem ser **Java Records** — imutáveis, sem setters, sem lógica de negócio embutida.
- Validações customizadas (ex: regras de formato específicas do domínio) devem usar anotações Jakarta Validation customizadas (`@Constraint`) quando reutilizadas em mais de um DTO.

## 5. Migrações e Tratamento de Erros

### 5.1 Migrações (Flyway)
- Toda alteração de schema requer uma nova migração SQL em `src/main/resources/db/migration/V<Versão>__<descricao>.sql`.
- **Migrações já mergeadas em `develop`/`main` nunca devem ser editadas.** Qualquer correção necessária deve ser feita através de uma **nova migração corretiva**, nunca alterando o arquivo já aplicado (o Flyway valida checksums e quebra o build caso detecte alteração em migração já executada).
- Convenção de nomes de constraints e índices no SQL, para evitar nomes genéricos gerados automaticamente pelo Hibernate:
  - Chaves estrangeiras: `fk_<tabela_origem>_<tabela_destino>` (ex: `fk_order_customer`)
  - Índices: `idx_<tabela>_<coluna(s)>` (ex: `idx_category_name`)
  - Constraints únicas: `uq_<tabela>_<coluna(s)>` (ex: `uq_category_code`)
  - Chaves primárias: `pk_<tabela>`
- Não há rollback automático no Flyway (edição community). Reversões de schema são feitas via nova migração que desfaz a alteração anterior, nunca removendo/editando a migração original.
- Migrações de dados (seed, correção de dados existentes) seguem a mesma convenção de versionamento e ficam no mesmo diretório, claramente identificadas na descrição do arquivo (ex: `V12__seed_default_categories.sql`).

### 5.2 Tratamento de Erros
- Erros devem ser lançados via exceções de negócio (pacote `exception/` de cada feature) e traduzidas globalmente utilizando a especificação `ProblemDetail` (RFC 7807), através de um `@RestControllerAdvice` centralizado.
- Cada exceção de negócio deve mapear para um status HTTP semanticamente correto (ex: `EntityNotFoundException` → 404, `DuplicateResourceException` → 409, erros de validação → 400).
- Respostas de erro de validação (Jakarta Validation) devem detalhar, dentro do `ProblemDetail`, os campos específicos que falharam e a respectiva mensagem.
- Nunca expor stack traces, mensagens de exceção internas do banco de dados ou detalhes de implementação nas respostas de erro ao cliente.

## 6. Logging
- Uso de SLF4J + Logback (padrão do Spring Boot).
- Nível `INFO` para eventos relevantes de negócio (criação/atualização/remoção de recursos); `DEBUG` para detalhes técnicos úteis em desenvolvimento; `WARN`/`ERROR` para falhas e exceções tratadas.
- **Nunca** logar dados sensíveis (senhas, tokens, dados pessoais completos como CPF/cartão de crédito).
- Logs de exceção devem ser feitos no `@RestControllerAdvice` global, evitando duplicação de logs da mesma exceção em múltiplas camadas.

## 7. Documentação da API (Swagger)
- Todo endpoint deve conter `@Operation` com `summary` e `description` claros.
- Todo endpoint deve documentar as respostas possíveis via `@ApiResponse`, incluindo códigos de sucesso e erro (400, 404, 409, etc.), com exemplos de payload quando o contrato não for óbvio.
- DTOs devem usar `@Schema` para descrever campos não triviais e fornecer exemplos.