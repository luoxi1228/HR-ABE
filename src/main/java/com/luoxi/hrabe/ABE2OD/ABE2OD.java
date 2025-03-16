package com.luoxi.hrabe.ABE2OD;

import com.luoxi.hrabe.ABE2OD.param.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;
import java.util.List;

public class ABE2OD {

    public SetupResult Setup(Pairing pairing) {

        PK pk=new PK();
        MSK msk=new MSK();

        // 获取不同群的Field
        Field G1 = pairing.getG1();
        Field GT = pairing.getGT();
        Field Zr = pairing.getZr();

        // 生成随机数 - 直接设置为不可变
        Element a = Zr.newRandomElement().getImmutable();
        Element alpha = Zr.newRandomElement().getImmutable();

        // 生成公钥 - g设置为不可变
        Element g = G1.newRandomElement().getImmutable();

        // 计算ga - 使用不可变的g计算，结果设置为不可变
        Element ga = g.powZn(a).getImmutable();

        // 计算e(g,g)^alpha - 使用不可变的g计算，结果设置为不可变
        Element eggalpha = pairing.pairing(g, g).powZn(alpha).getImmutable();

        // 生成主密钥 - 使用不可变的g计算，结果设置为不可变
        Element galpha = g.powZn(alpha).getImmutable();

        pk.setG(g);
        pk.setGa(ga);
        pk.setEggalpha(eggalpha);
        msk.setGalpha(galpha);

        return new SetupResult(pk, msk);
    }

    public Ciphertext Enc(String m,LSSS lsss, PK pk,Pairing pairing) {

        Ciphertext cipher =new Ciphertext();

        int lsss_row=lsss.getM().size();
        int lsss_col=lsss.getM().get(0).size();
        cipher.setPolicy(lsss);

        //生成随机元
        Element R = pairing.getGT().newRandomElement();

        // El Gamal layer
        // 计算C1
        String hash_R =utils.elementToHash(R);
        String xorResult = utils.xorHashes(hash_R, m);
        cipher.setC1(xorResult);

        //计算C0
        Element s = pairing.getZr().newElement();
        s.setFromHash(xorResult.getBytes(), 0, xorResult.length());
        Element C0 = pk.getEggalpha().duplicate().powZn(s);  // e(g,g)^(αs)
        cipher.setC0(C0.mul(R));  // R * e(g,g)^(αs)

        // 计算C2
        Element C2 = pk.getG().duplicate().powZn(s);  // g^s
        cipher.setC2(C2);

        //随机元 v和r
        List<Element> v = new ArrayList<>();
        v.add(s);  // v[0] = s
        // 生成v的其余元素
        for (int i = 0; i < lsss_col - 1; i++) {
            Element temp_y = pairing.getZr().newRandomElement();
            v.add(temp_y);
        }
        // 生成随机向量r
        List<Element> r = new ArrayList<>();
        for (int i = 0; i < lsss_row; i++) {
            Element tmp = pairing.getZr().newRandomElement();
            r.add(tmp);
        }
        // 访问策略层
        // 生成共享份额
        cipher.setLambda(lsss.generateShares(v,pairing));
        assert cipher.getLambda().size() == lsss.getM().size();
        // 计算Di和Ei
        for (int i = 0; i < r.size(); i++) {
            Element di = pairing.getG1().newElement();
            Element ei = pairing.getG1().newElement();

            // 计算di = g^(r[i])
            di = pk.getG().duplicate().powZn(r.get(i));

            // 计算ei = (g^a)^(lambda[i]) * H(rho[i])^(-r[i])
            ei = pk.getGa().duplicate().powZn(cipher.getLambda().get(i));

            Element hashLabel = pairing.getG1().newElement();
            Element rInv = r.get(i).duplicate().negate();

            // 计算属性的哈希值
            hashLabel.setFromHash(lsss.getRho().get(i).getBytes(),0, lsss.getRho().get(i).length());

            hashLabel.powZn(rInv);
            ei.mul(hashLabel);

            // 将di和ei添加到密文中
            cipher.getDi().add(di);
            cipher.getEi().add(ei);
        }
        return cipher;
    }

    public KeyTuple KeyGen(String attributes, MSK msk, PK pk, Pairing pairing) {

        KeyTuple keyTuple =new KeyTuple();
        // 设置属性
        keyTuple.getTk_1().setAttributes(attributes);
        keyTuple.getTk_2().setAttributes(attributes);
        List<String> attributeList = utils.string2attributeSet(attributes);

        //System.out.println("请求者的属性："+attributeList);

        // 初始化随机元素，确保不为 0
        Element t1 = pairing.getZr().newRandomElement();
        while (t1.isZero()) { t1 = pairing.getZr().newRandomElement();}
        t1 = t1.getImmutable();

        Element t2 = pairing.getZr().newRandomElement();
        while (t2.isZero()) { t2 = pairing.getZr().newRandomElement(); }
        t2 = t2.getImmutable();

        // 生成解密密钥
        keyTuple.getDk().setBeta(pairing.getZr().newRandomElement().getImmutable());

        // 生成撤销密钥
        keyTuple.getHk().setGamma_1(pairing.getZr().newRandomElement().getImmutable());
        keyTuple.getHk().setGamma_2(pairing.getZr().newRandomElement().getImmutable());

        // 生成转换密钥1 (tk1)
        // 计算 betagammainv1 = 1/(beta * gamma1)
        Element betagammainv1 = keyTuple.getDk().getBeta().duplicate()
                .mul(keyTuple.getHk().getGamma_1());

        if (betagammainv1.isZero()) {
            throw new IllegalArgumentException("betagammainv1 is zero, cannot invert.");
        }

        betagammainv1 = betagammainv1.invert().getImmutable();

        // 计算 K1 = g^(alpha * betagammainv1) * (g^a)^(t1 * betagammainv1)
        Element tempK1_1 = msk.getGalpha().duplicate()
                .powZn(betagammainv1)
                .getImmutable();

        Element tempK1_2 = pk.getGa().duplicate()
                .powZn(betagammainv1.duplicate().mul(t1))
                .getImmutable();

        Element K1 = tempK1_1.mul(tempK1_2).getImmutable();
        keyTuple.getTk_1().setK(K1);

        // 计算 L1 = g^(betagammainv1 * t1)
        keyTuple.getTk_1().setL(pk.getG().duplicate()
                .powZn(betagammainv1.duplicate().mul(t1))
                .getImmutable());

        // 生成转换密钥2 (tk2)
        // 计算 betagammainv2 = 1/(beta * gamma2)
        Element betagammainv2 = keyTuple.getDk().getBeta().duplicate()
                .mul(keyTuple.getHk().getGamma_2());

        if (betagammainv2.isZero()) {
            throw new IllegalArgumentException("betagammainv2 is zero, cannot invert.");
        }

        betagammainv2 = betagammainv2.invert().getImmutable();

        // 计算 K2 = g^(alpha * betagammainv2) * (g^a)^(t2 * betagammainv2)
        Element tempK2_1 = msk.getGalpha().duplicate()
                .powZn(betagammainv2)
                .getImmutable();

        Element tempK2_2 = pk.getGa().duplicate()
                .powZn(betagammainv2.duplicate().mul(t2))
                .getImmutable();

        Element K2 = tempK2_1.mul(tempK2_2).getImmutable();
        keyTuple.getTk_2().setK(K2);

        // 计算 L2 = g^(betagammainv2 * t2)
        keyTuple.getTk_2().setL(pk.getG().duplicate()
                .powZn(betagammainv2.duplicate().mul(t2))
                .getImmutable());

        // 生成属性密钥
        for (String attr : attributeList) {
            // 从属性字符串生成哈希值，并计算 Ky=H(y)^(t * betagammainv)
            Element tempKy1 = pairing.getG1().newElement().setFromHash(attr.getBytes(), 0, attr.length())
                    .powZn(betagammainv1.duplicate().mul(t1))
                    .getImmutable();

            Element tempKy2 = pairing.getG1().newElement().setFromHash(attr.getBytes(), 0, attr.length())
                    .powZn(betagammainv2.duplicate().mul(t2))
                    .getImmutable();

            // 存储属性密钥
            keyTuple.getTk_1().getKy().put(attr, tempKy1);
            keyTuple.getTk_2().getKy().put(attr, tempKy2);
        }
        return keyTuple;
    }

    public PTC transform1( TK tk1, TK tk2, Ciphertext cipher, Pairing pairing) {
        PTC ptc=new PTC();
        String userAttributes=tk1.getAttributes();
        LSSS lsss=cipher.getPolicy();
        boolean flag=lsss.isSatisfy(userAttributes);//判断用户的属性是否满足访问策略
        if(!flag) {
            System.out.println("The access policy is not satisfied, transform1 failed!");
            return null;
        }

        //C0
        ptc.setC0(pairing.getGT().newElement().set(cipher.getC0()).getImmutable());

        //C1
        ptc.setC1(cipher.getC1());

        //e(C2, tk_1.K)、e(C2, tk_2.K)
        Element numerator1 = pairing.pairing(cipher.getC2(), tk1.getK());
        Element numerator2 = pairing.pairing(cipher.getC2(), tk2.getK());

        // 获取有效共享属性
        List<Element> lambda = new ArrayList<>();
        List<Element> Ei = new ArrayList<>();
        List<Element> Di = new ArrayList<>();
        List<Element> ky1 = new ArrayList<>();
        List<Element> ky2 = new ArrayList<>();

        // 调用策略验证方法
        cipher.getPolicy().getValidSharesExt(lambda, Ei, Di, ky1,
                cipher.getLambda(), cipher.getEi(), cipher.getDi(),
                tk1.getKy(), tk1.getAttributes(), pairing);

        cipher.getPolicy().getValidSharesExt(lambda, Ei, Di, ky2,
                cipher.getLambda(), cipher.getEi(), cipher.getDi(),
                tk2.getKy(), tk2.getAttributes(), pairing);

        // 查找权重向量
        List<Element> ws1 = new ArrayList<>();
        List<Element> ws2 = new ArrayList<>();
        cipher.getPolicy().findVector(ws1, tk1.getAttributes(), pairing);
        cipher.getPolicy().findVector(ws2, tk2.getAttributes(), pairing);

        // 初始化分母为GT群的单位元
        Element denominator1 = pairing.getGT().newOneElement();
        Element denominator2 = pairing.getGT().newOneElement();

        // 遍历权重向量进行计算
        for (int i = 0; i < ws1.size(); i++) {
            // CP1部分计算
            Element temp1 = pairing.pairing(Ei.get(i), tk1.getL())
                    .mul(pairing.pairing(Di.get(i), ky1.get(i)))
                    .powZn(ws1.get(i));
            denominator1.mul(temp1);

            // CP2部分计算
            Element temp2 = pairing.pairing(Ei.get(i), tk2.getL())
                    .mul(pairing.pairing(Di.get(i), ky2.get(i)))
                    .powZn(ws2.get(i));
            denominator2.mul(temp2);
        }

        // 计算最终CP值
        ptc.setCP1((numerator1).div(denominator1).getImmutable());
        ptc.setCP2((numerator2).div(denominator2).getImmutable());
        return ptc;
    }

    public TC Transform2(HK hk,PTC ptc,Pairing pairing) {
        TC tc = new TC();

        if(ptc.getC0()==null || ptc.getC1()==null || ptc.getCP1()==null || ptc.getCP2()==null) {//说明Transform1中的属性集不满足
            System.out.println("Conditions not met,transform2 failed!");
            return null;
        }
       Element temp1 = ptc.getCP1().duplicate().powZn(hk.getGamma_1());
       Element temp2 = ptc.getCP2().duplicate().powZn(hk.getGamma_2());

       if(temp1.isEqual(temp2)) {
           tc.setT0(ptc.getC0());
           tc.setT1(ptc.getC1());
           tc.setT2(temp1);
       }else {
           System.out.println("Conditions not met,transform2 failed!");
       }
     return tc;
    }

    public String Dec(DK dk,TC tc,PK pk,Pairing pairing) {
        if(tc.getT0()==null ||tc.getT1()==null ||tc.getT2()==null) { //说明Transform2中不满足条件
            System.out.println("Dec Failed!");
            return null;
        }
        //R=T/(T”)^beta
        Element temp1 = tc.getT2().duplicate().powZn(dk.getBeta()).invert();
        Element R=tc.getT0().duplicate().mul(temp1).getImmutable();

        //M=H(R)xor(T')
        String hash_R = utils.elementToHash(R);
        String M = utils.xorHashes(hash_R,tc.getT1()); //运算结果为二进制字符串
        //System.out.println(M);

        //s=H(R)xorM
        String str_s = utils.xorHashes(hash_R,M);
        Element s = pairing.getZr().newElement();
        s.setFromHash(str_s.getBytes(), 0, str_s.length());

        //R·e(g,g)^(alpha·s)
        Element result1 = pk.getEggalpha().duplicate().powZn(s).duplicate().mul(R);
        //e(g,g)^(alpha·s)
        Element result2 = pk.getEggalpha().duplicate().powZn(s);
        //(T")^beta
        Element result3 = tc.getT2().duplicate().powZn(dk.getBeta());

        if (tc.getT0().isEqual(result1) && result2.isEqual(result3))
        {
            System.out.println("Dec SUCCESS!");
            return M;
        }else {
            System.out.println("Dec Failed!");
            return null;
        }
    }

}
