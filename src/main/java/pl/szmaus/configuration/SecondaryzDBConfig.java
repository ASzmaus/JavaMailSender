package pl.szmaus.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.szmaus.secondary.entity.GmFs;
import pl.szmaus.secondaryz.entity.R3DocumentFiles;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "secondaryzEntityManager",
        transactionManagerRef = "secondaryzTransactionManager",
        basePackages = "pl.szmaus.secondaryz.repository"
)
public class SecondaryzDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.secondaryz.datasource")
    public DataSource secondaryzDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(name = "secondaryzEntityManager")
    public LocalContainerEntityManagerFactoryBean secondaryzEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(secondaryzDataSource())
                .properties(hibernateProperties())
                .packages(R3DocumentFiles.class)
                .persistenceUnit("secondaryzPU")
                .build();
    }

    @Bean(name = "secondaryzTransactionManager")
    public PlatformTransactionManager secondaryzTransactionManager(@Qualifier("secondaryzEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map hibernateProperties() {

        Resource resource = new ClassPathResource("hibernate.properties");

        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);

            return properties.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            e -> e.getValue())
                    );
        } catch (IOException e) {
            return new HashMap();
        }
    }
}