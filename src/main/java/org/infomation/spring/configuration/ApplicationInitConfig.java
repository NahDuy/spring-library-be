package org.infomation.spring.configuration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.infomation.spring.entity.User;
import org.infomation.spring.enums.Role;
import org.infomation.spring.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()){
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());

                User user = User.builder()
                        .username("admin")
                        .password("admin")
                        .roles(roles)
                        .build();

                userRepository.save(user);
            }
        };
    }
}
