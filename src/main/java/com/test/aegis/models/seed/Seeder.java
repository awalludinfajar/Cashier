package com.test.aegis.models.seed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import com.test.aegis.models.entities.RoleEntities;
import com.test.aegis.models.repos.RoleRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Seeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    static final Logger log = LoggerFactory.getLogger(Seeder.class);
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.getOptionValues("seeder") != null) {
            List<String> seeder = Arrays.asList(args.getOptionValues("seeder").get(0).split(","));
            if (seeder.contains("role")) {
                seedRole();
                log.info("Success run role seeder");
            }
        } else {
            log.info("Role Seeder Skipped.");
        }
    }

    public void seedRole() {
        List<String> roles = new ArrayList<>();

        roles.add("ADMIN");
        roles.add("KASIR");

        var index = 0;
        for (String role : roles) {
            this.roleRepository.save(
                RoleEntities.builder()
                            .name(role)
                            .modifiedAt(Instant.now())
                            .createdBy("main")
                            .updatedBy("main").build()
            );
            log.info("Success run RoleSeeder {}",roles.get(index));
            index++;
        }
    }
    
}
