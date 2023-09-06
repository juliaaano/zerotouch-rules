package app.user.db;

import static app.user.db.UserEntity.userEntity;
import static java.util.stream.Collectors.toList;
import static org.eclipse.microprofile.metrics.MetricUnits.NONE;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import app.user.User;
import app.user.UserRepository;

@ApplicationScoped
class DatabaseUserRepository implements UserRepository {

    private int totalNumberOfUsers = 0;

    @Gauge(name = "totalNumberOfUsers", description = "Total number of users retrieved last query.", unit = NONE)
    public int totalNumberOfUsers() {
        return totalNumberOfUsers;
    }

    @Override
    public Optional<User> find(final String identifier) {
        final UserEntity entity = UserEntity.findById(identifier);
        return entity != null ? Optional.of(entity.map()) : Optional.empty();
    }

    @Override
    public List<User> find() {
        final Stream<UserEntity> stream = UserEntity.streamAll();
        final List<User> users = stream.map(UserEntity::map).collect(toList());
        totalNumberOfUsers = users.size();
        return users;
    }

    @Override
    @Transactional
    public String create(final User user) {
        final UserEntity entity = userEntity(user);
        entity.persist();
        return entity.identifier;
    }

    @Override
    @Transactional
    public User update(final User user) {
        final UserEntity entity = UserEntity.findById(user.getIdentifier());
        entity.overwrite(user);
        entity.persist();
        return entity.map();
    }

    @Override
    @Transactional
    public boolean replace(final User user) {
        final UserEntity entity = userEntity(user);
        final boolean deleted = UserEntity.deleteById(user.getIdentifier());
        entity.persist();
        return deleted;
    }

    @Override
    @Transactional
    public void delete(final String identifier) {
        UserEntity.deleteById(identifier);
    }
}
