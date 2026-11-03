# EcoEnergia API — Indicadores de Sustentabilidade e Eficiência Energética

**Tema ESG:** 1 – Eficiência energética e sustentabilidade
**Subtema:** Indicadores de sustentabilidade para uma empresa

API RESTful que permite a uma empresa registrar o consumo de energia (por fonte), definir metas mensais,
receber **alertas automáticos** quando o consumo ultrapassa a meta e consultar **indicadores de sustentabilidade**
(percentual de energia renovável, emissões estimadas de CO₂, aderência à meta e score A–D).

## Tecnologias
Java 17 · Spring Boot 3.3 · Spring Web · Spring Data JPA · Bean Validation · **Spring Security + JWT** ·
**Oracle** (Free 23ai) · **Flyway** · **Docker / Docker Compose** · Springdoc OpenAPI (Swagger)

## Como executar (Docker)
```bash
docker compose up --build
```
O primeiro start do Oracle leva ~1–2 minutos. Depois acesse:

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

As migrations do Flyway (`src/main/resources/db/migration`) são executadas automaticamente na subida da API.
Usuário administrador padrão: `admin@ecoenergia.com` / `Admin@12345` (altere via variáveis de ambiente, ver `.env.example`).

### Executar localmente (sem Docker para a API)
```bash
docker compose up -d oracle
mvn spring-boot:run
```

## Segurança
- Autenticação via JWT (`POST /auth/login`), enviar no header `Authorization: Bearer <token>`.
- `/auth/**`, Swagger e `/actuator/health` são públicos; todo o resto exige autenticação.
- Perfis: `USER` (leitura, criação e atualização) e `ADMIN` (também pode executar **DELETE**).
- Senhas armazenadas com BCrypt.

## Endpoints
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| POST | /auth/registro | Cadastra usuário (perfil USER) | público |
| POST | /auth/login | Autentica e retorna o JWT | público |
| GET | /empresas | Lista empresas (paginado) | autenticado |
| GET | /empresas/{id} | Busca empresa | autenticado |
| POST | /empresas | Cadastra empresa | autenticado |
| PUT | /empresas/{id} | Atualiza empresa | autenticado |
| DELETE | /empresas/{id} | Remove empresa | ADMIN |
| POST | /empresas/{empresaId}/medicoes | Registra medição de consumo (gera alerta se exceder a meta) | autenticado |
| GET | /empresas/{empresaId}/medicoes?inicio=&fim= | Lista medições (paginado) | autenticado |
| GET | /medicoes/{id} | Busca medição | autenticado |
| PUT | /medicoes/{id} | Atualiza medição | autenticado |
| DELETE | /medicoes/{id} | Remove medição | ADMIN |
| POST | /empresas/{empresaId}/metas | Define meta mensal (inativa a anterior) | autenticado |
| GET | /empresas/{empresaId}/metas | Lista metas | autenticado |
| PUT | /metas/{id} | Atualiza meta | autenticado |
| DELETE | /metas/{id} | Remove meta | ADMIN |
| GET | /alertas?empresaId=&status= | Lista alertas (paginado) | autenticado |
| PATCH | /alertas/{id}/resolver | Marca alerta como resolvido | autenticado |
| GET | /empresas/{empresaId}/indicadores?inicio=&fim= | Indicadores de sustentabilidade da empresa | autenticado |
| GET | /indicadores-sustentabilidade | Visão geral e ranking de todas as empresas | autenticado |

## Indicadores calculados
- **Consumo total / renovável / não renovável (kWh)** e média mensal
- **% de energia renovável** (SOLAR, EOLICA e BIOMASSA são renováveis)
- **Emissão estimada de CO₂ (kg)** = consumo não renovável × fator (`FATOR_EMISSAO`, padrão 0,0617 kg/kWh — configurável)
- **Desvio em relação à meta (%)** e alertas abertos
- **Score de sustentabilidade (0–100)** = 50% % renovável + 50% aderência à meta → classificação **A, B, C ou D**

## Tratamento de exceções e validação
`GlobalExceptionHandler` padroniza as respostas de erro (400 validação, 401, 403, 404, 409 conflito, 422 regra de negócio, 500).
Validações via Bean Validation nos DTOs (ex.: CNPJ com 14 dígitos, consumo > 0, data de medição não futura).

## Banco de dados e migrations
- `V1__criar_tabelas.sql`: tabelas `TB_USUARIO`, `TB_EMPRESA`, `TB_MEDICAO`, `TB_META_CONSUMO`, `TB_ALERTA` (PKs identity, FKs, checks e índices)
- `V2__dados_demonstracao.sql`: empresa de demonstração com 6 meses de medições e meta
- `spring.jpa.hibernate.ddl-auto=none`: o schema é controlado somente pelo Flyway.

## Exemplo rápido
```bash
TOKEN=$(curl -s -X POST localhost:8080/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"admin@ecoenergia.com","senha":"Admin@12345"}' | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])")
curl -H "Authorization: Bearer $TOKEN" localhost:8080/empresas/1/indicadores
```
