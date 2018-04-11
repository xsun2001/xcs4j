package io.xsun.xcs4j.parser.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegexASTBuilder {

    private static final Logger LOG = LogManager.getLogger(RegexASTBuilder.class);

    private final String regex;
    private final int length;

    //TODO: add javadoc
    public RegexASTBuilder(String regex) {
        this.regex = Objects.requireNonNull(regex);
        length = regex.length();
    }

    public final RegexASTNode buildAST() {
        LOG.info("Build the AST of regex [%s]", regex);
        return buildAST(0, length);
    }

    private RegexASTNode buildAST(int start, int end) {
        LOG.debug("Building the AST of regex[%s] in range [%d, %d]", start, end);
        Objects.checkFromIndexSize(start, end, length);
        return buildOr(buildConcatenation(buildNodeList(start, end)));
    }

    private int findingRightParenthesis(int left) {
        LOG.debug("Finding matching right parenthesis in regex [%s] for position [%d]", regex, left);
        Objects.checkIndex(left, length);
        int leftParenthesis = 0;
        for (int i = left; i < length; i++) {
            if (regex.charAt(i) == '(') ++leftParenthesis;
            if (regex.charAt(i) == ')') --leftParenthesis;
            if (leftParenthesis == 0) {
                LOG.debug("Found matching right parenthesis in position [%d]", i);
                return i;
            }
        }
        LOG.error("Cannot find matching parenthesis. The regex may have a syntax mistake.");
        throw new IllegalStateException("No matching parenthesis");
    }

    private List<RegexASTNode> buildNodeList(int start, int end) {
        LOG.debug("Building nodes list of regex [%s] in range [%d, %d]", regex, start, end);
        //TODO: more error check
        var nodeList = new ArrayList<RegexASTNode>();
        Objects.checkFromToIndex(start, end, length);
        for (int i = start; i < end; i++) {
            char c = regex.charAt(i);
            if (c == '(') {
                LOG.debug("Found left parenthesis in position [%d]", i);
                int right = findingRightParenthesis(i);
                nodeList.add(buildAST(i + 1, right));
                i = right + 1;
            } else if (c == '*') {
                LOG.debug("Found '*' in position [%d]", i);
                var node = nodeList.remove(nodeList.size() - 1);
                nodeList.add(new RegexASTNode(RegexASTNode.NodeType.STAR, node, null));
            } else if (c == '|') {
                LOG.debug("Found '|' in position [%d]", i);
                nodeList.add(new RegexASTNode(RegexASTNode.NodeType.OR));
            } else {
                LOG.debug("Found normal char [%c] in position [%d]", c, i);
                nodeList.add(new RegexASTNode(c));
            }
        }
        LOG.debug("Finished building nodes list of regex [%s]", regex);
        return nodeList;
    }

    private List<RegexASTNode> buildConcatenation(List<RegexASTNode> nodeList) {
        LOG.debug("Building CAT nodes");
        //TODO: more error check
        var newList = new ArrayList<RegexASTNode>();
        var temp = RegexASTNode.EMPTY_NODE;
        for (var node : nodeList) {
            var nodeType = node.getType();
            if (nodeType == RegexASTNode.NodeType.OR) {
                if (node.getLeft() == null && node.getRight() == null) {
                    LOG.debug("Meet an empty OR node. Stop concatenating any more");
                    if (temp != RegexASTNode.EMPTY_NODE) {
                        newList.add(temp);
                        temp = RegexASTNode.EMPTY_NODE;
                    }
                    newList.add(node);
                } else {
                    LOG.debug("Meet a nonempty OR node. It will be concatenated");
                    temp = temp == RegexASTNode.EMPTY_NODE ? node : new RegexASTNode(RegexASTNode.NodeType.CAT, temp, node);
                }
            } else {
                LOG.debug("Meet a [%s] node. It will be concatenated", nodeType.toString());
                temp = temp == RegexASTNode.EMPTY_NODE ? node : new RegexASTNode(RegexASTNode.NodeType.CAT, temp, node);
            }
        }
        LOG.debug("finishing building CAT nodes");
        return newList;
    }

    private RegexASTNode buildOr(List<RegexASTNode> nodeList) {
        LOG.debug("building OR nodes");
        var result = RegexASTNode.EMPTY_NODE;
        var iterator = nodeList.iterator();
        while (iterator.hasNext()) {
            var node = iterator.next();
            if (result == RegexASTNode.EMPTY_NODE) {
                if (checkOrNodeIsEmpty(node)) {
                    //Previous node is empty, current OR node has no left child
                    throw new IllegalStateException();
                }
                result = node;
            } else {
                if (node.getType() == RegexASTNode.NodeType.OR && checkOrNodeIsEmpty(node)) {
                    result = new RegexASTNode(RegexASTNode.NodeType.OR, result, iterator.next());
                } else {
                    //Two nodes whose type is node OR cannot be right next to each other
                    throw new IllegalStateException();
                }
            }
        }
        if (result.getType() == RegexASTNode.NodeType.OR) {
            checkOrNodeIsEmpty(result);
        }
        return result;
    }

    /**
     * Check if the given OR node is empty
     *
     * @param node the node to check
     * @return true if <code>node</code> is empty, or it has no child
     * @throws IllegalArgumentException If <code>code</code> isn't a OR node or is a OR node with only one child node
     */
    private boolean checkOrNodeIsEmpty(RegexASTNode node) {
        LOG.debug("Checking if [%s] is empty", node.toString());
        Objects.requireNonNull(node);
        if (node.getType() == RegexASTNode.NodeType.OR) {
            if (node.getLeft() == null && node.getRight() == null) return true;
            if (node.getLeft() != null && node.getRight() != null) return false;
        }
        LOG.error("Check failed. [%s] is ", node.toString());
        throw new IllegalArgumentException();
    }
}
