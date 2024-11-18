package  com.TakeHomeTest.restApi.service;

import com.TakeHomeTest.restApi.dto.GeneralResponse;
import com.TakeHomeTest.restApi.entity.User;
import com.TakeHomeTest.restApi.repository.UserRepository;
import com.TakeHomeTest.restApi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public GeneralResponse<String> authenticateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            return new GeneralResponse<>(103, "Username atau password salah", null);
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            return new GeneralResponse<>(103, "Username atau password salah", null);
        }
        
        String token = jwtUtil.generateToken(email);
        System.out.println(token);
        return new GeneralResponse<>(0, "Login Sukses", token);
    }
}
