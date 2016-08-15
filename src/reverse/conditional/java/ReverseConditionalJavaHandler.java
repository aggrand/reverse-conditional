package reverse.conditional.java;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import reverse.conditional.ReverseConditionalWizard;

public class ReverseConditionalJavaHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		RefactoringWizard wizard = new ReverseConditionalWizard(createRefactoring(selection, event));
		Shell parent = HandlerUtil.getActiveShell(event);
		String dialogTitle = "Reverse Conditional";
		try {
            RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(wizard);
            operation.run(parent, dialogTitle);
        } catch (InterruptedException exception) {
            // Do nothing
        }
		return null;
	}

	private ReverseConditionalJavaRefactoring createRefactoring(ISelection selection, ExecutionEvent event) {
		IEditorPart editorSite = HandlerUtil.getActiveEditor(event);
		IEditorInput editorInput = editorSite.getEditorInput();
		IResource resource = (IResource) editorInput.getAdapter(IResource.class);
		
		ICompilationUnit compUnit = JavaCore.createCompilationUnitFrom((IFile) resource);
		TextSelection ts = (TextSelection) selection;
		CompilationUnit cu = getCompilationUnit(compUnit);
		int offset = ts.getOffset();
		int length = ts.getLength();
		NodeFinder finder = new NodeFinder(cu, offset, length);
		ASTNode selected = finder.getCoveringNode();
		return new ReverseConditionalJavaRefactoring(compUnit, selected, offset, length);
	}
	
	private CompilationUnit getCompilationUnit(ICompilationUnit compUnit) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(compUnit);
        parser.setResolveBindings(true);
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        return cu;
    }
	
}