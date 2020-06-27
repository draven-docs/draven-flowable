//package com.noxus.draven.flow.config;
//
//import liquibase.Liquibase;
//import liquibase.database.Database;
//import liquibase.database.DatabaseConnection;
//import liquibase.database.DatabaseFactory;
//import liquibase.database.jvm.JdbcConnection;
//import liquibase.exception.DatabaseException;
//import liquibase.resource.ClassLoaderResourceAccessor;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.flowable.common.engine.api.FlowableException;
//import org.flowable.spring.SpringProcessEngineConfiguration;
//import org.flowable.ui.common.service.exception.InternalServerErrorException;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.core.io.support.ResourcePatternUtils;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.SQLException;
//import java.util.Properties;
//
///**
// * 待整合
// * @Description TODO 构造流程引擎配置类
// * @Author draven
// * @Date 2019/11/17 20:12
// */
//@Configuration
//public class GblfyProcessEngine {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(GblfyProcessEngine.class);
//
//    //TODO 解决创建流程时报act_re_model找不到
//    protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    protected ResourceLoader resourceLoader;
//
//    //事务管理器
//    @Bean
//    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource){
//        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
//        return dataSourceTransactionManager;
//    }
//
//    @Bean
//    public SpringProcessEngineConfiguration springProcessEngineConfiguration(){
//        SpringProcessEngineConfiguration springProcessEngineConfiguration =
//                new SpringProcessEngineConfiguration();
//        springProcessEngineConfiguration.setDataSource(dataSource);
//        springProcessEngineConfiguration.setDatabaseSchemaUpdate("true");
//        springProcessEngineConfiguration.setTransactionManager(dataSourceTransactionManager(dataSource));
//        return springProcessEngineConfiguration;
//    }
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        String databaseType = initDatabaseType(dataSource);
//        if (databaseType == null) {
//            throw new FlowableException("couldn't deduct database type");
//        }
//
//        try {
//            Properties properties = new Properties();
//            properties.put("prefix", "");
//            properties.put("blobType", "BLOB");
//            properties.put("boolValue", "TRUE");
//
//            //properties.load(this.getClass().getClassLoader().getResourceAsStream("properties/" + databaseType + ".properties"));
//
//            sqlSessionFactoryBean.setConfigurationProperties(properties);
//            sqlSessionFactoryBean
//                    .setMapperLocations(ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("classpath:/META-INF/modeler-mybatis-mappings/*.xml"));
//            sqlSessionFactoryBean.afterPropertiesSet();
//            return sqlSessionFactoryBean.getObject();
//        } catch (Exception e) {
//            throw new FlowableException("Could not create sqlSessionFactory", e);
//        }
//
//    }
//
//    protected String initDatabaseType(DataSource dataSource) {
//        String databaseType = null;
//        Connection connection = null;
//        try {
//            connection = dataSource.getConnection();
//            DatabaseMetaData databaseMetaData = connection.getMetaData();
//            String databaseProductName = databaseMetaData.getDatabaseProductName();
//            LOGGER.info("database product name: '{}'", databaseProductName);
//            databaseType = databaseTypeMappings.getProperty(databaseProductName);
//            if (databaseType == null) {
//                throw new FlowableException("couldn't deduct database type from database product name '" + databaseProductName + "'");
//            }
//            LOGGER.info("using database type: {}", databaseType);
//
//        } catch (SQLException e) {
//            LOGGER.error("Exception while initializing Database connection", e);
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                LOGGER.error("Exception while closing the Database connection", e);
//            }
//        }
//
//        return databaseType;
//    }
//
//    protected static Properties databaseTypeMappings = getDefaultDatabaseTypeMappings();
//
//    public static final String DATABASE_TYPE_MYSQL = "mysql";
//    public static final String DATABASE_TYPE_ORACLE = "oracle";
//
//    public static Properties getDefaultDatabaseTypeMappings() {
//        Properties databaseTypeMappings = new Properties();
//        databaseTypeMappings.setProperty("MySQL", DATABASE_TYPE_MYSQL);
//        databaseTypeMappings.setProperty("Oracle", DATABASE_TYPE_ORACLE);
//        return databaseTypeMappings;
//    }
//
//    @Bean
//    public Liquibase liquibase(DataSource dataSource) {
//        LOGGER.info("Configuring Liquibase");
//
//        Liquibase liquibase = null;
//        try {
//            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
//            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
//            //TODO 解决创建流程时报act_re_model找不到
//            database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
//            database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());
//
//            liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
//            liquibase.update("flowable");
//            return liquibase;
//
//        } catch (Exception e) {
//            throw new InternalServerErrorException("Error creating liquibase database", e);
//        } finally {
//            closeDatabase(liquibase);
//        }
//    }
//
//
//    private void closeDatabase(Liquibase liquibase) {
//        if (liquibase != null) {
//            Database database = liquibase.getDatabase();
//            if (database != null) {
//                try {
//                    database.close();
//                } catch (DatabaseException e) {
//                    LOGGER.warn("Error closing database", e);
//                }
//            }
//        }
//    }
//}
//
