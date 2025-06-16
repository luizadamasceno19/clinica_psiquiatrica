package com.clinica.Clinica.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clinica.Clinica.model.Role;
import com.clinica.Clinica.model.User;
import com.clinica.Clinica.repository.UserRepository;
import com.clinica.Clinica.security.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Busca o usuário por username (login) no banco e converte para UserDetails,
     * já colocando a Role correta (ROLE_MEDICO ou ROLE_PACIENTE).
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado com username: " + username);
        }

        User user = userOpt.get();
        // Pega a role que está no User (enum Role) e converte para GrantedAuthority
        Role roleEnum = user.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority(roleEnum.name());
        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

        // Constrói o UserDetailsImpl (que também deve carregar authorities)
        return UserDetailsImpl.build(user, authorities);
    }
}
