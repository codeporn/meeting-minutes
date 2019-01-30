package de.kodestruktor.minutes.model;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import microsoft.exchange.webservices.data.core.enumeration.property.LegacyFreeBusyStatus;

/**
 * @author Christoph Wende
 */
@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class Workday {

  @NonNull
  @GraphQLIgnore
  private Date day;

  @GraphQLQuery(name = "appointments")
  private Set<Appointment> appointments = new HashSet<>(0);

  @GraphQLQuery(name="busyTimeInMinutes")
  public Long getBusyTimeInMinutes() {
    long busyTimeInMinutes = 0L;
    for(Appointment appointment : this.appointments) {
      if(LegacyFreeBusyStatus.OOF.equals(appointment.getFreeBusyStatus()) || LegacyFreeBusyStatus.Busy.equals(appointment.getFreeBusyStatus()))
      busyTimeInMinutes += appointment.getDurationMinutes();
    }
    return busyTimeInMinutes;
  }

  @GraphQLQuery(name="busyTime")
  public String getBusyTime() {
    Duration duration = new Duration(0);
    for(Appointment appointment : this.appointments) {
      if(LegacyFreeBusyStatus.OOF.equals(appointment.getFreeBusyStatus()) || LegacyFreeBusyStatus.Busy.equals(appointment.getFreeBusyStatus()))
        duration = duration.plus(appointment.getDuration());
    }
    return DurationFormatUtils.formatDuration(duration.getMillis(), "HH:mm", true);
  }
}
