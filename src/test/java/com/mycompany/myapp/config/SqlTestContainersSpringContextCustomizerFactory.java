package com.mycompany.myapp.config;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;
import tech.jhipster.config.JHipsterConstants;

public class SqlTestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

    private static SQLTCContextCutomeizer sqlTCCustemizer;

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        return getSQLTCContextCustemizer(testClass);
    }

    private synchronized ContextCustomizer getSQLTCContextCustemizer(Class<?> testClass) {
        if (sqlTCCustemizer == null) {
            sqlTCCustemizer = new SQLTCContextCutomeizer(testClass);
        }
        return sqlTCCustemizer;
    }
}

class SQLTCContextCutomeizer implements ContextCustomizer {

    private Logger log = LoggerFactory.getLogger(SQLTCContextCutomeizer.class);

    Class<?> testClass;
    private static SqlTestContainer prodTestContainer;

    SQLTCContextCutomeizer(Class<?> testClass) {
        this.testClass = testClass;
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        TestPropertyValues testValues = TestPropertyValues.empty();
        EmbeddedSQL sqlAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedSQL.class);
        boolean usingTestProdProfile = Arrays.asList(context.getEnvironment().getActiveProfiles()).contains(
            "test" + JHipsterConstants.SPRING_PROFILE_PRODUCTION
        );
        if (null != sqlAnnotation && usingTestProdProfile) {
            log.debug("detected the EmbeddedSQL annotation on class {}", testClass.getName());
            log.info("Warming up the sql database");
            if (null == prodTestContainer) {
                try {
                    Class<? extends SqlTestContainer> containerClass = (Class<? extends SqlTestContainer>) Class.forName(
                        this.getClass().getPackageName() + ".PostgreSqlTestContainer"
                    );
                    prodTestContainer = beanFactory.createBean(containerClass);
                    beanFactory.registerSingleton(containerClass.getName(), prodTestContainer);
                    // ((DefaultListableBeanFactory)beanFactory).registerDisposableBean(containerClass.getName(), prodTestContainer);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            testValues = testValues.and("spring.datasource.url=" + prodTestContainer.getTestContainer().getJdbcUrl() + "");
            testValues = testValues.and("spring.datasource.username=" + prodTestContainer.getTestContainer().getUsername());
            testValues = testValues.and("spring.datasource.password=" + prodTestContainer.getTestContainer().getPassword());
        }
        testValues.applyTo(context);
    }
}
