package com.luoxi.hrabe.ABE2OD.param;

import com.luoxi.hrabe.ABE2OD.LSSS;
import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 密文
 * */

public class Ciphertext {
    private LSSS policy;
    private Element C0;         // GT群元素
    private String C1;         // 哈希值
    private Element C2;        // G1群元素
    private List<Element> Di;  // G1群元素列表
    private List<Element> Ei;  // G1群元素列表
    private List<Element> lambda;  // Zr群元素列表

    public Ciphertext() {
        this.Di = new ArrayList<>();
        this.Ei = new ArrayList<>();
        this.lambda = new ArrayList<>();
        this.policy = new LSSS();
    }

    public LSSS getPolicy() {
        return policy;
    }

    public void setPolicy(LSSS policy) {
        this.policy = policy;
    }

    public Element getC0() {
        return C0;
    }

    public void setC0(Element c0) {
        C0 = c0;
    }

    public String getC1() {
        return C1;
    }

    public void setC1(String c1) {
        C1 = c1;
    }

    public Element getC2() {
        return C2;
    }

    public void setC2(Element c2) {
        C2 = c2;
    }

    public List<Element> getDi() {
        return Di;
    }

    public void setDi(List<Element> di) {
        Di = di;
    }

    public List<Element> getEi() {
        return Ei;
    }

    public void setEi(List<Element> ei) {
        Ei = ei;
    }

    public List<Element> getLambda() {
        return lambda;
    }

    public void setLambda(List<Element> lambda) {
        this.lambda = lambda;
    }

    public void showCipher() {
        System.out.println("--Ciphertext--");

        // 打印访问策略
        System.out.println("Policy: " + (this.policy != null ? this.policy.getAccessPolicy() : "null"));

        // 打印加密元素
        System.out.println("C0: " + (this.C0 != null ? this.C0.toString() : "null"));
        System.out.println("C1 (Hash): " + (this.C1 != null ? this.C1 : "null"));
        System.out.println("C2: " + (this.C2 != null ? this.C2.toString() : "null"));

        // 打印 Di 列表
        System.out.println("Di List:");
        if (Di.isEmpty()) {
            System.out.println("  Empty");
        } else {
            for (int i = 0; i < Di.size(); i++) {
                System.out.println("  Di[" + i + "]: " + Di.get(i));
            }
        }

        // 打印 Ei 列表
        System.out.println("Ei List:");
        if (Ei.isEmpty()) {
            System.out.println("  Empty");
        } else {
            for (int i = 0; i < Ei.size(); i++) {
                System.out.println("  Ei[" + i + "]: " + Ei.get(i));
            }
        }

        // 打印 lambda 列表
        System.out.println("Lambda List:");
        if (lambda.isEmpty()) {
            System.out.println("  Empty");
        } else {
            for (int i = 0; i < lambda.size(); i++) {
                System.out.println("  lambda[" + i + "]: " + lambda.get(i));
            }
        }
    }

}
