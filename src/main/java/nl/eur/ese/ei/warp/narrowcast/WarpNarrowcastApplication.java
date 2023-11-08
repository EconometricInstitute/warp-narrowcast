package nl.eur.ese.ei.warp.narrowcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WarpNarrowcastApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarpNarrowcastApplication.class, args);
    }

}
