package org.key_project.sed.key.evaluation.model.definition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.key_project.util.java.CollectionUtil;

public class Choice {
   private final String text;
   
   private final String value;
   
   private final String tooltip;
   
   private final List<AbstractQuestion> childQuestions;

   public Choice(String text, String value, AbstractQuestion... childQuestions) {
      this(text, value, null, childQuestions);
   }

   public Choice(String text, String value, String tooltip, AbstractQuestion... childQuestions) {
      this(text, value, tooltip, CollectionUtil.toList(childQuestions));
   }

   public Choice(String text, String value, String tooltip, List<AbstractQuestion> childQuestions) {
      this.text = text;
      this.value = value;
      this.tooltip = tooltip;
      this.childQuestions = childQuestions;
      validateChildren();
   }
   
   protected void validateChildren() {
      // Ensure that all children have different names
      Set<String> usedNames = new HashSet<String>();
      if (childQuestions != null) {
         for (AbstractQuestion childQuestion : childQuestions) {
            if (!usedNames.add(childQuestion.getName())) {
               throw new IllegalStateException("Chlild question name '" + childQuestion.getName() + "' used multiple times.");
            }
         }
      }
   }

   public String getText() {
      return text;
   }

   public String getValue() {
      return value;
   }

   public String getTooltip() {
      return tooltip;
   }

   public AbstractQuestion[] getChildQuestions() {
      return childQuestions.toArray(new AbstractQuestion[childQuestions.size()]);
   }
   
   public int countChildQuestions() {
      return childQuestions.size();
   }
}