package com.joia.service;

import com.joia.entity.User;
import com.joia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private User userAdmin;
    private User userRegular;

    @BeforeEach
    void setUp() {
        userAdmin = new User("admin", "hashedpasswordAdmin", "Admin User Full Name", Set.of("ROLE_ADMIN", "ROLE_USER"));
        userAdmin.setId(1L);

        userRegular = new User("user", "hashedpasswordUser", "Regular User Full Name", Set.of("ROLE_USER"));
        userRegular.setId(2L);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve carregar usuário 'admin' com sucesso quando username existe")
    void loadUserByUsername_deveRetornarUserDetails_quandoAdminExiste() {
        // Arrange
        String username = "admin";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userAdmin));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(userAdmin.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(2);
        Set<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertThat(authorityStrings).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve carregar usuário 'user' com sucesso quando username existe")
    void loadUserByUsername_deveRetornarUserDetails_quandoUserRegularExiste() {
        // Arrange
        String username = "user";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userRegular));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(userRegular.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve lançar UsernameNotFoundException quando username não existe")
    void loadUserByUsername_deveLancarExcecao_quandoUsernameNaoExiste() {
        // Arrange
        String usernameInexistente = "usuarioinexistente";
        when(userRepository.findByUsername(usernameInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(usernameInexistente))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado com username: " + usernameInexistente);
        verify(userRepository, times(1)).findByUsername(usernameInexistente);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve lançar UsernameNotFoundException para username nulo")
    void loadUserByUsername_deveLancarExcecao_quandoUsernameNulo() {
        // Arrange
        String usernameNulo = null;
        // O mock pode ou não ser configurado para null, a implementação de findByUsername deve lidar com isso.
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(usernameNulo))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado com username: " + usernameNulo);
        verify(userRepository, times(1)).findByUsername(usernameNulo);
    }
}