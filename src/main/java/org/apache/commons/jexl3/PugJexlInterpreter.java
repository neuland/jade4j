package org.apache.commons.jexl3;

import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.internal.Interpreter;
import org.apache.commons.jexl3.internal.Scope;
import org.apache.commons.jexl3.parser.ASTIdentifier;
import org.apache.commons.jexl3.parser.ASTMethodNode;
import org.apache.commons.jexl3.parser.ASTReference;
import org.apache.commons.jexl3.parser.JexlNode;
import org.apache.commons.jexl3.parser.Token;

public class PugJexlInterpreter extends Interpreter {
	public PugJexlInterpreter(Engine engine, JexlContext aContext, Scope.Frame eFrame) {
		super(engine, aContext, eFrame);
	}



	@Override
	protected Object visit(ASTReference node, Object data) {
		int numChildren = node.jjtGetNumChildren();
		for (int c = 0; c < numChildren; ++c) {
			JexlNode childNode = node.jjtGetChild(c);
			if (childNode instanceof ASTMethodNode && node.jjtGetChild(0) != childNode
				&&
                (
                    (
                        node.jjtGetChild(0) instanceof ASTIdentifier
                        &&  context.get( ((ASTIdentifier) node.jjtGetChild(0)).getName()) == null
                    )
                    || !(node.jjtGetChild(0) instanceof ASTIdentifier)
                )
			) {
				// correct info where exception took place
				addExceptionInfoTo(childNode);
				throw new JexlException(childNode, "attempting to call method on null");
			}
		}
		return super.visit(node, data);
	}

	private void addExceptionInfoTo(JexlNode childNode) {
		JexlInfo info = createInfo();
		childNode.jjtSetValue(info);
		Token t = new Token();
		t.beginLine = info.getLine();
		t.endLine = 0;
		childNode.jjtSetFirstToken(t);
	}


	private JexlInfo createInfo() {
		JexlInfo info = null;
		StackTraceElement[] stack = new Throwable().getStackTrace();
		StackTraceElement se = null;
		String name = getClass().getName();
		for (int s = 1; s < stack.length; ++s) {
			se = stack[s];
			String className = se.getClassName();
			if (!className.equals(name)) {
				// go deeper if called from jexl implementation classes
				if (className.startsWith("org.apache.commons.jexl3.")) {
					name = className;
				} else {
					break;
				}
			}
		}
		if (se != null) {
			info = jexl.createInfo(se.getClassName() + "." + se.getMethodName(), se.getLineNumber(), 0);
		}
		return info;
	}
}