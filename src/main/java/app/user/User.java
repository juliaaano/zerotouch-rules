package app.user;

import static java.util.UUID.randomUUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User details.")
public class User {

    @Schema(example = "73df7903-33b0-4203-b87e-26034e106542", readOnly = true)
    private String identifier;

    @Size(min = 2, max = 40)
    @Schema(example = "Felix", required = true)
    private String name;

    @NotBlank
    private int running;

    private boolean allowed;

    private User(final String identifier) {
        this.identifier = identifier;
    }

    private User() {
        this(randomUUID().toString());
    }

    public static User user(final String identifier, final String name, final int running) {

        final User user = new User(identifier);

        user.name = name;
        user.running = running;

        return user;
    }

    public static User user(final String name, final int running) {
        return user(randomUUID().toString(), name, running);
    }

    User clone(final String identifier) {

        final User cloned = new User(identifier);

        cloned.name = this.name;
        cloned.running = this.running;
        cloned.allowed = this.allowed;

        return cloned;
    }

    User merge(final User user) {

        final User merged = new User(this.identifier);

        merged.name = user.name != null ? user.name : this.name;
        merged.running = user.running != 0 ? user.running : this.running;
        merged.allowed = user.allowed != false ? user.allowed : this.allowed;

        return merged;
    }

    public void allow() {

        this.allowed = true;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public int getRunning() {
        return running;
    }

    public boolean isAllowed() {
        return allowed;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final User other = (User) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        return true;
    }
}
