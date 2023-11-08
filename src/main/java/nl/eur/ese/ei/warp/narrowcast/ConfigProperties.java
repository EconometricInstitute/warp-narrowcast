package nl.eur.ese.ei.warp.narrowcast;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@ConfigurationProperties(prefix="application.warp.narrowcast")
public class ConfigProperties {
    private final ZoneId databaseTimezone;
    private final ZoneId applicationTimezone;

    @ConstructorBinding
    public ConfigProperties(ZoneId databaseTimezone, ZoneId applicationTimezone) {
        this.databaseTimezone = databaseTimezone != null ? databaseTimezone : ZoneId.of("UTC");
        this.applicationTimezone = applicationTimezone != null ? applicationTimezone : ZoneId.systemDefault();
    }

    public ZoneId getDatabaseTimezone() {
        return databaseTimezone;
    }

    public ZoneId getApplicationTimezone() {
        return applicationTimezone;
    }


    public long getDatabaseNowTimestamp() {
        return OffsetDateTime.now(applicationTimezone)
                .atZoneSimilarLocal(databaseTimezone)
                .toEpochSecond();
    }

    public long getDatabaseTimestamp(OffsetDateTime time) {
        return time.atZoneSameInstant(databaseTimezone).toEpochSecond();
    }

}
