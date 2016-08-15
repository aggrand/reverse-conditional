package reverse.conditional.c;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import reverse.conditional.ReverseConditionalWizard;

public class ReverseConditionalCHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			RefactoringWizard wizard = new ReverseConditionalWizard(createRefactoring(selection, event));
			Shell parent = HandlerUtil.getActiveShell(event);
			String dialogTitle = "Reverse Conditional";
			
            RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(wizard);
            operation.run(parent, dialogTitle);
        } catch (InterruptedException | CoreException exception) {
        	JOptionPane.showMessageDialog(new JFrame(), "This refactoring cannot be performed.", "Dialog", 
        			JOptionPane.ERROR_MESSAGE);
        }
		return null;
	}

	private ReverseConditionalCRefactoring createRefactoring(ISelection selection, ExecutionEvent event) 
			throws CoreException {
		IEditorPart editorSite = HandlerUtil.getActiveEditor(event);
		IEditorInput editorInput = editorSite.getEditorInput();
		IResource resource = (IResource) editorInput.getAdapter(IResource.class);
		
		ITranslationUnit tu = (ITranslationUnit) CoreModel.getDefault().create(resource);
		IASTTranslationUnit AST =  tu.getAST();
		
		TextSelection ts = (TextSelection) selection;
		int offset = ts.getOffset();
		int length = ts.getLength();
		IASTNodeSelector finder = AST.getNodeSelector(null);
		IASTNode selected = finder.findEnclosingNode(offset, length);
		
		return new ReverseConditionalCRefactoring(AST, selected, offset, length);
	}
}