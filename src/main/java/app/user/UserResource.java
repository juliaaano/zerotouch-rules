package app.user;

import static java.util.Arrays.asList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType.OAUTH2;
import java.util.Map;
import java.util.stream.Stream;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.OAuthScope;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.kie.kogito.incubation.application.AppRoot;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.rules.QueryId;
import org.kie.kogito.incubation.rules.RuleUnitIds;
import org.kie.kogito.incubation.rules.services.RuleUnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.pet.PetResource;

@Path("users")
@Consumes(TEXT_PLAIN)
@Produces(TEXT_PLAIN)
@Tag(name = "users", description = "operations about users")
@SecurityScheme(
    securitySchemeName = "oauth2",
    type = OAUTH2,
    description = "Authentication needed for this operation",
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "http://localhost:50102/realms/quarkus/protocol/openid-connect/auth",
            tokenUrl = "http://localhost:50102/realms/quarkus/protocol/openid-connect/token",
            scopes = {
                @OAuthScope(name = "api.users:read", description = "Allows to read users.")
            }
        )
    )
)
@RequestScoped
public class UserResource {

    private static final Logger log = LoggerFactory.getLogger(PetResource.class);

    private final QueryId queryId;

    private final RuleUnitService ruleUnit;

    private final UserRepository repository;

    private final JsonWebToken jwt;

    UserResource(
        final AppRoot appRoot,
        final RuleUnitService ruleUnit,
        final UserRepository repository,
        final JsonWebToken jwt) {

        this.queryId = 
            appRoot.get(RuleUnitIds.class)
                .get(ZeroTouchRules.class)
                .queries()
                .get("isAllowed");

        this.ruleUnit = ruleUnit;
        this.repository = repository;
        this.jwt = jwt;
    }

    @GET
    @Path("context")
    @Operation(hidden = true)
    public String context(@Context SecurityContext ctx) {

        log.info(jwt.toString());

        return String.format("hello + %s," + " isHttps: %s," + " authScheme: %s",
                ctx.getUserPrincipal().getName(), ctx.isSecure(), ctx.getAuthenticationScheme());
    }

    @GET
    @Path("{identifier}")
    @RolesAllowed("USERS_READ")
    @Produces(APPLICATION_JSON)
    @Operation(
        summary = "checks user authorized",
        description = "This operation checks if a user is authorized."
    )
    @APIResponse(
        responseCode = "200"
        // description = "The user.",
        // content = {
        //     @Content(schema = @Schema(implementation = User.class))
        // }
    )
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized."
    )
    @APIResponse(
        responseCode = "403",
        description = "Forbidden."
    )
    @APIResponse(
        responseCode = "404",
        description = "User not found."
    )
    public Response get(@PathParam("identifier") final String userId) {

        final boolean isUserAllowed = repository.find(userId)
            .map(user -> fireRules(user))
            .orElseThrow(NotFoundException::new)
            .anyMatch(user -> user.isAllowed());

        return response(isUserAllowed);
    }

    private Stream<User> fireRules(final User user) {

        final MapDataContext data = MapDataContext.of(Map.of("users", asList(user)));

        return ruleUnit.evaluate(queryId, data)
                .map(dc -> dc.as(MapDataContext.class)
                .get("$usr", User.class));
    }

    private Response response(final boolean isUserAllowed) {

        var response = isUserAllowed ? Response.ok() : Response.status(UNAUTHORIZED);
        return response.build();
    }
}
