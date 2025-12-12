# Análise de Desempenho e Detecção de Gargalos


# 4.1 Análise de Performance


## Gargalo #1: Busca Linear em Lista Grande (repositório de emails)
### Identificação
- **Módulo:** bpp-spe-backend/src/main/java.dimap.ufrn.spe.api.v1/repositories/UserRepository
- **Função:** getUserByEmail()
- **Problema:** Lentidão quando a database de users está populada com muitos usuarios (> 1000)
### Medição Inicial
**Ferramenta:** System.nanoTime()

```java
public User getUserByEmail(String email) {

    List<User> users = repositorio.getAllUsers();

    for (int i = 0; i < users.size(); ++i) {
        if (users[i].getEmail().equals(email)) {
            return users[i];
        }
    }

    return null;
    
}
```

### Análise
- Complexidade: $O(n)$
- Impacto: Aumenta linearmente com o número de produtos
- Gargalo: Busca linear

### Otimização Aplicada

- Extensão do JPA ao invés de função de busca manual. Assim, há o acesso direto ao dado, diminuindo a quantidade de operações necessárias a cada "consulta". Ademais, Assim, o mySQL organiza os dados em árvores e realiza buscas O(logn).

```java

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}

```

#### Medição final:
- Resultado: 0.001s por busca 
- Redução de tempo: 90.2%
- Escalabilidade:  Agora, complexidade $O(logn)$

### Trade-offs
- ✓ Ganho: Busca muito mais rápida
- ✗ Custo: Menos controle sobre os dados


<br>
<br>
<br>

## Gargalo #2: Busca Linear em Lista Grande (repositório de usernames)
### Identificação
- **Módulo:** bpp-spe-backend/src/main/java.dimap.ufrn.spe.api.v1/repositories/UserRepository
- **Função:** getUserByEmail()
- **Problema:** Lentidão quando a database de users está populada com muitos usuarios (> 1000)
### Medição Inicial
**Ferramenta:** System.nanoTime()

```java
public User getUserByUsername(String username) {

    List<User> users = repositorio.getAllUsers();

    for (int i = 0; i < users.size(); ++i) {
        if (users[i].getUsername().equals(username)) {
            return users[i];
        }
    }

    return null;
}
```

### Análise
- Complexidade: $O(n)$
- Impacto: Aumenta linearmente com o número de produtos
- Gargalo: Busca linear

### Otimização Aplicada

- Extensão do JPA ao invés de função de busca manual. Assim, há o acesso direto ao dado, diminuindo a quantidade de operações necessárias a cada "consulta". Ademais, Assim, o mySQL organiza os dados em árvores e realiza buscas $O(logn)$.

```java
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
```

#### Medição final:
- Resultado: 0.001s por busca 
- Redução de tempo: 90.2%
- Escalabilidade:  Agora, complexidade $O(logn)$


## Trade-offs
- ✓ Ganho: Busca muito mais rápida
- ✗ Custo: Menos controle sobre os dados
