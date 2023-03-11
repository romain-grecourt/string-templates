package com.acme.codegen;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;

/**
 * Simple tree scanner.
 *
 * @param <P> argument type
 */
public class SimpleTreeScanner<P> extends TreeScanner<Void, P> {

    /**
     * Invoked for every node visited.
     *
     * @param node node
     * @param p    argument
     */
    protected void visit(Tree node, P p) {
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree node, P p) {
        visit(node, p);
        return super.visitCompilationUnit(node, p);
    }

    @Override
    public Void visitPackage(PackageTree node, P p) {
        visit(node, p);
        return super.visitPackage(node, p);
    }

    @Override
    public Void visitImport(ImportTree node, P p) {
        visit(node, p);
        return super.visitImport(node, p);
    }

    @Override
    public Void visitClass(ClassTree node, P p) {
        visit(node, p);
        return super.visitClass(node, p);
    }

    @Override
    public Void visitMethod(MethodTree node, P p) {
        visit(node, p);
        return super.visitMethod(node, p);
    }

    @Override
    public Void visitVariable(VariableTree node, P p) {
        visit(node, p);
        return super.visitVariable(node, p);
    }

    @Override
    public Void visitEmptyStatement(EmptyStatementTree node, P p) {
        visit(node, p);
        return super.visitEmptyStatement(node, p);
    }

    @Override
    public Void visitBlock(BlockTree node, P p) {
        visit(node, p);
        return super.visitBlock(node, p);
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree node, P p) {
        visit(node, p);
        return super.visitDoWhileLoop(node, p);
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree node, P p) {
        visit(node, p);
        return super.visitWhileLoop(node, p);
    }

    @Override
    public Void visitForLoop(ForLoopTree node, P p) {
        visit(node, p);
        return super.visitForLoop(node, p);
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree node, P p) {
        visit(node, p);
        return super.visitEnhancedForLoop(node, p);
    }

    @Override
    public Void visitLabeledStatement(LabeledStatementTree node, P p) {
        visit(node, p);
        return super.visitLabeledStatement(node, p);
    }

    @Override
    public Void visitSwitch(SwitchTree node, P p) {
        visit(node, p);
        return super.visitSwitch(node, p);
    }

    @Override
    public Void visitSwitchExpression(SwitchExpressionTree node, P p) {
        visit(node, p);
        return super.visitSwitchExpression(node, p);
    }

    @Override
    public Void visitCase(CaseTree node, P p) {
        visit(node, p);
        return super.visitCase(node, p);
    }

    @Override
    public Void visitSynchronized(SynchronizedTree node, P p) {
        visit(node, p);
        return super.visitSynchronized(node, p);
    }

    @Override
    public Void visitTry(TryTree node, P p) {
        visit(node, p);
        return super.visitTry(node, p);
    }

    @Override
    public Void visitCatch(CatchTree node, P p) {
        visit(node, p);
        return super.visitCatch(node, p);
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree node, P p) {
        visit(node, p);
        return super.visitConditionalExpression(node, p);
    }

    @Override
    public Void visitIf(IfTree node, P p) {
        visit(node, p);
        return super.visitIf(node, p);
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree node, P p) {
        visit(node, p);
        return super.visitExpressionStatement(node, p);
    }

    @Override
    public Void visitBreak(BreakTree node, P p) {
        visit(node, p);
        return super.visitBreak(node, p);
    }

    @Override
    public Void visitContinue(ContinueTree node, P p) {
        visit(node, p);
        return super.visitContinue(node, p);
    }

    @Override
    public Void visitReturn(ReturnTree node, P p) {
        visit(node, p);
        return super.visitReturn(node, p);
    }

    @Override
    public Void visitThrow(ThrowTree node, P p) {
        visit(node, p);
        return super.visitThrow(node, p);
    }

    @Override
    public Void visitAssert(AssertTree node, P p) {
        visit(node, p);
        return super.visitAssert(node, p);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, P p) {
        visit(node, p);
        return super.visitMethodInvocation(node, p);
    }

    @Override
    public Void visitNewClass(NewClassTree node, P p) {
        visit(node, p);
        return super.visitNewClass(node, p);
    }

    @Override
    public Void visitNewArray(NewArrayTree node, P p) {
        visit(node, p);
        return super.visitNewArray(node, p);
    }

    @Override
    public Void visitLambdaExpression(LambdaExpressionTree node, P p) {
        visit(node, p);
        return super.visitLambdaExpression(node, p);
    }

    @Override
    public Void visitParenthesized(ParenthesizedTree node, P p) {
        visit(node, p);
        return super.visitParenthesized(node, p);
    }

    @Override
    public Void visitAssignment(AssignmentTree node, P p) {
        visit(node, p);
        return super.visitAssignment(node, p);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, P p) {
        visit(node, p);
        return super.visitCompoundAssignment(node, p);
    }

    @Override
    public Void visitUnary(UnaryTree node, P p) {
        visit(node, p);
        return super.visitUnary(node, p);
    }

    @Override
    public Void visitBinary(BinaryTree node, P p) {
        visit(node, p);
        return super.visitBinary(node, p);
    }

    @Override
    public Void visitTypeCast(TypeCastTree node, P p) {
        visit(node, p);
        return super.visitTypeCast(node, p);
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree node, P p) {
        visit(node, p);
        return super.visitInstanceOf(node, p);
    }

    @Override
    public Void visitBindingPattern(BindingPatternTree node, P p) {
        visit(node, p);
        return super.visitBindingPattern(node, p);
    }

    @Override
    public Void visitDefaultCaseLabel(DefaultCaseLabelTree node, P p) {
        visit(node, p);
        return super.visitDefaultCaseLabel(node, p);
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree node, P p) {
        visit(node, p);
        return super.visitArrayAccess(node, p);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree node, P p) {
        visit(node, p);
        return super.visitMemberSelect(node, p);
    }

    @Override
    public Void visitParenthesizedPattern(ParenthesizedPatternTree node, P p) {
        visit(node, p);
        return super.visitParenthesizedPattern(node, p);
    }

    @Override
    public Void visitMemberReference(MemberReferenceTree node, P p) {
        visit(node, p);
        return super.visitMemberReference(node, p);
    }

    @Override
    public Void visitIdentifier(IdentifierTree node, P p) {
        visit(node, p);
        return super.visitIdentifier(node, p);
    }

    @Override
    public Void visitLiteral(LiteralTree node, P p) {
        visit(node, p);
        return super.visitLiteral(node, p);
    }

    @Override
    public Void visitPrimitiveType(PrimitiveTypeTree node, P p) {
        visit(node, p);
        return super.visitPrimitiveType(node, p);
    }

    @Override
    public Void visitArrayType(ArrayTypeTree node, P p) {
        visit(node, p);
        return super.visitArrayType(node, p);
    }

    @Override
    public Void visitParameterizedType(ParameterizedTypeTree node, P p) {
        visit(node, p);
        return super.visitParameterizedType(node, p);
    }

    @Override
    public Void visitUnionType(UnionTypeTree node, P p) {
        visit(node, p);
        return super.visitUnionType(node, p);
    }

    @Override
    public Void visitIntersectionType(IntersectionTypeTree node, P p) {
        visit(node, p);
        return super.visitIntersectionType(node, p);
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree node, P p) {
        visit(node, p);
        return super.visitTypeParameter(node, p);
    }

    @Override
    public Void visitWildcard(WildcardTree node, P p) {
        visit(node, p);
        return super.visitWildcard(node, p);
    }

    @Override
    public Void visitModifiers(ModifiersTree node, P p) {
        visit(node, p);
        return super.visitModifiers(node, p);
    }

    @Override
    public Void visitAnnotation(AnnotationTree node, P p) {
        visit(node, p);
        return super.visitAnnotation(node, p);
    }

    @Override
    public Void visitAnnotatedType(AnnotatedTypeTree node, P p) {
        visit(node, p);
        return super.visitAnnotatedType(node, p);
    }

    @Override
    public Void visitModule(ModuleTree node, P p) {
        visit(node, p);
        return super.visitModule(node, p);
    }

    @Override
    public Void visitExports(ExportsTree node, P p) {
        visit(node, p);
        return super.visitExports(node, p);
    }

    @Override
    public Void visitOpens(OpensTree node, P p) {
        visit(node, p);
        return super.visitOpens(node, p);
    }

    @Override
    public Void visitProvides(ProvidesTree node, P p) {
        visit(node, p);
        return super.visitProvides(node, p);
    }

    @Override
    public Void visitRequires(RequiresTree node, P p) {
        visit(node, p);
        return super.visitRequires(node, p);
    }

    @Override
    public Void visitUses(UsesTree node, P p) {
        visit(node, p);
        return super.visitUses(node, p);
    }

    @Override
    public Void visitErroneous(ErroneousTree node, P p) {
        visit(node, p);
        return super.visitErroneous(node, p);
    }

    @Override
    public Void visitOther(Tree node, P p) {
        visit(node, p);
        return super.visitOther(node, p);
    }

    @Override
    public Void visitYield(YieldTree node, P p) {
        visit(node, p);
        return super.visitYield(node, p);
    }
}
