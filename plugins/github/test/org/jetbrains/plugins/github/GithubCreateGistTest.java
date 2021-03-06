/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.github;

import com.intellij.notification.NotificationType;

import java.util.Collections;
import java.util.List;

import static org.jetbrains.plugins.github.api.GithubGist.FileContent;

/**
 * @author Aleksey Pivovarov
 */
public class GithubCreateGistTest extends GithubCreateGistTestBase {
  public void testSimple() throws Throwable {
    List<FileContent> expected = createContent();

    String url = GithubCreateGistAction.createGist(myProject, myGitHubSettings.getAuthData(), expected, true, GIST_DESCRIPTION, null);
    assertNotNull(url);
    GIST_ID = url.substring(url.lastIndexOf('/') + 1);

    checkGistExists();
    checkGistNotAnonymous();
    checkGistPrivate();
    checkGistDescription(GIST_DESCRIPTION);
    checkGistContent(expected);
  }

  public void testAnonymous() throws Throwable {
    List<FileContent> expected = createContent();

    String url = GithubCreateGistAction.createGist(myProject, GithubAuthData.createAnonymous(myHost), expected, true, GIST_DESCRIPTION, null);
    assertNotNull(url);
    GIST_ID = url.substring(url.lastIndexOf('/') + 1);

    checkGistExists();
    checkGistAnonymous();
    checkGistPrivate();
    checkGistDescription(GIST_DESCRIPTION);
    checkGistContent(expected);

    // anonymous gists - undeletable
    GIST_ID = null;
    GIST = null;
  }

  public void testUnusedFilenameField() throws Throwable {
    List<FileContent> expected = createContent();

    String url = GithubCreateGistAction.createGist(myProject, myGitHubSettings.getAuthData(), expected, true, GIST_DESCRIPTION, "filename");
    assertNotNull(url);
    GIST_ID = url.substring(url.lastIndexOf('/') + 1);

    checkGistExists();
    checkGistNotAnonymous();
    checkGistPrivate();
    checkGistDescription(GIST_DESCRIPTION);
    checkGistContent(expected);
  }

  public void testUsedFilenameField() throws Throwable {
    List<FileContent> content = Collections.singletonList(new FileContent("file.txt", "file.txt content"));
    List<FileContent> expected = Collections.singletonList(new FileContent("filename", "file.txt content"));

    String url = GithubCreateGistAction.createGist(myProject, myGitHubSettings.getAuthData(), content, true, GIST_DESCRIPTION, "filename");
    assertNotNull(url);
    GIST_ID = url.substring(url.lastIndexOf('/') + 1);

    checkGistExists();
    checkGistNotAnonymous();
    checkGistPrivate();
    checkGistDescription(GIST_DESCRIPTION);
    checkGistContent(expected);
  }

  public void testPublic() throws Throwable {
    List<FileContent> expected = createContent();

    String url = GithubCreateGistAction.createGist(myProject, myGitHubSettings.getAuthData(), expected, false, GIST_DESCRIPTION, null);
    assertNotNull(url);
    GIST_ID = url.substring(url.lastIndexOf('/') + 1);

    checkGistExists();
    checkGistNotAnonymous();
    checkGistPublic();
    checkGistDescription(GIST_DESCRIPTION);
    checkGistContent(expected);
  }

  public void testEmpty() throws Throwable {
    List<FileContent> expected = Collections.emptyList();

    String url = GithubCreateGistAction.createGist(myProject, myGitHubSettings.getAuthData(), expected, true, GIST_DESCRIPTION, null);
    assertNull("Gist was created", url);

    checkNotification(NotificationType.WARNING, "Can't create Gist", "Can't create empty gist");
  }

  public void testWrongLogin() throws Throwable {
    List<FileContent> expected = createContent();

    GithubAuthData auth = myGitHubSettings.getAuthData();
    GithubAuthData myAuth = GithubAuthData.createBasicAuth(auth.getHost(), myLogin1 + "some_suffix", myPassword);
    String url = GithubCreateGistAction.createGist(myProject, myAuth, expected, true, GIST_DESCRIPTION, null);
    assertNull("Gist was created", url);

    checkNotification(NotificationType.ERROR, "Can't create Gist", null);
  }

  public void testWrongPassword() throws Throwable {
    List<FileContent> expected = createContent();

    GithubAuthData auth = myGitHubSettings.getAuthData();
    GithubAuthData myAuth = GithubAuthData.createBasicAuth(auth.getHost(), myLogin1, myPassword + "some_suffix");
    String url = GithubCreateGistAction.createGist(myProject, myAuth, expected, true, GIST_DESCRIPTION, null);
    assertNull("Gist was created", url);

    checkNotification(NotificationType.ERROR, "Can't create Gist", null);
  }


}
