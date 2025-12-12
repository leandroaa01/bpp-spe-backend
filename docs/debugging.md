# Debugging.md

## Bug #1: Erro no routing do frontend quando conectado ao backend

### Identificação
- **Data:** 5-12-2025
- **Reportado por:** Teste manual
- **Severidade:** Alta
- **Módulo:** bpp-frontend/src/router/index.js
### Descrição
O routing estava incorreto, causando um erro e fazendo com que as páginas não fossem corretamente mostradas

### Reprodução
1. Rodar o frontend
2. Tentar carregar a página de registro de usuário
3. Resultado esperado: página de registro carregada corretamente
4. Resultado obtido: página de registro não carregada

### Investigação
**Técnica utilizada:** Debugger + logging estratégico
**Código problemático:**
```javascript
    <div style="margin-top: 20px;">
      <a href="/spe/api/admin/novoUsuarioBolsista">
        <button class="button-class">Adicionar Usuário</button>
      </a>
    </div>
```

**Código corrigido**
```javascript
    <div style="margin-top: 20px;">
      <a href="/admin/register">
        <button class="button-class">Adicionar Usuário</button>
      </a>
    </div>
```


## Bug #2 : Mensagem de sucesso incorretamente mandada em caso de erro 400 de autenticação
- **Data:** 8-12-2025
- **Reportado por:** Teste automatizado
- **Severidade:** Média
- **Módulo:** bpp-spe-backend/src/main/java.dimap.ufrn.spe.api.v1/controllers/AuthenticationCOntroller.java
### Descrição
Ao tentar se logar com uma solicitação inválida, o usuário recebe, incorretamente, uma mensagem de sucesso.

### Reprodução
1. Ir para a página de login
2. tentar fazer login com dados de login inválidos
3. Resultado esperado: mensagem de erro condizente com o erro (erro 400)
5. Resultado obtido: mensagem de login realizado com sucesso (200)

### Investigação
**Técnica utilizada:** Debugger + logging estratégico
**Código problemático:**
```java
  @Operation(summary = "Realizar login", description = "Endpoint para autenticar um usuário e gerar um token de acesso.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", 
                content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
      @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(responseCode = "401", description = "Credenciais incorretas ou não autorizadas", 
                content = @Content(schema = @Schema(implementation = String.class)))
  })
```

**Código corrigido**
```java

  @Operation(summary = "Realizar login", description = "Endpoint para autenticar um usuário e gerar um token de acesso.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", 
                content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
      @ApiResponse(responseCode = "400", description = "Dados de login inválidos", 
                content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(responseCode = "401", description = "Credenciais incorretas ou não autorizadas", 
                content = @Content(schema = @Schema(implementation = String.class)))
  })
```



## Bug #3: Erro de sincronização de relógio (servidor / máquina)

### Identificação
- **Data:** 5-12-2025
- **Reportado por:** Teste automático
- **Severidade:** Alta
- **Módulo:** bpp-spe-backend/src/main/java.dimap.ufrn.spe.api.v1/controllers/BolsistaController.java

### Descrição
Ao tentar fechar pontos, nem todas as branches eram contempladas, resultando em erro no caso de não presença de ponto

### Reprodução
1. logar como bolsista
2. ir para o dashboard
3. tentar fechar ponto
4. Resultado esperado: erro corretamente sinalizado ao tentar marcar o ponto
5. Resultado obtido: erro não sinalizado

### Investigação
**Técnica utilizada:** Debugger + logging estratégico
**Código problemático:**
```java

  if (pontoAberto.isEmpty()) {
      return "Nenhum ponto aberto para finalizar.";
  }

```

**Código corrigido**
```java

  if (!(pontoAberto.isPresent() && !pontoAberto.isEmpty())) {
      return "Nenhum ponto aberto para finalizar.";
  }

```