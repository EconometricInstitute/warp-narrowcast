/*
 * warp_narrowcast - application to display important information related to seat reservations
 * Copyright (C) 2023-2024 Paul Bouman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
