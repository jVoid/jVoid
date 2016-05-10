package io.jvoid.configuration;


/**
 * The {@code JVoidConfiguration} contains the parameters that can be
 * used to tune JVoid for specific needs and custom executions.
 *
 */
public interface JVoidConfiguration {

    String dbUrl();

    String dbUsername();

    String dbPassword();

    String basePackage();

    String excludes();

    String includes();

    Boolean heuristicExcludeCglib();

    Boolean heuristicExcludeJacoco();

    Boolean heuristicExcludeGroovyCallSite();

}
