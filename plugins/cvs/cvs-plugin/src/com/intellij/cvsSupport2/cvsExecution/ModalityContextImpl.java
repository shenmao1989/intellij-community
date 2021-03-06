/*
 * Copyright 2000-2011 JetBrains s.r.o.
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
package com.intellij.cvsSupport2.cvsExecution;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.WaitForProgressToShow;
import org.jetbrains.annotations.NotNull;

/**
 * author: lesya
 */
public class ModalityContextImpl implements ModalityContext {
  private final ModalityState myDefaultModalityState;
  public static final ModalityContext NON_MODAL = new ModalityContextImpl(ModalityState.NON_MODAL);

  public ModalityContextImpl(ModalityState defaultModalityState) {
    myDefaultModalityState = defaultModalityState;
  }

  @Override
  public void runInDispatchThread(@NotNull Runnable action, Project project) {
    Application application = ApplicationManager.getApplication();
    if (application.isUnitTestMode() || application.isDispatchThread()) {
      action.run();
    }
    else {
      WaitForProgressToShow.runOrInvokeAndWaitAboveProgress(action, getCurrentModalityState());
    }
  }

  private ModalityState getCurrentModalityState() {
    ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
    ModalityState modalityState = progressIndicator == null
                                  ? myDefaultModalityState
                                  : progressIndicator.getModalityState();
    if (modalityState == null) modalityState = ModalityState.defaultModalityState();
    return modalityState;
  }
}
