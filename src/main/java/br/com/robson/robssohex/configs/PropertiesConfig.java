package br.com.robson.robssohex.configs;

import br.com.robson.robssohex.configs.properties.DatabaseProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@AllArgsConstructor
public class PropertiesConfig {

    private final SecretManagerConfiguration secretManagerConfiguration;


    @Bean
    public DatabaseProperties getDatabaseProperties() throws IOException {
        return secretManagerConfiguration.getSecretProperties("mysql-secret", DatabaseProperties.class);
    }

    @Bean
    public DataSource dataSource(DatabaseProperties dbProps) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbProps.getUrl());
        dataSource.setUsername(dbProps.getUsername());
        dataSource.setPassword(dbProps.getPassword());
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }

}
