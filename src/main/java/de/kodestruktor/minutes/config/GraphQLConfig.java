package de.kodestruktor.minutes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.xml.internal.ws.developer.Serialization;
import de.kodestruktor.minutes.service.CalendarService;
import de.kodestruktor.minutes.service.UserService;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.PublicResolverBuilder;
import io.leangen.graphql.metadata.strategy.type.DefaultTypeInfoGenerator;
import io.leangen.graphql.metadata.strategy.type.TypeInfoGenerator;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;

/**
 * @author Christoph Wende
 */
@Configuration
public class GraphQLConfig {

  @Autowired
  private UserService userService;

  @Autowired
  private CalendarService calendarService;

  @Bean
  public GraphQL graphQL() {
    return GraphQL.newGraphQL(this.graphQLSchema()).build();
  }

  @Bean
  public GraphQLSchema graphQLSchema() {
    ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);


    return new GraphQLSchemaGenerator()
            .withResolverBuilders(
                    //Resolve by annotations
                    new AnnotatedResolverBuilder(),
                    //Resolve public methods inside root package
                    new PublicResolverBuilder("de.kodestruktor.minutes"))
            .withOperationsFromSingleton(this.userService)
            .withOperationsFromSingleton(this.calendarService)
            .withValueMapperFactory(JacksonValueMapperFactory.builder().withPrototype(mapper).build())


            .generate();

   }
}
