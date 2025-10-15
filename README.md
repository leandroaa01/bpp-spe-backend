# Sistema de Ponto Eletrônico (SPE) - Backend

Sistema de Ponto Eletrônico desenvolvido em Java com Spring Boot para gerenciamento de ponto de bolsistas e técnicos.

## 📋 Descrição do Projeto

O SPE é um sistema backend para controle de ponto eletrônico que permite o registro de entrada e saída de bolsistas, com autenticação baseada em JWT e diferentes níveis de permissão (ADMIN, BOLSISTA, TECNICO).

### Funcionalidades Principais

- **Autenticação JWT**: Sistema de login seguro com tokens JWT
- **Gerenciamento de Usuários**: Registro e autenticação de usuários
- **Controle de Ponto**: Registro de entrada e saída de bolsistas
- **Cálculo Automático de Horas**: Sistema calcula automaticamente as horas trabalhadas
- **Visualização de Pontos**: Bolsistas podem visualizar seu histórico de pontos
- **Diferentes Perfis**: Suporte para ADMIN, BOLSISTA e TECNICO

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security** (Autenticação e Autorização)
- **Spring Data JPA** (Persistência de dados)
- **MySQL** (Banco de dados)
- **JWT** (JSON Web Tokens) via auth0-java-jwt
- **Lombok** (Redução de código boilerplate)
- **Maven** (Gerenciamento de dependências)

## 📦 Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- **Java JDK 17** ou superior
- **Maven 3.6+** (ou use o Maven Wrapper incluído)
- **MySQL 8.0+**
- **Git** (para clonar o repositório)

## ⚙️ Configuração do Ambiente

### 1. Clonar o Repositório

```bash
git clone https://github.com/leandroaa01/bpp-spe-backend-java.git
cd bpp-spe-backend-java
```

### 2. Configurar o Banco de Dados MySQL

#### 2.1. Criar o Banco de Dados

Acesse o MySQL e execute:

```sql
CREATE DATABASE SPE;
```

#### 2.2. Configurar Credenciais

Edite o arquivo `src/main/resources/application.properties` e ajuste as configurações do banco de dados:

```properties
# Configuração do Banco de Dados
spring.datasource.url=jdbc:mysql://localhost/SPE?serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=sua_senha_aqui

# Configuração do JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuração de Segurança - JWT Secret
api.security.token.secret=sua_chave_secreta_aqui
```

**⚠️ IMPORTANTE**: Altere os seguintes valores:
- `spring.datasource.username`: Seu usuário do MySQL
- `spring.datasource.password`: Sua senha do MySQL
- `api.security.token.secret`: Uma chave secreta forte para geração dos tokens JWT

### 3. Variáveis de Ambiente (Opcional)

Para maior segurança em produção, você pode configurar as variáveis de ambiente:

```bash
export DB_URL=jdbc:mysql://localhost/SPE?serverTimezone=UTC
export DB_USERNAME=seu_usuario
export DB_PASSWORD=sua_senha
export JWT_SECRET=sua_chave_secreta
```

E modificar o `application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
api.security.token.secret=${JWT_SECRET}
```

## 🏃 Como Executar o Projeto

### Opção 1: Usando Maven Wrapper (Recomendado)

#### No Linux/Mac:
```bash
./mvnw clean install
./mvnw spring-boot:run
```

#### No Windows:
```cmd
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### Opção 2: Usando Maven Instalado

```bash
mvn clean install
mvn spring-boot:run
```

### Opção 3: Executar o JAR

```bash
mvn clean package
java -jar target/spe-0.0.1-SNAPSHOT.jar
```

O servidor será iniciado em: `http://localhost:8080`

## 👤 Usuário Administrador Padrão

Ao executar a aplicação pela primeira vez, um usuário administrador será criado automaticamente:

- **Username**: `redes`
- **Password**: `123456789`
- **Email**: `redes@dimap.ufrn.br`
- **Role**: `ADMIN`

**⚠️ IMPORTANTE**: Altere a senha padrão após o primeiro login!

## 📚 API Endpoints

### Autenticação

#### POST `/auth/login`
Realizar login no sistema

**Request Body:**
```json
{
  "username": "redes",
  "password": "123456789"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### POST `/auth/register`
Registrar novo usuário

**Request Body:**
```json
{
  "name": "João Silva",
  "username": "joao.silva",
  "password": "senha123",
  "email": "joao.silva@example.com",
  "roles": "BOLSISTA"
}
```

### Endpoints do Bolsista (Requer Role: BOLSISTA)

Todos os endpoints abaixo requerem o header de autenticação:
```
Authorization: Bearer {seu_token_jwt}
```

#### POST `/spe/api/bolsista/entrada`
Registrar entrada (início do expediente)

**Response:**
```
"Entrada registrada para joao.silva"
```

#### POST `/spe/api/bolsista/saida`
Registrar saída (fim do expediente)

**Response:**
```
"Saída registrada com sucesso! Total de horas: 8.5"
```

#### GET `/spe/api/bolsista/meus-pontos`
Visualizar histórico de pontos

**Response:**
```json
[
  {
    "horaDeEntrada": "2025-10-12T08:00:00",
    "horaDeSaida": "2025-10-12T17:00:00",
    "qtdDeHorasFeitas": "9.0 Hrs"
  }
]
```

## 🔐 Sistema de Autenticação

### Fluxo de Autenticação

1. **Login**: O usuário envia username e password para `/auth/login`
2. **Token JWT**: O sistema retorna um token JWT válido por 2 horas
3. **Autenticação**: Para acessar endpoints protegidos, inclua o token no header:
   ```
   Authorization: Bearer {token}
   ```
4. **Autorização**: O sistema verifica as permissões do usuário baseado na role

### Roles (Perfis de Usuário)

- **ADMIN**: Acesso total ao sistema
- **BOLSISTA**: Pode registrar entrada/saída e visualizar seus próprios pontos
- **TECNICO**: Perfil para técnicos (funcionalidades podem ser expandidas)

## 📁 Estrutura do Projeto

```
bpp-spe-backend-java/
├── src/
│   ├── main/
│   │   ├── java/dimap/ufrn/spe/api/v1/spe/
│   │   │   ├── controllers/        # Controladores REST
│   │   │   │   ├── AuthenticationController.java
│   │   │   │   └── BolsistaController.java
│   │   │   ├── dtos/               # Data Transfer Objects
│   │   │   │   ├── AuthenticationDTO.java
│   │   │   │   ├── LoginResponseDTO.java
│   │   │   │   ├── PontoDTO.java
│   │   │   │   └── RegisterDTO.java
│   │   │   ├── models/             # Entidades do banco de dados
│   │   │   │   ├── Ponto.java
│   │   │   │   ├── Roles.java
│   │   │   │   └── User.java
│   │   │   ├── repositories/       # Repositórios JPA
│   │   │   │   ├── PontoRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/           # Configurações de segurança
│   │   │   │   ├── SecurityConfigurations.java
│   │   │   │   ├── SecurityFilter.java
│   │   │   │   └── TokenService.java
│   │   │   ├── services/           # Serviços de negócio
│   │   │   │   └── AuthorizationService.java
│   │   │   └── SpeApplication.java # Classe principal
│   │   └── resources/
│   │       └── application.properties  # Configurações da aplicação
│   └── test/                       # Testes unitários e de integração
├── pom.xml                         # Configuração do Maven
└── README.md                       # Este arquivo
```

## 🧪 Executar Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes com relatório detalhado
./mvnw test -Dtest.output=verbose
```

## 🔧 Troubleshooting (Solução de Problemas)

### Erro de Conexão com o Banco de Dados

**Problema**: `Communications link failure`

**Solução**:
1. Verifique se o MySQL está rodando: `sudo service mysql status`
2. Confirme que o banco de dados `SPE` existe
3. Verifique as credenciais no `application.properties`
4. Teste a conexão: `mysql -u root -p`

### Erro: "Table doesn't exist"

**Problema**: Tabelas não foram criadas automaticamente

**Solução**:
1. Verifique se `spring.jpa.hibernate.ddl-auto=update` está configurado
2. Delete o banco e deixe o Hibernate recriá-lo automaticamente
3. Ou configure para `create` na primeira execução (⚠️ isso apaga dados existentes)

### Erro: "JWT Token Invalid"

**Problema**: Token JWT inválido ou expirado

**Solução**:
1. Tokens expiram após 2 horas - faça login novamente
2. Verifique se o `api.security.token.secret` é o mesmo usado para gerar o token
3. Certifique-se de incluir "Bearer " antes do token no header

### Porta 8080 já em uso

**Problema**: `Port 8080 is already in use`

**Solução**:
1. Altere a porta no `application.properties`:
   ```properties
   server.port=8081
   ```
2. Ou finalize o processo que está usando a porta 8080

### Erro de Compilação com Lombok

**Problema**: Erros relacionados a getters/setters não encontrados

**Solução**:
1. Certifique-se de que sua IDE tem o plugin Lombok instalado
2. Execute: `./mvnw clean compile`
3. No IntelliJ IDEA: Habilite "Enable annotation processing" nas configurações

## 📝 Notas de Desenvolvimento

### Adicionar Novos Endpoints

1. Crie um novo Controller em `controllers/`
2. Use `@PreAuthorize("hasRole('ROLE_NAME')")` para proteger endpoints
3. Documente o endpoint neste README

### Modificar o Modelo de Dados

1. Edite a entidade em `models/`
2. O Hibernate atualizará o schema automaticamente (se `ddl-auto=update`)
3. Para mudanças complexas, considere usar Flyway ou Liquibase

### Configuração de CORS (se necessário)

Se você precisar permitir requisições de um frontend em outro domínio, adicione no `SecurityConfigurations.java`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## 📄 Licença

Este projeto está sob a licença [especificar licença].

## 👥 Contribuidores

- Projeto desenvolvido para o DIMAP/UFRN

## 📞 Suporte

Para dúvidas ou problemas, entre em contato com a equipe de desenvolvimento ou abra uma issue no GitHub.

---

**Desenvolvido com ☕ e Spring Boot**
