package stocktales.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(basePackages =
{ "${spring.data.jpa.repository.packages}" })

public class DataSourceConfig
{
	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String dbMode;

	@Value("${spring.data.jpa.entity.packages-to-scan}")
	private String entitiesPkgToScan;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	@Profile("prod")
	public DataSource appDataSource()
	{
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasourcetest")
	@Profile("test")
	public DataSource appDataSourceTest()
	{
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.data.jpa.entity")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			DataSource appDataSource)
	{
		// return builder.dataSource(appDataSource).build();

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(appDataSource);
		em.setPackagesToScan(new String[]
		{ "stocktales" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	Properties additionalProperties()
	{
		Properties properties = new Properties();
		properties.setProperty("spring.jpa.hibernate.ddl-auto", "update");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");

		return properties;
	}
}
