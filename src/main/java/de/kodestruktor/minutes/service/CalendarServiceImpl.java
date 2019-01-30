package de.kodestruktor.minutes.service;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.kodestruktor.minutes.model.Appointment;
import de.kodestruktor.minutes.model.User;
import de.kodestruktor.minutes.model.Workday;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLSubscription;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.availability.AvailabilityData;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.response.AttendeeAvailability;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.availability.AttendeeInfo;
import microsoft.exchange.webservices.data.misc.availability.AvailabilityOptions;
import microsoft.exchange.webservices.data.misc.availability.GetUserAvailabilityResults;
import microsoft.exchange.webservices.data.misc.availability.TimeWindow;
import microsoft.exchange.webservices.data.property.complex.availability.CalendarEvent;
import microsoft.exchange.webservices.data.search.CalendarView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;


/**
 * @author Christoph Wende
 */
@Service
public class CalendarServiceImpl implements CalendarService {
  private static final Logger LOG = LoggerFactory.getLogger(CalendarServiceImpl.class);

  @Value("${exchange.url}")
  private String exchangeUrl;

  @Value("${exchange.user}")
  private String exchangeUser;

  @Value("${exchange.password}")
  private String exchangePassword;

  @Autowired
  private UserService userService;

  @GraphQLQuery(name="fetchCalendars")
  @Override
  public Set<User> fetchCalendars(@GraphQLArgument(name="start", description = "The start date to fetch the calendar entries for every user") final Date start, @GraphQLArgument(name="end", description = "The end date to fetch the calendar entries for every user")final Date end) {
    Set<User> users = this.userService.listUsers();
    return this.fillCalendars(users, new DateTime(start).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0), new DateTime(end).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59));
  }


  @GraphQLSubscription
  public Publisher<Integer> tick() {
    Observable<Integer> observable = Observable.create(emitter -> {
      emitter.onNext(1);
      Thread.sleep(1000);
      emitter.onNext(2);
      Thread.sleep(1000);
      emitter.onComplete();
    });

    return observable.toFlowable(BackpressureStrategy.BUFFER);
  }

  @GraphQLIgnore
  @Override
  public Set<User> fillCalendars(final Set<User> users, final DateTime start, final DateTime end) {


    ExchangeService exchangeService = this.createExchangeService(this.exchangeUser, this.exchangePassword, this.exchangeUrl);
    CalendarView view = new CalendarView(start.toDate(), end.toDate());

    for(User user : users) {
      try {
        LOG.info("Checking appointments for [{}] from [{}] to [{}]", user, start.toDate(), end.toDate());

        List<AttendeeInfo> attendees = new ArrayList<>();
        attendees.add(new AttendeeInfo(user.getEmail()));
        AvailabilityOptions availabilityOptions = new AvailabilityOptions();
        availabilityOptions.setDetailedSuggestionsWindow(new TimeWindow(start.plusDays(1).toDate(), end.plusDays(1).toDate()));
        GetUserAvailabilityResults results = exchangeService.getUserAvailability(attendees,
                availabilityOptions.getDetailedSuggestionsWindow(),
                AvailabilityData.FreeBusy,
                availabilityOptions);

        Iterator<AttendeeAvailability> iter = results.getAttendeesAvailability().iterator();
        Duration d = new Duration(0);
        while (iter.hasNext()) {
          AttendeeAvailability av = iter.next();

          Workday workday = null;
          for (CalendarEvent c : av.getCalendarEvents()) {
            Appointment appointment = new Appointment();
            appointment.setDetail(c.getDetails()!= null ? c.getDetails().getSubject(): "?");
            appointment.setFreeBusyStatus(c.getFreeBusyStatus());
            appointment.setStart(c.getStartTime());
            appointment.setEnd(c.getEndTime());

            if(workday == null || new DateTime(workday.getDay()).isBefore(c.getStartTime().getTime())) {
              if(workday != null) {
                user.getWorkdays().add(workday);
              }
              workday = new Workday(new DateTime(c.getStartTime()).withTime(23,59,59,999).toDate());
            }
            workday.getAppointments().add(appointment);
          }
          user.getWorkdays().add(workday);
        }
      } catch (final ServiceLocalException e) {
        LOG.warn("Fetching appointments caused web service exception: ", e);
      } catch (final Exception e) {
        LOG.warn("Fetching appointments failed: ", e);
      }
    }

    return users;
  }

  /**
   * Create a {@link ExchangeService} resource, configured with the passed credentials.
   *
   * @param username
   *          the Active Directory username
   * @param password
   *          the Active Directory password
   * @param uri
   *          the URI of the EWS endpoint
   * @return an already connected {@link ExchangeService}
   */
  @GraphQLIgnore
  private ExchangeService createExchangeService(final String username, final String password, final String uri) {

    LOG.info("Create Microsoft EWS service");

    ExchangeService exchangeService = null;

    try {
      exchangeService = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
      final ExchangeCredentials credentials = new WebCredentials(username, password);
      exchangeService.setCredentials(credentials);
      exchangeService.setUrl(new URI(uri));
    } catch (final URISyntaxException e) {
      LOG.warn("Unable to parse EWS URL [{}]", uri, e);
    }
    return exchangeService;

  }

}

