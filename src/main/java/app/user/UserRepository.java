package app.user;

import java.util.Optional;

public interface UserRepository {

    public Optional<User> find(String identifier);

    public Iterable<User> find();

    public String create(User user);

    public User update(User user);

    public boolean replace(User user);

    public void delete(String identifier);
}
