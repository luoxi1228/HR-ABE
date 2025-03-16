package com.luoxi.hrabe.ABE2OD;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class LSSS {
    private String accessPolicy;
    private List<List<Integer>> M;
    private List<String> rho;

    public LSSS() {
        this.M = new ArrayList<>();
        this.rho = new ArrayList<>();
    }
    public LSSS(String accessPolicy) {
        this.M = new ArrayList<>();
        this.rho = new ArrayList<>();
        initialize(accessPolicy);
    }

    public String getAccessPolicy() {
        return accessPolicy;
    }

    public void setAccessPolicy(String accessPolicy) {
        this.accessPolicy = accessPolicy;
    }

    public List<List<Integer>> getM() {
        return M;
    }

    public void setM(List<List<Integer>> m) {
        M = m;
    }

    public List<String> getRho() {
        return rho;
    }

    public void setRho(List<String> rho) {
        this.rho = rho;
    }

    private static class Pair<K, V> {
        private K first;
        private V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }

        public K getFirst() {
            return first;
        }

        public V getSecond() {
            return second;
        }
    }
    private void parseString(Stack<Pair<List<Integer>, String>> stk,
                             Pair<List<Integer>, String> pair,
                             AtomicInteger counter) {
        if (pair.getSecond().charAt(0) != '(') {
            this.M.add(new ArrayList<>(pair.getFirst()));
            this.rho.add(pair.getSecond());
            return;
        }

        String str = pair.getSecond().substring(1, pair.getSecond().length() - 1);
        int d = str.charAt(str.length() - 1) - '0';
        str = str.substring(0, str.length() - 2);

        List<String> substrvec = new ArrayList<>();
        StringBuilder substr = new StringBuilder();
        int ct = 0;

        for (int i = 0; i < str.length(); i++) {
            substr.append(str.charAt(i));
            if (str.charAt(i) == '(') ++ct;
            if (str.charAt(i) == ')') --ct;
            if (ct == 0 && (str.charAt(i) == ')' || (i < str.length() - 1 && str.charAt(i + 1) == ','))) {
                substrvec.add(substr.toString());
                substr.setLength(0);
                i++;
            }
        }
        if (!substr.isEmpty()) substrvec.add(substr.toString());

        while (pair.getFirst().size() < counter.get()) {
            pair.getFirst().add(0);
        }

        for (int i = substrvec.size() - 1; i >= 0; --i) {
            List<Integer> vec = new ArrayList<>(pair.getFirst());
            vec.add(i + 1);
            stk.push(new Pair<>(vec, substrvec.get(i)));
        }

        counter.addAndGet(d - 1);
    }

    public void initialize(String accessControl) {
        this.accessPolicy = accessControl;
        accessControl = utils.trim(accessControl);
        //System.out.println(accessControl);

        Stack<Pair<List<Integer>, String>> stk = new Stack<>();
        List<Integer> initVec = new ArrayList<>();
        initVec.add(1);
        AtomicInteger counter = new AtomicInteger(1);

        stk.push(new Pair<>(initVec, accessControl));

        while (!stk.isEmpty()) {
            //showStack(stk);
            Pair<List<Integer>, String> top = stk.pop();
            parseString(stk, top, counter);
            //System.out.println("Counter: " + counter.get());
        }

        for (List<Integer> row : M) {
            while (row.size() < counter.get()) {
                row.add(0);
            }
        }
    }


    public List<Element> generateShares(List<Element> secret, Pairing pairing) {
        List<Element> shares = new ArrayList<>();

        // 逐个生成共享份额
        for (int i = 0; i < this.M.size(); i++) {
            // 初始化临时元素为0
            Element tmp = pairing.getZr().newElement().setToZero();

            // 计算每行的线性组合
            for (int j = 0; j < secret.size(); j++) {
                // 创建临时元素存储乘积
                Element tmp1 = secret.get(j).duplicate()  // 复制secret[j]
                        .mulZn(pairing.getZr().newElement(this.M.get(i).get(j)))  // 乘以矩阵元素
                        .getImmutable();  // 设置为不可变

                // 累加到结果中
                tmp = tmp.add(tmp1).getImmutable();
            }

            // 将计算结果添加到共享份额列表
            shares.add(tmp);
        }

        return shares;
    }

    public void getValidSharesExt(List<Element> validShares, List<Element> valid_Ci, List<Element> valid_Di,
                                  List<Element> valid_kx, List<Element> shares,
                                  List<Element> Ci, List<Element> Di, Map<String, Element> kx,
                                  String attributes, Pairing pairing) {

        // 解析属性字符串
        List<String> attributes_set = utils.string2attributeSet(attributes);

        // 获取有效的索引集合
        List<Integer> I = utils.fetchRows(attributes_set, this.rho);
        if (I.isEmpty()) return;

        // 遍历有效索引，提取有效元素
        for (int i = 0; i < I.size(); ++i) {
            int index = I.get(i);

            // 确保 rho.get(index) 存在
            String key = rho.get(index);
            if (key == null || !kx.containsKey(key)) {
                throw new IllegalStateException("Key not found in kx map: " + key);
            }

            // 复制 Element 避免修改原数据
            Element tmp = shares.get(index).duplicate();
            Element c = Ci.get(index).duplicate();
            Element d = Di.get(index).duplicate();
            Element k = kx.get(key).duplicate();

            // 存入有效列表
            validShares.add(tmp);
            valid_Ci.add(c);
            valid_Di.add(d);
            valid_kx.add(k);
        }
    }

    public void findVector(List<Element> vec, String attributes, Pairing pairing) {
        // 解析属性字符串
        List<String> attributesSet = utils.string2attributeSet(attributes);

        // 获取匹配的行索引
        List<Integer> I = utils.fetchRows(attributesSet, this.rho);
        if (I.isEmpty()) return;

        // 构建增广矩阵
        List<List<Double>> matrix = new ArrayList<>();
        for (int i = 0; i < M.get(0).size(); ++i) {
            List<Double> rowVec = new ArrayList<>();
            for (int j = 0; j < I.size(); ++j) {
                rowVec.add((double)M.get(I.get(j)).get(i));
            }
            rowVec.add(i == 0 ? 1.0 : 0.0);
            matrix.add(rowVec);
        }

        // 计算矩阵的转置
        List<List<Double>> matrixT = new ArrayList<>();
        for (int i = 0; i < I.size(); ++i) {
            List<Double> rowVec = new ArrayList<>();
            for (int j = 0; j < M.get(0).size(); ++j) {
                rowVec.add((double) M.get(I.get(i)).get(j));
            }
            matrixT.add(rowVec);
        }

        // 计算矩阵乘积 prod = matrixT * matrix
        List<List<Double>> prod = new ArrayList<>();
        for (int i = 0; i < matrixT.size(); ++i) {
            List<Double> rowVec = new ArrayList<>();
            for (int j = 0; j < matrix.get(0).size(); ++j) {
                double sum = 0.0;
                for (int k = 0; k < matrixT.get(0).size(); ++k) {
                    sum += matrixT.get(i).get(k) * matrix.get(k).get(j);
                }
                rowVec.add(sum);
            }
            prod.add(rowVec);
        }

        // 转换 prod 矩阵中的每个元素为 Element 类型
        List<List<Element>> prod_G = new ArrayList<>();
        for (List<Double> row : prod) {
            List<Element> rowVec = new ArrayList<>();
            for (Double value : row) {
                Element a = pairing.getZr().newElement(new BigInteger(String.valueOf(Math.round(value)))).getImmutable();
                rowVec.add(a);
            }
            prod_G.add(rowVec);
        }


        // 求解线性方程组
        utils.solve(vec, prod_G, pairing);
    }

    public void showLSSS() {
        System.out.println("\n=== 矩阵 M ===");
        for (List<Integer> row : M) {
            System.out.println(row);
        }

        System.out.println("\n=== rho 属性映射 ===");
        System.out.println(rho);
    }

    public boolean isSatisfy(String userAttributes) {
        List<String> userAttrs = utils.string2attributeSet(userAttributes);
        PolicyNode root = parsePolicy(this.accessPolicy);
        return checkPolicy(root, userAttrs);
    }

    // 策略节点内部类
    private static class PolicyNode {
        int threshold; // 门限值（仅门限节点有效）
        List<PolicyNode> children; // 子节点（门限节点）
        String attribute; // 属性名（属性节点）
    }

    // 解析策略字符串为策略树
    private PolicyNode parsePolicy(String policyStr) {
        policyStr = utils.trim(policyStr);
        if (policyStr.startsWith("(") && policyStr.endsWith(")")) {
            String content = policyStr.substring(1, policyStr.length() - 1);
            List<String> parts = splitParts(content);
            if (parts.isEmpty()) throw new IllegalArgumentException("Invalid policy");

            // 最后一个元素是门限值
            int threshold = Integer.parseInt(parts.get(parts.size() - 1));
            PolicyNode node = new PolicyNode();
            node.threshold = threshold;
            node.children = new ArrayList<>();

            // 解析子节点
            for (int i = 0; i < parts.size() - 1; i++) {
                node.children.add(parsePolicy(parts.get(i)));
            }
            return node;
        } else {
            // 属性节点
            PolicyNode node = new PolicyNode();
            node.attribute = policyStr;
            return node;
        }
    }

    // 分割策略字符串的子部分（处理嵌套括号）
    private List<String> splitParts(String content) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int bracketDepth = 0;

        for (char c : content.toCharArray()) {
            if (c == '(') bracketDepth++;
            else if (c == ')') bracketDepth--;

            if (c == ',' && bracketDepth == 0) {
                parts.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            parts.add(current.toString().trim());
        }
        return parts;
    }

    // 递归检查策略节点是否满足
    private boolean checkPolicy(PolicyNode node, List<String> userAttrs) {
        if (node.attribute != null) {
            // 属性节点直接检查存在性
            return userAttrs.contains(node.attribute);
        } else {
            // 门限节点检查子节点满足数
            int satisfiedCount = 0;
            for (PolicyNode child : node.children) {
                if (checkPolicy(child, userAttrs)) {
                    satisfiedCount++;
                }
            }
            return satisfiedCount >= node.threshold;
        }
    }

}