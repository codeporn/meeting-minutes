package de.kodestruktor.minutes.service;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Set;

import de.kodestruktor.minutes.model.User;
import io.leangen.graphql.annotations.GraphQLArgument;

/**
 * @author Christoph Wende
 */
public interface CalendarService {

  public Set<User> fetchCalendars( final Date start, final Date end);
  public Set<User> fillCalendars(final Set<User> users, final DateTime start, final DateTime end);
}
