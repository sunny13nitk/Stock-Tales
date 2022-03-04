package stocktales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication()
@PropertySources(
    { @PropertySource("classpath:weights.properties"), @PropertySource(
        "classpath:application.properties"
    ), @PropertySource("classpath:nfs.properties") }
)
@EnableAspectJAutoProxy
@EnableAsync

public class MainApplication
{
	
	public static void main(
	        String[] args
	)
	{
		SpringApplication.run(MainApplication.class, args);
		
	}
	
}
