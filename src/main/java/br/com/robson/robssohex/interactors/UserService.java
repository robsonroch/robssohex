package br.com.robson.robssohex.interactors;

import br.com.robson.robssohex.entities.User;
import br.com.robson.robssohex.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUserFromPreSignup(String username, String email, String senha, LocalDate dataNascimento) {
        // Verifica se já existe usuário com o mesmo e-mail ou username
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username já cadastrado.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setSenha(passwordEncoder.encode(senha));
        user.setDataNascimento(dataNascimento);

        userRepository.save(user);
    }
}