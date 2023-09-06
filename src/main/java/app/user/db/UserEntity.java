package app.user.db;

import javax.persistence.Id;
import javax.persistence.Table;
import app.Entity;
import app.user.User;

@javax.persistence.Entity
@Table(name = "user_rhdp")
class UserEntity extends Entity {

    @Id
    public String identifier;

    public String name;

    public int running;

    static UserEntity userEntity(final User user) {

        final var entity = new UserEntity();

        entity.identifier = user.getIdentifier();
        entity.name = user.getName();
        entity.running = user.getRunning();

        return entity;
    }

    User map() {

        return User.user(this.identifier, this.name, this.running);
    }

    void overwrite(final User user) {

        this.name = user.getName();
        this.running = user.getRunning();
    }
}
