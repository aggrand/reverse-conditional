package reverse.conditional.c;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.INodeFactory;
import org.eclipse.cdt.core.dom.ast.c.ICNodeFactory;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

public class ReverseConditionalCAlteration {

	private IASTTranslationUnit AST;
	private IASTNode selectedNode;
	
	public ReverseConditionalCAlteration(IASTTranslationUnit AST, IASTNode selectedNode) {
		this.AST = AST;
		this.selectedNode = selectedNode;
	}

	public void change(ASTRewrite rewriter) throws JavaModelException {
		
		IASTExpression ifExpression = ((IASTIfStatement) selectedNode).getConditionExpression();
		IASTExpression copiedNode = (IASTExpression) ifExpression.copy();
		swapStatements(rewriter, (IASTIfStatement) selectedNode);
		
		negateIfExpression(rewriter, AST, ifExpression, copiedNode);
	}
	
	private void swapStatements(ASTRewrite rewriter, IASTIfStatement selectedNode2) {
		IASTStatement thenStatement = selectedNode2.getThenClause();
        IASTStatement elseStatement = selectedNode2.getElseClause();
        
        rewriter.replace(thenStatement, elseStatement, null);
        rewriter.replace(elseStatement, thenStatement, null); 
	}
	
	private void negateIfExpression(ASTRewrite astRewrite, IASTTranslationUnit AST, IASTExpression ifExpression, 
			IASTExpression copiedNode) {
        IASTExpression negatedExpr = createNewExpression(AST, copiedNode);
        astRewrite.replace(ifExpression, negatedExpr, null);
    }
	
	private IASTExpression createNewExpression(IASTTranslationUnit ast, IASTExpression originalExpression) {
        
        IASTExpression newNode;
        if (originalExpression instanceof IASTUnaryExpression 
                && ((IASTUnaryExpression) originalExpression).getOperator() == IASTUnaryExpression.op_not) {
            newNode = ((IASTUnaryExpression) originalExpression).getOperand();
            if (newNode instanceof IASTUnaryExpression 
            		&& ((IASTUnaryExpression) newNode).getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
                newNode = ((IASTUnaryExpression) newNode).getOperand();
            }
        } else {
            newNode = createParenthesizedNotExpr(ast, originalExpression);
        }
        return newNode;
    }
	
	private IASTUnaryExpression createParenthesizedNotExpr(IASTTranslationUnit ast, IASTExpression copiedNode) {
		ICNodeFactory factory = ASTNodeFactoryFactory.getDefaultCNodeFactory();
		IASTUnaryExpression parenExp = 
				factory.newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, copiedNode);
		IASTUnaryExpression newExpression = factory.newUnaryExpression(IASTUnaryExpression.op_not, parenExp);
        return newExpression;
    }
	
	
}
