package de.kodestruktor.minutes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring Boot entry point
 *
 * @author Christoph Wende
 */
@SpringBootApplication
public class Application {

  /**
   * Starting the application
   *
   * @param args The commandline aguments
   */
  public static void main(final String[] args) {
    final SpringApplication application = new SpringApplication(Application.class);


    application.run(args);
  }


  @Bean
  public WebMvcConfigurerAdapter forwardToIndex() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName(
                "forward:/graphiql/index.html");
      }
    };
  }

}

