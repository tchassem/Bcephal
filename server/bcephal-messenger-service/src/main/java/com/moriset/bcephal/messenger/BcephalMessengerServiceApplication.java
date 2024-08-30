package com.moriset.bcephal.messenger;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.hawt.config.ConfigFacade;
import io.hawt.springboot.HawtioPlugin;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(scanBasePackages = {"com.moriset.bcephal.*"  })
@EnableBatchProcessing
@EnableScheduling
@EnableDiscoveryClient
@Slf4j
@PropertySources({
	@PropertySource("classpath:login.config"),
	@PropertySource(value = "file:${user.dir}login.config",ignoreResourceNotFound = true),
	@PropertySource(value = "file:${login.config}",ignoreResourceNotFound = true),
})
public class BcephalMessengerServiceApplication {
	
	private static ConfigurableApplicationContext context;
	
	public static void main(String[] args) {
		context = SpringApplication.run(BcephalMessengerServiceApplication.class, args);
		log.trace("Local dir: {}", System.getProperty("user.dir"));
		log.info("Messenger service started!");
	}
	
	public static void restart() {
		log.info("Try to restart application...");
        Thread thread = new Thread(() -> {
        	if(context != null) {
        		context.close();
        	}
            context = SpringApplication.run(BcephalMessengerServiceApplication.class, new String[] {});
        });

        thread.setDaemon(false);
        thread.start();
    }
	
	public static void stop(int status) {
		log.info("Try to stop application...");
		if(context != null) {
			SpringApplication.exit(context, new ExitCodeGenerator() {				
				@Override
				public int getExitCode() {
				        return status;
				    }
				});
		}
        System.exit(status);
    }
	


	@Bean
	HawtioPlugin samplePlugin() {
		return new HawtioPlugin("sample-plugin", "plugins", "", new String[] { "sample-plugin/sample-plugin.js" });
	}

	@Bean
	ConfigFacade configFacade() {
		return ConfigFacade.getSingleton();
	}
}
