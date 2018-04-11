package io.xsun.xcs4j.parser.impl;

import org.junit.Test;

import static io.xsun.xcs4j.parser.impl.RegexASTNode.NodeType.*;
import static org.junit.Assert.assertEquals;

public class RegexASTBuilderTest {

    public void testBuildAST(String regex, RegexASTNode answer) {
        assertEquals(answer, new RegexASTBuilder(regex).buildAST());
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

    @Test
    public void testBuildAST5() {
        var node1 = new RegexASTNode(CAT, new RegexASTNode('a'), new RegexASTNode('b'));
        var node2 = new RegexASTNode(CAT, new RegexASTNode('b'), new RegexASTNode('a'));
        var node3 = new RegexASTNode(STAR, new RegexASTNode(OR, node1, node2), null);
        var answer = new RegexASTNode(CAT,
                new RegexASTNode(CAT,
                        node3,
                        new RegexASTNode('a')),
                new RegexASTNode('b')
        );

        testBuildAST("(ab|ba)*ab", answer);
    }

    @Test
    public void testBuildAST6(){
        var node1 = new RegexASTNode(STAR, new RegexASTNode(OR, new RegexASTNode('a'), new RegexASTNode('b')), null);
        var answer = new RegexASTNode(CAT,
                new RegexASTNode(CAT,
                        new RegexASTNode(CAT,
                                new RegexASTNode(CAT, node1, new RegexASTNode('a')),
                                new RegexASTNode('b')),
                        new RegexASTNode('b')),
                node1
        );

        testBuildAST("(a|b)*abb(a|b)*", answer);
    }
}