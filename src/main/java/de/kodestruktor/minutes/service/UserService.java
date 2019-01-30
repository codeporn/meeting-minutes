package de.kodestruktor.minutes.service;

import java.util.Set;

import de.kodestruktor.minutes.model.User;

/**
 * @author Christoph Wende
 */
public interface UserService {


  public Set<User> listUsers();

  public User addUser(final String email);

  public User removeUser(final String email);
}
