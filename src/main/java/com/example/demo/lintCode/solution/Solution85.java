package com.example.demo.lintCode.solution;

public class Solution85 {
    /*
     * @param root: The root of the binary search tree.
     * @param node: insert this node into the binary search tree
     * @return: The root of the new binary search tree.
     */
    public TreeNode insertNode(TreeNode root, TreeNode node) {
        // write your code here
        if (root == null) {
            root = node;
            return node;
        }
        if (root.right == null) {
            root.right = node;
            return root;
        }
        if (root.left == null) {
            root.left = node;
            return root;
        }
        TreeNode left = root.left;
        TreeNode right = root.right;
        if (!isNull(right,node)) {
            isNull(left, node);
        }
        return root;
    }

    private boolean isNull(TreeNode root,TreeNode node) {
        if (root.right == null) {
            root.right = node;
            return true;
        }
        if (root.left == null) {
            root.left = node;
            return true;
        }
        if (!isNull(root.right,node)) {
            return isNull(root.left, node);
        }
        return false;
    }

    public class TreeNode {
        public int val;
        public TreeNode left, right;

        public TreeNode(int val) {
            this.val = val;
            this.left = this.right = null;
        }
    }

    public static void main(String[] args) {
        System.out.println();
    }
}
