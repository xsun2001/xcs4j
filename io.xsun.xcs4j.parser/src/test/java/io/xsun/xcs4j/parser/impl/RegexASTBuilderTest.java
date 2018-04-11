package io.xsun.xcs4j.parser.impl;

import org.junit.Test;

import static io.xsun.xcs4j.parser.impl.RegexASTNode.NodeType.*;
import static org.junit.Assert.assertEquals;

public class RegexASTBuilderTest {

    public void testBuildAST(String regex, RegexASTNode answer) {
        assertEquals(new RegexASTBuilder(regex).buildAST(), answer);
    }

    @Test
    public void testBuildAST1() {
        testBuildAST("a", new RegexASTNode('a'));
    }

    @Test
    public void testBuildAST2() {
        var answer = new RegexASTNode(CAT, new RegexASTNode('a'), new RegexASTNode('b'));
        testBuildAST("ab", answer);
    }

    @Test
    public void testBuildAST3() {
        var answer = new RegexASTNode(STAR, new RegexASTNode('a'), null);
        testBuildAST("a*", answer);
    }

    @Test
    public void testBuildAST4() {
        var answer = new RegexASTNode(OR, new RegexASTNode('a'), new RegexASTNode('b'));
        testBuildAST("a|b", answer);
    }
}