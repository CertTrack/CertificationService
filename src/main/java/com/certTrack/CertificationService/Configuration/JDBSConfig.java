package com.certTrack.CertificationService.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JDBSConfig {
	@Bean
	public JdbcTemplate jdbcTemplate(javax.sql.DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
