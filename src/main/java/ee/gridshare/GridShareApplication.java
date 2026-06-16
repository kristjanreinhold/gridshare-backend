package ee.gridshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GridShareApplication {
    public static void main(String[] args) {
        SpringApplication.run(GridShareApplication.class, args);
    }
}
