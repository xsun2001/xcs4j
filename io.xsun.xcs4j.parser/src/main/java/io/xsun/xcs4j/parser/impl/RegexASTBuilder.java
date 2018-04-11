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
        LOG.info("Build the AST of regex [{}]", regex);
        return buildAST(0, length);
    }

    private RegexASTNode buildAST(int start, int end) {
        LOG.debug("Building the AST of regex[{}] in range [{}, {}]", regex, start, end);
        Objects.checkFromIndexSize(start, end, length);
        return buildOr(buildConcatenation(buildNodeList(start, end)));
    }

    private int findingRightParenthesis(int left) {
        LOG.debug("Finding matching right parenthesis in regex [{}] for position {}", regex, left);
        Objects.checkIndex(left, length);
        int leftParenthesis = 0;
        for (int i = left; i < length; i++) {
            if (regex.charAt(i) == '(') ++leftParenthesis;
            if (regex.charAt(i) == ')') --leftParenthesis;
            if (leftParenthesis == 0) {
                LOG.debug("Found matching right parenthesis in position {}", i);
                return i;
            }
        }
        LOG.error("Cannot find matching parenthesis. The regex may have a syntax mistake.");
        throw new IllegalStateException("No matching parenthesis");
    }

    private List<RegexASTNode> buildNodeList(int start, int end) {
        LOG.debug("Building nodes list of regex [{}] in range [{}, {}]", regex, start, end);
        //TODO: more error check
        var nodeList = new ArrayList<RegexASTNode>();
        Objects.checkFromToIndex(start, end, length);
        for (int i = start; i < end; i++) {
            char c = regex.charAt(i);
            if (c == '(') {
                LOG.debug("Found left parenthesis in position {}", i);
                int right = findingRightParenthesis(i);
                nodeList.add(buildAST(i + 1, right));
                i = right + 1;
            } else if (c == '*') {
                LOG.debug("Found '*' in position {}", i);
                var node = nodeList.remove(nodeList.size() - 1);
                nodeList.add(new RegexASTNode(RegexASTNode.NodeType.STAR, node, null));
            } else if (c == '|') {
                LOG.debug("Found '|' in position {}", i);
                nodeList.add(new RegexASTNode(RegexASTNode.NodeType.OR));
            } else {
                LOG.debug("Found normal char '{}' in position {}", c, i);
                nodeList.add(new RegexASTNode(c));
            }
        }
        LOG.debug("Finished building nodes list of regex [{}]", regex);
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
                LOG.debug("Meet a {} node. It will be concatenated", nodeType.toString());
                temp = temp == RegexASTNode.EMPTY_NODE ? node : new RegexASTNode(RegexASTNode.NodeType.CAT, temp, node);
            }
        }
        LOG.debug("Finishing building CAT nodes");
        return newList;
    }

    private RegexASTNode buildOr(List<RegexASTNode> nodeList) {
        LOG.debug("Building OR nodes");
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
        LOG.debug("Finish building OR nodes");
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
        LOG.debug("Checking if [{}] is empty", node);
        Objects.requireNonNull(node);
        if (node.getType() == RegexASTNode.NodeType.OR) {
            if (node.getLeft() == null && node.getRight() == null) return true;
            if (node.getLeft() != null && node.getRight() != null) return false;
        }
        LOG.error("Check failed. [{}] is ", node);
        throw new IllegalArgumentException();
    }
}
