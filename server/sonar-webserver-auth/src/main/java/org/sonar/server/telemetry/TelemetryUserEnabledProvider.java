/*
 * SonarQube
 * Copyright (C) 2009-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.telemetry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserQuery;

public class TelemetryUserEnabledProvider implements TelemetryDataProvider<Boolean> {

  private final DbClient dbClient;

  public TelemetryUserEnabledProvider(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public String getMetricKey() {
    return "user_enabled";
  }

  @Override
  public Dimension getDimension() {
    return Dimension.USER;
  }

  @Override
  public Granularity getGranularity() {
    return Granularity.DAILY;
  }

  @Override
  public TelemetryDataType getType() {
    return TelemetryDataType.BOOLEAN;
  }

  @Override
  public Map<String, Boolean> getUuidValues() {
    Map<String, Boolean> result = new HashMap<>();
    int pageSize = 1000;
    int page = 1;
    try (DbSession dbSession = dbClient.openSession(false)) {
      List<UserDto> userDtos = null;
      do {
        userDtos = dbClient.userDao().selectUsers(dbSession, UserQuery.builder().build(), page, pageSize);
        for (UserDto userDto : userDtos) {
          result.put(userDto.getUuid(), userDto.isActive());
        }
        page++;
      } while (!userDtos.isEmpty());
    }
    return result;
  }
}
