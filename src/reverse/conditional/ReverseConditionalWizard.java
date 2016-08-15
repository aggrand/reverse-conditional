package reverse.conditional;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ReverseConditionalWizard extends RefactoringWizard {


	public ReverseConditionalWizard(ReverseConditionalRefactoring refactoring) {
        super(refactoring, WIZARD_BASED_USER_INTERFACE);
    }

    @Override
    protected void addUserInputPages() {
        // No additional input pages required
    }

}