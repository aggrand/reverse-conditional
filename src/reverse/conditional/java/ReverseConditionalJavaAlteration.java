package reverse.conditional.java;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class ReverseConditionalJavaAlteration {

	private CompilationUnit compNode;
	private int offset, length;
	
	public ReverseConditionalJavaAlteration(CompilationUnit compNode, int offset, int length) {
		this.compNode = compNode;
		this.offset = offset;
		this.length = length;
	}
	
	public void change(ASTRewrite rewriter) throws JavaModelException {
		NodeFinder finder = new NodeFinder(this.compNode, this.offset, this.length);
		ASTNode selectedNode = finder.getCoveringNode();
		if (selectedNode instanceof Block) {
			for (Object child : ((Block) selectedNode).statements()) {
				if (((ASTNode) child).getNodeType() == ASTNode.IF_STATEMENT) {
					selectedNode = (ASTNode) child;
					break;
				}
			}
		}
		AST ast = this.compNode.getAST();
		
		Expression ifExpression = ((IfStatement) selectedNode).getExpression();
		Expression copiedNode = (Expression) ASTNode.copySubtree(ast, ifExpression);
		swapStatements(rewriter, (IfStatement) selectedNode);
		
		negateIfExpression(rewriter, ast, ifExpression, copiedNode);
	}
	
	private void swapStatements(ASTRewrite rewriter, IfStatement selectedNode) {
		Statement thenStatement = selectedNode.getThenStatement();
        Statement elseStatement = selectedNode.getElseStatement();
        
        rewriter.replace(thenStatement, elseStatement, null);
        rewriter.replace(elseStatement, thenStatement, null); 
	}
	
	private void negateIfExpression(ASTRewrite astRewrite, AST ast, Expression ifExpression, Expression copiedNode) {
        Expression negatedExpr = createNewExpression(ast, copiedNode);
        astRewrite.replace(ifExpression, negatedExpr, null);
    }
	
	private Expression createNewExpression(AST ast, Expression originalExpression) {
        
        Expression newNode;
        if (originalExpression instanceof PrefixExpression 
                && ((PrefixExpression) originalExpression).getOperator().equals(PrefixExpression.Operator.NOT)) {
            newNode = ((PrefixExpression) originalExpression).getOperand();
            if (newNode instanceof ParenthesizedExpression) {
                newNode = ((ParenthesizedExpression) newNode).getExpression();
            }
        } else {
            ParenthesizedExpression parenthExpr = createParenthesizedExpr(ast, originalExpression);
            newNode = ast.newPrefixExpression();
            ((PrefixExpression) newNode).setOperator(Operator.NOT);
            ((PrefixExpression) newNode).setOperand(parenthExpr);
        }
        return newNode;
    }
	
	private ParenthesizedExpression createParenthesizedExpr(AST ast, Expression copiedNode) {
        ParenthesizedExpression parenthExpr = ast.newParenthesizedExpression();
        parenthExpr.setExpression(copiedNode);
        return parenthExpr;
    }
	
	
}
