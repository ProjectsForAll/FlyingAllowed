package host.plas.flyingallowed.compat;

import lombok.Getter;
import lombok.Setter;
import tv.quaint.objects.Identifiable;

import java.util.Optional;
import java.util.function.Function;

@Getter @Setter
public abstract class ApiHolder<A> implements Identifiable {
    public enum ApiTristate {
        TRUE,
        FALSE,
        NULL,
        ;
    }

    private String identifier;

    private Optional<A> api;
    private Function<Void, A> getter;

    public ApiHolder(String identifier, Function<Void, A> getter) {
        this.identifier = identifier;
        this.getter = getter;

        tryEnable();
    }

    public A checkApi() {
        try {
            return getter.apply(null);
        } catch (Throwable t) {
            return null;
        }
    }

    public void tryEnable() {
        A api = checkApi();
        if (api != null) {
            this.api = Optional.of(api);
        } else {
            this.api = Optional.empty();
        }
    }

    public boolean isEnabled() {
        return api.isPresent();
    }

    public A api() {
        return api.orElse(null);
    }
}
