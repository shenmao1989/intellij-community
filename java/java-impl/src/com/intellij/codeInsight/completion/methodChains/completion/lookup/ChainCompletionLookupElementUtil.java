package com.intellij.codeInsight.completion.methodChains.completion.lookup;

import com.intellij.codeInsight.completion.methodChains.completion.lookup.sub.SubLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import gnu.trove.TIntObjectHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dmitry Batkovich
 */
public final class ChainCompletionLookupElementUtil {
  private ChainCompletionLookupElementUtil() {
  }

  public static LookupElement createLookupElement(final PsiMethod method,
                                                  final @Nullable TIntObjectHashMap<SubLookupElement> replaceElements) {
    if (method.isConstructor()) {
      //noinspection ConstantConditions
      return LookupElementBuilder.create(String.format("%s %s", PsiKeyword.NEW, method.getContainingClass().getName()));
    } else if (method.hasModifierProperty(PsiModifier.STATIC)) {
      return new ChainCompletionMethodCallLookupElement(method, replaceElements, false, true);
    } else {
      return new ChainCompletionMethodCallLookupElement(method, replaceElements);
    }
  }

  public static String fillMethodParameters(final PsiMethod method, @Nullable final TIntObjectHashMap<SubLookupElement> replaceElements) {
    final TIntObjectHashMap<SubLookupElement> notNullReplaceElements = replaceElements == null ?
        new TIntObjectHashMap<SubLookupElement>(0) :
        replaceElements;

    final PsiParameter[] parameters = method.getParameterList().getParameters();
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parameters.length; i++) {
      if (i != 0) {
        sb.append(", ");
      }
      final PsiParameter parameter = parameters[i];
      final SubLookupElement replaceElement = notNullReplaceElements.get(i);
      if (replaceElement != null) {
        sb.append(replaceElement.getInsertString());
      } else {
        sb.append(parameter.getName());
      }
    }
    return sb.toString();
  }
}
