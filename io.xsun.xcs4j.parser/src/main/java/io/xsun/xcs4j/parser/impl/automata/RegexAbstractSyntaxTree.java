package io.xsun.xcs4j.parser.impl.automata;

import java.util.Objects;

public class RegexAbstractSyntaxTree {

    public RegexAbstractSyntaxTree(String regex) {

    }

    public static class Node {
        private NodeType type;
        private Node left, right;

        public Node(NodeType type) {

            this.type = type;
        }

        public Node(NodeType type, Node left, Node right) {

            this.type = type;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Node{");
            sb.append("type=").append(type);
            sb.append(", left=").append(left);
            sb.append(", right=").append(right);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return type == node.type &&
                    Objects.equals(left, node.left) &&
                    Objects.equals(right, node.right);
        }

        @Override
        public int hashCode() {

            return Objects.hash(type, left, right);
        }

        public NodeType getType() {

            return type;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public enum NodeType {
            CHAR, EMPTY, CAT, OR, STAR
        }
    }

}
