package de.kodestruktor.minutes.service;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kodestruktor.minutes.model.Appointment;
import de.kodestruktor.minutes.model.User;
import de.kodestruktor.minutes.model.Workday;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLSubscription;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;


/**
 * @author Christoph Wende
 */
@Service
public class UserServiceImpl implements UserService{

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

  private Set<User> userSpace = new HashSet<>(0);

  @Autowired
  private CalendarService calendarService;

  @GraphQLQuery(name = "listUsers")
  @Override
  public Set<User> listUsers() {
    return this.userSpace;
  }

  @GraphQLMutation(name="addUser")
  public User addUser(@GraphQLArgument(name="email", description = "The email address of the user to add") final String email){
    if(StringUtils.isNotBlank(email)) {
      User user = new User(email);
      this.userSpace.add(user);
      return user;
    }
    return null;
  }

  @GraphQLMutation(name="removeUser")
  public User removeUser(@GraphQLArgument(name="email", description = "The email address of the user to remove")final String email) {
    if(StringUtils.isNotBlank(email)) {
      this.userSpace.remove(new User(email));
      return new User(email);
    }
    return null;
  }

  public void bla() {}
}
