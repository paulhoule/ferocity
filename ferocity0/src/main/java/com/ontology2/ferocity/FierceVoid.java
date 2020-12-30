package com.ontology2.ferocity;

/**
 * This singleton class is a stand-in for all the null values of the world.
 *
 */
public final class FierceVoid {
    final static FierceVoid _nothing = new FierceVoid();
    private FierceVoid() {};
    public static FierceVoid nothing() { return _nothing; }
}
