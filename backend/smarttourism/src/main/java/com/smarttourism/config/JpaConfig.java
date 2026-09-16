package com.smarttourism.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA Auditing so @CreatedDate fields are auto-populated.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
