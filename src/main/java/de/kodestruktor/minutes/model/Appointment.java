package de.kodestruktor.minutes.model;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Date;

import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import microsoft.exchange.webservices.data.core.enumeration.availability.FreeBusyViewType;
import microsoft.exchange.webservices.data.core.enumeration.property.LegacyFreeBusyStatus;

/**
 * @author Christoph Wende
 */
@Data
@EqualsAndHashCode
public class Appointment {

  @GraphQLQuery(name="start")
  private Date start;
  @GraphQLQuery(name="end")
  private Date end;


  @GraphQLQuery(name="detail")
  private String detail;
  @GraphQLQuery(name="freeBusyStatus")
  private LegacyFreeBusyStatus freeBusyStatus;

  @GraphQLIgnore
  public Duration getDuration() {
    return new Duration(new DateTime(this.start), new DateTime(this.end));
  }

  @GraphQLQuery(name="durationInMinutes")
  public Long getDurationMinutes() {
    return Long.valueOf(this.getDuration().getStandardMinutes());
  }
}
