package reverse.conditional.c;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import reverse.conditional.ReverseConditionalRefactoring;

public class ReverseConditionalCRefactoring extends ReverseConditionalRefactoring {

	private IASTTranslationUnit AST;
	private IASTNode selectedNode;
	private int offset, length;
	
	ReverseConditionalCRefactoring() {}
	
	ReverseConditionalCRefactoring(IASTTranslationUnit AST, IASTNode selected, int offset, 
			int length) {
		this.AST = AST;
		this.selectedNode = selected;
		
		this.selectedNode = selected;
		this.offset = offset;
		this.length = length;
	}
	
	@Override
	public String getName() {
		return "Reverse Conditional";
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status = new RefactoringStatus();
		
		pm.beginTask("checkInitialConditions", 0);
		if (!validNode()) {
			status.addFatalError("Not a valid selection.");
		}
		pm.done();
		
		return status;
	}
	
	private boolean validNode() {
		if (this.selectedNode instanceof IASTCompoundStatement) {
			for (IASTNode child : this.selectedNode.getChildren()) {
				if (child instanceof IASTIfStatement) {
					this.selectedNode = child;
				}
			}
		} 
		if (!(this.selectedNode instanceof IASTIfStatement)) {
			return false;
		} else if (((IASTIfStatement) this.selectedNode).getElseClause() == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		ASTRewrite rewriter = ASTRewrite.create(this.AST);
		
		new ReverseConditionalCAlteration(this.AST, this.selectedNode).change(rewriter);
		return rewriter.rewriteAST();
	}
	
	public void setCompilationUnit(IASTTranslationUnit AST) {
		this.AST = AST;
	}
	
	public void setSelectedNode(IASTNode node) {
		this.selectedNode = node;
	}
	
	public void setOffsetLength(int offset, int length) {
		this.offset = offset;
		this.length = length;
	}
}
