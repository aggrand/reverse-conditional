package reverse.conditional;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import reverse.conditional.java.ReverseConditionalJavaRefactoring;

public class ReverseConditionalRefactoringContribution extends RefactoringContribution {

	public static class ReverseConditionalDescriptor extends RefactoringDescriptor {

		public static final String REFACTORING_ID = "refactorings.reverse.conditional";
		
		private final Map fArguments;
		
		public ReverseConditionalDescriptor(String project, String description, String comment, Map arguments) {
            super(REFACTORING_ID, project, description, comment, RefactoringDescriptor.STRUCTURAL_CHANGE
                    | RefactoringDescriptor.MULTI_CHANGE);
            this.fArguments = arguments;
		}
		
		@Override
		public Refactoring createRefactoring(RefactoringStatus status) throws CoreException {
			ReverseConditionalJavaRefactoring refactoring = new ReverseConditionalJavaRefactoring();
			return refactoring;
		}
	}
	
	@Override
	public RefactoringDescriptor createDescriptor(String id, String project, String description, String comment,
			Map<String, String> arguments, int flags) throws IllegalArgumentException {
		return new ReverseConditionalDescriptor(project, description, comment, arguments);
	}
	
}