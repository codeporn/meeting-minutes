package de.kodestruktor.minutes.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Christoph Wende
 */
@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class User {
  @NonNull
  @GraphQLQuery(name = "email")
  private String email;
  @GraphQLQuery(name = "workdays")
  private Set<Workday> workdays = new HashSet<>(0);


}
