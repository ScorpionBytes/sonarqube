/*
 * SonarQube
 * Copyright (C) 2009-2022 SonarSource SA
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
package org.sonar.db.user;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang.math.RandomUtils.nextLong;

public class UserTokenTesting {

  private UserTokenTesting() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static UserTokenDto newUserToken() {
    return new UserTokenDto()
      .setUserUuid("userUuid_" + randomAlphanumeric(40))
      .setName("name_" + randomAlphanumeric(20))
      .setTokenHash("hash_" + randomAlphanumeric(30))
      .setCreatedAt(nextLong());
  }

  public static UserTokenDto newProjectAnalysisToken() {
    return new UserTokenDto()
      .setUserUuid("userUuid_" + randomAlphanumeric(40))
      .setName("name_" + randomAlphanumeric(20))
      .setTokenHash("hash_" + randomAlphanumeric(30))
      .setProjectKey("projectUuid_" + randomAlphanumeric(40))
      .setCreatedAt(nextLong());
  }

}
