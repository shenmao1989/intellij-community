/*
 * Copyright 2000-2012 JetBrains s.r.o.
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
package com.intellij.facet.impl.ui.libraries;

import com.intellij.facet.ui.FacetConfigurationQuickFix;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.ValidationResult;
import com.intellij.facet.ui.libraries.FrameworkLibraryValidator;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.ui.configuration.libraries.AddCustomLibraryDialog;
import com.intellij.openapi.roots.ui.configuration.libraries.CustomLibraryDescription;
import com.intellij.openapi.roots.ui.configuration.libraries.LibraryPresentationManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Processor;

import javax.swing.*;
import java.util.Set;

/**
 * @author nik
 */
public class FrameworkLibraryValidatorImpl extends FrameworkLibraryValidator {
  private CustomLibraryDescription myLibraryDescription;
  private final LibrariesValidatorContext myContext;
  private final FacetValidatorsManager myValidatorsManager;
  private final String myLibraryCategoryName;

  public FrameworkLibraryValidatorImpl(CustomLibraryDescription libraryDescription,
                                       LibrariesValidatorContext context,
                                       FacetValidatorsManager validatorsManager,
                                       String libraryCategoryName) {
    myLibraryDescription = libraryDescription;
    myContext = context;
    myValidatorsManager = validatorsManager;
    myLibraryCategoryName = libraryCategoryName;
  }

  @Override
  public ValidationResult check() {
    final Set<? extends LibraryKind> libraryKinds = myLibraryDescription.getSuitableLibraryKinds();
    final Ref<Boolean> found = Ref.create(false);
    myContext.getRootModel().orderEntries().using(myContext.getModulesProvider()).recursively().librariesOnly().forEachLibrary(new Processor<Library>() {
      @Override
      public boolean process(Library library) {
        if (LibraryPresentationManager.getInstance().isLibraryOfKind(library, myContext.getLibrariesContainer(), libraryKinds)) {
          found.set(true);
          return false;
        }
        return true;
      }
    });
    if (found.get()) return ValidationResult.OK;

    return new ValidationResult(StringUtil.capitalize(myLibraryCategoryName) + " library not found in the module dependencies list", new LibrariesQuickFix(myLibraryDescription));
  }

  private class LibrariesQuickFix extends FacetConfigurationQuickFix {
    private CustomLibraryDescription myDescription;

    public LibrariesQuickFix(CustomLibraryDescription description) {
      super(IdeBundle.message("missing.libraries.fix.button"));
      myDescription = description;
    }

    public void run(final JComponent place) {
      AddCustomLibraryDialog dialog = AddCustomLibraryDialog.createDialog(myDescription, myContext.getLibrariesContainer(),
                                                                          myContext.getModule(), myContext.getModifiableRootModel(), null);
      dialog.show();
      myValidatorsManager.validate();
    }
  }
}
