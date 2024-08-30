/**
 * 
 */
package com.moriset.bcephal.project.archive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Component
@Data
//@PropertySources({
//@PropertySource(value  = "classpath:postgres.properties"),
//@PropertySource(value  = "file:${user.dir}/postgres.properties", ignoreResourceNotFound = true),
//@PropertySource(value  = "${postgres.properties}", ignoreResourceNotFound = true)
//}
//)
@ConfigurationProperties(prefix = "postgres.cmd")
public class PostgresProperties {
	private String dump;
	private String createdb;
	private String psql;
	private String restore;
	private String[] restoreOpts;
	private String[] dumpOpts;

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final String DEFAULT_PORT = "5433";
}
