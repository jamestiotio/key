package org.key_project.sed.key.evaluation.wizard.page;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.key_project.sed.key.evaluation.model.input.InstructionPageInput;
import org.key_project.sed.key.evaluation.wizard.manager.BrowserManager;

public class InstructionWizardPage extends AbstractEvaluationWizardPage<InstructionPageInput> {
   public InstructionWizardPage(InstructionPageInput pageInput) {
      super(pageInput);
   }

   @Override
   protected void createContent(FormToolkit toolkit, ScrolledForm form) {
      form.getBody().setLayout(new GridLayout(1, false));
      BrowserManager.createBrowser(toolkit, form.getBody(), getPageInput().getPage().getDescriptionURL());
   }

   @Override
   protected void updatePageCompleted() {
      String errorMessage = getRunnablesFailure();
      setErrornousControl(null);
      setPageComplete(errorMessage == null);
      setErrorMessage(errorMessage);
   }
}