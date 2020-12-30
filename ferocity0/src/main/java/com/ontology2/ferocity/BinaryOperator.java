package com.ontology2.ferocity;

abstract class BinaryOperator<L, R, O>  extends Expression<O> {
    private final Expression<L> left;
    private final Expression<R> right;

    BinaryOperator(Expression<L> left, Expression<R> right) {
        this.left = left;
        this.right = right;
    }

    abstract String getSymbol();

    public String toString() {
        StringBuilder that=new StringBuilder();
        that.append('(');
        that.append(left.toString());
        that.append(getSymbol());
        that.append(right.toString());
        that.append(')');
        return that.toString();
    };
}

/**
 * Not sure if we need to differentiate this from ConcatOperator
 *
 * @param <L>
 * @param <R>
 * @param <O>
 */
class AddOperator<L,R,O> extends BinaryOperator<L, R, O> {
    AddOperator(Expression<L> left, Expression<R> right) {
        super(left, right);
    }

    String getSymbol() {
        return "+";
    }

    @Override
    public O evaluate(Context ctx) {
        throw new UnsupportedOperationException();
    }
}
