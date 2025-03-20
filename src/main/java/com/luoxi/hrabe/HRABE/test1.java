package com.luoxi.hrabe.HRABE;

import com.luoxi.hrabe.ABE2OD.LSSS;
import com.luoxi.hrabe.ABE2OD.param.*;
import com.luoxi.hrabe.ABE2OD.utils;
import com.luoxi.hrabe.HRABE.param.*;
import com.luoxi.hrabe.Util.CiphertextSerializer;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.List;

public class test1 {
    public static void main(String[] args) throws Exception {

        /*Pairing pairing = PairingFactory.getPairing("a.properties");

        SetupResult_h setupResult=HRABE.Setup_h(pairing);
        //setupResult.show();

        //初始化的参数
        MPK_h mpk_h=setupResult.getMpk_h();
        MSK_h msk_h=setupResult.getMsk_h();

        List<UL> ulList=setupResult.getUl_h();//用户列表
        ST st0=setupResult.getSt_h(); //初始状态
        List<ST> stList=new ArrayList<ST>();//状态列表
        //stList.add(st0);

        // 初始化LSSS
        // 设置访问策略
        String access_control = "((A, B, C, 2), (D, E, F, 2), (G, H, (I, J, K, L, 3), 2), 2)";
        LSSS lsss = new LSSS(access_control);
        //lsss.showLSSS();

        //加密信息
        String m="罗皙luoxi_0123456789";
        String m_binary= utils.stringToBinary(m);
        //属性集
        String attributes1="(A,B,D,E,G,H,I,J,K)";
        String attributes2="(A,B,D,E,G)";

        //加密
        Ciphertext ciphertext=HRABE.Enc_h(mpk_h,m_binary,lsss,pairing);
        ciphertext.showCipher();

        String c1= CiphertextSerializer.Ciphertext2String(ciphertext);
        System.out.println(c1);
        Ciphertext c2=CiphertextSerializer.String2Ciphertext(c1);
        String c3= CiphertextSerializer.Ciphertext2String(c2);
        if (c1.equals(c3)) {
            System.out.println("ciphertext 序列化正确");
        }


        //注册新的DU1
        String ID1="001";
        JoinResult JR1=HRABE.Join(mpk_h,msk_h,ulList,ID1,attributes1,pairing);
        //更新状态1
        ST st1=HRABE.Update(msk_h,ulList,"1",pairing);
        stList.add(st1);

        //注册新的DU2
        String ID2="002";
        JoinResult JR2=HRABE.Join(mpk_h,msk_h,ulList,ID2,attributes2,pairing);
        //更新状态2
        ST st2=HRABE.Update(msk_h,ulList,"2",pairing);
        stList.add(st2);

        //撤销用户
        ulList = HRABE.Rev(ulList,ID2);
        //更新状态3
        ST st3=HRABE.Update(msk_h,ulList,"3",pairing);
        stList.add(st3);


        //查看状态列表和用户列表
        System.out.println("============================状态列表==========================");
        for (ST st : stList) {
            st.showST();
        }
        System.out.println();
        System.out.println("============================用户列表==========================");
        for(int i=0;i<ulList.size();i++){
            System.out.println("---------------用户"+(i+1)+"------------------");
            ulList.get(i).showUL();
        }


        //解密
        //transdorm1
        PTC ptc=HRABE.Transform1_h(JR1.getTk1(),JR1.getTk2(),st3,ID1,ciphertext,mpk_h,pairing);
        //PTC ptc=HRABE.Transform1_h(JR2.getTk1(),JR2.getTk2(),st3,ID2,ciphertext,mpk_h,pairing);
        if(ptc!=null){
            ptc.showPTC();
        }else {
            System.out.println("用户不存在于UL");
        }
        //transform2
        TC tc=HRABE.Transform2_h(ptc,JR1.getHk(),pairing);
        if(tc!=null){
            tc.showTC();
        }
        //Dec
        String message=HRABE.Dec_h(JR1.getDk(),tc,mpk_h,pairing);
        System.out.println(message);*/

        System.out.println("    123456");
    }
}
