package dimap.ufrn.spe.api.v1.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import dimap.ufrn.spe.api.v1.dtos.*;
import dimap.ufrn.spe.api.v1.models.*;
import dimap.ufrn.spe.api.v1.repositories.*;
import dimap.ufrn.spe.api.v1.security.*;
import dimap.ufrn.spe.api.v1.services.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EndpointsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private PontoRepository pontoRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PontoService pontoService;

    @Test
    public void testAuthenticationLoginSuccess() throws Exception {
        // Arrange - Criar User para simular autenticação bem-sucedida
        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setRoles(Roles.ADMIN);
        mockUser.setName("Admin User");
        mockUser.setEmail("admin@email.com");

        AuthenticationDTO authDTO = new AuthenticationDTO("admin", "123456789");

        // Mock da autenticação para simular autenticação bem-sucedida
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(tokenService.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON) // define o tipo de conteúdo como JSON
                .content(mapper.writeValueAsString(authDTO))) // converte o objeto authDTO para JSON
                // .andDo(print())
                .andExpect(status().isOk()) // verifica se o status da resposta é 200 OK
                .andExpect(jsonPath("$.token").value("mocked-jwt-token")) // verifica se o token retornado é o esperado
                .andExpect(jsonPath("$.role").exists()); // verifica se o campo role existe na resposta

    }

    @Test
    public void testAuthenticationLoginFailure() throws Exception {
        // Arrange - Preparar dados de login inválidos
        AuthenticationDTO authDTO = new AuthenticationDTO("user", "wrong-password");

        // Mock da autenticação para lançar exceção de credenciais inválidas
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert -
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(authDTO)))
                .andExpect(status().is4xxClientError()); // verifica se o status da resposta é 4xx (erro do cliente)
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        // Arrange - Preparar dados de registro válidos
        RegisterDTO registerDTO = new RegisterDTO(
                "Novo Usuário", "newBolsista", "123456789", "newBolsista@email.com", Roles.BOLSISTA);

        when(userRepository.findByUsername("newBolsista")).thenReturn(null); // Simula que o username não existe
        when(userRepository.findByEmail("newBolsista@email.com")).thenReturn(null); // Simula que o email não existe
        when(userRepository.save(any(User.class))).thenReturn(new User()); // Simula o salvamento do usuário

        // Act & Assert
        mockMvc.perform(post("/spe/api/admin/register")
                .with(adminAuth()) // Autenticação como admin
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(registerDTO)))
                // .andDo(print())
                .andExpect(status().isOk()) // Verifica se o status da resposta é 200 OK
                .andExpect(content().string("User registered successfully")); // Verifica a mensagem de sucesso
    }

    @Test
    public void testRegistrarEntradaSuccess() throws Exception {
            // Arrange - Simula que não há pontos abertos
            when(pontoRepository.findAllByBolsista(any(User.class))).thenReturn(List.of());
            // Simula o salvamento do ponto
            when(pontoRepository.save(any(Ponto.class))).thenReturn(new Ponto());

            // Act & Assert
            mockMvc.perform(post("/spe/api/bolsista/entrada")
                            .with(bolsistaAuth())) // Autenticação como bolsista
                            .andExpect(status().isOk()) // Verifica se o status da resposta é 200 OK
                            .andExpect(content().string("Entrada registrada com sucesso!")); // Verifica a mensagem de sucesso
    }
    
    @Test
    public void testRegistrarSaidaSuccess() throws Exception {
        // Arrange - Simula que há um ponto aberto
        Ponto openPonto = new Ponto();
        openPonto.setId(1L);
        openPonto.setBolsista(new User());
        openPonto.setHoraDeEntrada(java.time.LocalDateTime.now().minusHours(2));
        openPonto.setHoraDeSaida(null); // Saída ainda não registrada

        when(pontoRepository.findAllByBolsista(any(User.class))).thenReturn(List.of(openPonto));
        // Simula o salvamento do ponto com a saída registrada
        when(pontoRepository.save(any(Ponto.class))).thenReturn(openPonto);
        var timeFeito = openPonto.getQtdDeHorasFeitas();

        // Act & Assert
        mockMvc.perform(post("/spe/api/bolsista/saida")
        .with(bolsistaAuth())) // Autenticação como bolsista
        .andExpect(status().isOk()) // Verifica se o status da resposta é 200 OK
        .andExpect(content().string("Saída registrada com sucesso! Total de horas: 2.0")); // Verifica a mensagem de sucesso
    }
    
    @Test
    public void testRegistrarSaidaNoOpenPonto() throws Exception {
        // Arrange - Simula que não há pontos abertos
        when(pontoRepository.findAllByBolsista(any(User.class))).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(post("/spe/api/bolsista/saida")
        .with(bolsistaAuth())) // Autenticação como bolsista
        .andExpect(status().isOk()) // Verifica se o status da resposta é 200 OK
        .andExpect(content().string("Nenhum ponto aberto para finalizar.")); // Verifica a mensagem de nenhum ponto aberto
    }
    
    @Test
        public void testMudarSenhaSemPermission() throws Exception {
            // Arrange - Preparar dados para mudança de senha
            PasswordDTO passwordDTO = new PasswordDTO("NovaSenha123");

            // Act & Assert
            mockMvc.perform(put("/spe/api/admin/mudar-senha/bolsista/1")
                    // .with(bolsistaAuth()) // Não autentica o usuário
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(passwordDTO)))
                    .andExpect(status().is4xxClientError()); // Verifica se o status da resposta é 4xx (erro do cliente)
        }

    @Test
    public void testMudarSenhaSuccess() throws Exception {
        // Arrange - Preparar dados para mudança de senha
        PasswordDTO passwordDTO = new PasswordDTO("NovaSenha123");
        User mockBolsista = new User();
        mockBolsista.setId(1L);
        mockBolsista.setRoles(Roles.BOLSISTA);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockBolsista)); // Simula a busca do usuário pelo ID
        when(userRepository.save(any(User.class))).thenReturn(mockBolsista); // Simula o salvamento do usuário com a nova senha

        // Act & Assert
        mockMvc.perform(put("/spe/api/admin/mudar-senha/bolsista/1")
         .with(bolsistaAuth())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(passwordDTO)))
        .andExpect(status().isOk()) // Verifica se o status da resposta é 200 OK
       .andExpect(content().string("Password updated successfully")); // Verifica a mensagem de sucesso
    }
    
    @Test
    public void testMudarDadosSuccess() throws Exception {
        // Arrange - Preparar dados para mudança de dados
        UpdateDTO updateUserDTO = new UpdateDTO("Bolsista Atualizado", "updatedBolsista", "updated@example.com",
                        Roles.BOLSISTA);
        User mockBolsista = new User();
        mockBolsista.setId(1L);
        mockBolsista.setRoles(Roles.BOLSISTA);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockBolsista)); // Simula a busca do usuário pelo ID
        when(userRepository.findByUsername("updatedBolsista")).thenReturn(null); // Simula que o username não existe
        when(userRepository.findByEmail("updated@example.com")).thenReturn(null); // Simula que o email não existe
        when(userRepository.save(any(User.class))).thenReturn(mockBolsista); // Simula o salvamento do usuário com os novos dados
        // Act & Assert
        mockMvc.perform(put("/spe/api/admin/mudar-dados/bolsista/1")
         .with(bolsistaAuth())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(updateUserDTO)))
        .andExpect(status().isOk()) // Verifica se o status da resposta é 200 OK
       .andExpect(content().string("User data updated successfully.")); // Verifica a mensagem de sucesso
    }

    // Helpers para autenticação nos testes
    private RequestPostProcessor adminAuth() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setRoles(Roles.ADMIN);
        adminUser.setPassword(new BCryptPasswordEncoder().encode("123456789"));
        Authentication auth = new UsernamePasswordAuthenticationToken( adminUser, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return authentication(auth);
    }

    private RequestPostProcessor bolsistaAuth() {
        User bolsistaUser = new User();
        bolsistaUser.setUsername("bolsista");
        bolsistaUser.setRoles(Roles.BOLSISTA);
        bolsistaUser.setPassword(new BCryptPasswordEncoder().encode("123456789"));
        Authentication auth = new UsernamePasswordAuthenticationToken( bolsistaUser, null, List.of(new SimpleGrantedAuthority("ROLE_BOLSISTA")));
        return authentication(auth);
    }
}