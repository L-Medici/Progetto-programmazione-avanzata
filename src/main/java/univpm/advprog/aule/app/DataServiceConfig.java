package univpm.advprog.aule.app;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.context.annotation.FilterType;

import univpm.advprog.aule.test.DataServiceConfigTest;

import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
//@ComponentScan(basePackages =
//     "it.univpm.advprog.singers")
@ComponentScan(basePackages = { "univpm.advprog.aule.model" },
		excludeFilters  = {@ComponentScan.Filter(
				type = FilterType.ASSIGNABLE_TYPE, classes = {DataServiceConfigTest.class})})

@EnableTransactionManagement
public class DataServiceConfig {
	private static Logger logger = LoggerFactory.getLogger(DataServiceConfig.class);

	@Bean
	public DataSource dataSource() {
		try {

// Example configuration for H2 local (test) db		    	
//		    EmbeddedDatabaseBuilder dbBuilder =
//		      new EmbeddedDatabaseBuilder();
//		    return dbBuilder.setType(EmbeddedDatabaseType.H2)
//		        .addScripts("classpath:sql/schema.sql",
//		        "classpath:sql/test-data.sql").build();

			DriverManagerDataSource ds = new DriverManagerDataSource();
			//ds.setDriverClassName(com.mysql.jdbc.Driver.class.getName()); // deprecated
			ds.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
			ds.setUrl("jdbc:mysql://localhost:3306/auleDB?createDatabaseIfNotExist=true");
			ds.setUsername("root");
			ds.setPassword("root");
			return ds;

		} catch (Exception e) {
			logger.error("DataSource bean cannot be created!", e);
			return null;
		}
	}

	protected Properties hibernateProperties() {
		Properties hibernateProp = new Properties();
		// hibernateProp.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect"); //
		// configurazione per H2
		hibernateProp.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		hibernateProp.put("hibernate.format_sql", true);
		hibernateProp.put("hibernate.use_sql_comments", true);
		hibernateProp.put("hibernate.show_sql", true);
		hibernateProp.put("hibernate.max_fetch_depth", 3);
		hibernateProp.put("hibernate.jdbc.batch_size", 10);
		hibernateProp.put("hibernate.jdbc.fetch_size", 50);
//		    hibernateProp.put("hibernate.enable_lazy_load_no_trans", true);
		// hibernateProp.put("javax.persistence.schema-generation.database.action",
		// "create"); // importante, altrimenti si aspetta il DB gia` "strutturato"
		hibernateProp.put("javax.persistence.schema-generation.database.action", "none"); // importante, altrimenti si
																							// aspetta il DB gia`
																							// "strutturato"
		return hibernateProp;
	}

	@Bean
	public SessionFactory sessionFactory() throws IOException {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
//		    sessionFactoryBean.setPackagesToScan("it.univpm.advprog.singers");
		sessionFactoryBean.setPackagesToScan("it.univpm.advprog.singers.model");
		sessionFactoryBean.setHibernateProperties(hibernateProperties());
		sessionFactoryBean.afterPropertiesSet();
		return sessionFactoryBean.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws IOException {
		return new HibernateTransactionManager(sessionFactory());
	}
}
