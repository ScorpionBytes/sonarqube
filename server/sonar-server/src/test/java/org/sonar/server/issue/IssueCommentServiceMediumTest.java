/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.issue;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.api.issue.internal.DefaultIssueComment;
import org.sonar.api.security.DefaultGroups;
import org.sonar.api.web.UserRole;
import org.sonar.core.component.ComponentDto;
import org.sonar.core.issue.db.IssueDto;
import org.sonar.core.permission.PermissionFacade;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.rule.RuleDto;
import org.sonar.server.component.ComponentTesting;
import org.sonar.server.component.SnapshotTesting;
import org.sonar.server.component.db.ComponentDao;
import org.sonar.server.component.db.SnapshotDao;
import org.sonar.server.db.DbClient;
import org.sonar.server.issue.db.IssueDao;
import org.sonar.server.rule.RuleTesting;
import org.sonar.server.rule.db.RuleDao;
import org.sonar.server.search.IndexClient;
import org.sonar.server.tester.ServerTester;
import org.sonar.server.user.MockUserSession;

import java.util.Date;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class IssueCommentServiceMediumTest {

  @ClassRule
  public static ServerTester tester = new ServerTester();

  DbClient db;
  IndexClient indexClient;
  DbSession session;
  IssueCommentService service;

  RuleDto rule;
  ComponentDto project;
  ComponentDto file;

  @Before
  public void setUp() throws Exception {
    tester.clearDbAndIndexes();
    db = tester.get(DbClient.class);
    indexClient = tester.get(IndexClient.class);
    session = db.openSession(false);
    service = tester.get(IssueCommentService.class);

    rule = RuleTesting.newXooX1();
    tester.get(RuleDao.class).insert(session, rule);

    project = ComponentTesting.newProjectDto();
    tester.get(ComponentDao.class).insert(session, project);
    tester.get(SnapshotDao.class).insert(session, SnapshotTesting.createForProject(project));

    file = ComponentTesting.newFileDto(project);
    tester.get(ComponentDao.class).insert(session, file);
    tester.get(SnapshotDao.class).insert(session, SnapshotTesting.createForComponent(file, project));

    // project can be seen by anyone
    tester.get(PermissionFacade.class).insertGroupPermission(project.getId(), DefaultGroups.ANYONE, UserRole.USER, session);
    db.issueAuthorizationDao().synchronizeAfter(session, new Date(0));

    MockUserSession.set().setLogin("gandalf");

    session.commit();
  }

  @After
  public void after() {
    session.close();
  }

  @Test
  public void add_comment() throws Exception {
    IssueDto issue = IssueTesting.newDto(rule, file, project);
    tester.get(IssueDao.class).insert(session, issue);
    session.commit();

    service.addComment(issue.getKey(), "my comment", MockUserSession.get());

    List<DefaultIssueComment> comments = service.findComments(issue.getKey());
    assertThat(comments).hasSize(1);
    assertThat(comments.get(0).markdownText()).isEqualTo("my comment");
  }

}
