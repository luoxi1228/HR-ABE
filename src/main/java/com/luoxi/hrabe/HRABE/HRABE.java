package com.luoxi.hrabe.HRABE;

import ch.qos.logback.core.pattern.ConverterUtil;
import com.luoxi.hrabe.ABE2OD.ABE2OD;
import com.luoxi.hrabe.ABE2OD.LSSS;
import com.luoxi.hrabe.ABE2OD.SSH.SSHClient;
import com.luoxi.hrabe.ABE2OD.SSH.send;
import com.luoxi.hrabe.ABE2OD.Serl_Deserl;
import com.luoxi.hrabe.ABE2OD.param.*;
import com.luoxi.hrabe.ABE2OD.utils;
import com.luoxi.hrabe.HRABE.param.*;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.*;

public class HRABE {
    public static SetupResult_h Setup_h(Pairing pairing) throws Exception {

        ABE2OD abe2OD = new ABE2OD();

        SetupResult result = abe2OD.Setup(pairing);
        MSK msk = result.getMsk();
        PK pk = result.getPk();

        Map<String, String> keyMap = RsaUtil.generateKey();
        String vk = keyMap.get("publicKeyStr");
        String sk = keyMap.get("privateKeyStr");

        MPK_h mpk_h = new MPK_h(pk, vk);
        MSK_h msk_h = new MSK_h(msk, sk);


        List<UL> ulList = new ArrayList<>();
        ST st = Update(msk_h,ulList,"0",pairing);

        return new SetupResult_h(mpk_h, msk_h, st, ulList);
    }

    public static Ciphertext Enc_h(MPK_h mpk_h, String m, LSSS lsss, Pairing pairing) {
        ABE2OD abe2OD = new ABE2OD();
        return abe2OD.Enc(m, lsss, mpk_h.getPk(), pairing);
    }

    public static JoinResult Join(MPK_h mpk_h, MSK_h msk_h, List<UL> ul_list, String ID, String A, Pairing pairing) {
        ABE2OD abe2OD = new ABE2OD();

        KeyTuple keyTuple = abe2OD.KeyGen(A, msk_h.getMsk(), mpk_h.getPk(), pairing);
        TK tk1 = keyTuple.getTk_1();
        TK tk2 = keyTuple.getTk_2();
        HK hk = keyTuple.getHk();
        DK dk = keyTuple.getDk();

        UL ul = new UL(ID, A, tk1, tk2, hk);
        ul_list.add(ul);
        return new JoinResult(ul_list, tk1, tk2, hk, dk);
    }

    public static List<UL> Rev(List<UL> ul_list, String ID) {
        ul_list.removeIf(ul -> ul.getID().equals(ID));
        return ul_list;
    }

    public static ST Update(MSK_h msk_h, List<UL> ul_list, String index, Pairing pairing) throws Exception {
        String sk = msk_h.getSk(); // 获取 MSK_h 的密钥

        // 序列化 UL 列表和索引
        byte[] serializedData = Serl_Deserl.serializeULListAndIndex(ul_list, index, pairing);

        // 签名
        String sign = RsaUtil.sign(serializedData, Base64.getDecoder().decode(sk), "RSA");

        // 深拷贝 ul_list
        // 假设 UL 类有一个拷贝构造函数
        List<UL> copiedUlList = new ArrayList<>(ul_list);

        // 返回新的 ST 实例
        return new ST(sign, copiedUlList, index);
    }


    public static PTC Transform1_h(TK tk1,TK tk2,ST st,String ID,Ciphertext ciphertext,MPK_h mpk_h,Pairing pairing) throws Exception {

        String index=st.getIndex();
        List<UL>ul_list=st.getUl();
        String sign=st.getSign();
        String vk=mpk_h.getVk();

        ABE2OD abe2OD = new ABE2OD();

        // 序列化 UL 列表和索引
        byte[] serializedData = Serl_Deserl.serializeULListAndIndex(ul_list, index, pairing);

        //验签正确返回1
        boolean b1 = RsaUtil.verify(serializedData, Base64.getDecoder().decode(sign), Base64.getDecoder().decode(vk), "RSA");
        boolean b2 =false;

        for(UL ul:ul_list){
            if (ul.getID().equals(ID)) {
                b2 = true;
                break;
            }
        }
        if(b1 && b2){
            return abe2OD.transform1(tk1,tk2,ciphertext,pairing);
        }
        System.out.println("用户已经被撤销了");
        return null;
    }

    public static TC Transform2_h(PTC ptc,HK hk,Pairing pairing) throws Exception {
        //连接服务器进行transform2
        SSHClient sshClient = new SSHClient("lx", "10.242.175.231", 10021, "qymshkm#:\"ZHkcNDV6TQq");
        String url="http://localhost:5000/Transform2";
        String json_PTC = Serl_Deserl.serializePTC2Json(ptc);
        String json_HK = Serl_Deserl.serializeHK2Json(hk);

        String send_json= send.mergeJson(json_PTC,json_HK);
        String receive_json=send.sendJson(send_json,sshClient,url);
        System.out.println("发送数据："+send_json);
        System.out.println("接收数据："+receive_json);

        byte[] bytes = send.convertBase64JsonToBytes(receive_json);
        if (bytes != null) {
            System.out.println("transform2成功！字节流长度: " + bytes.length);
            return Serl_Deserl.deserl_TC(bytes,pairing);

        } else {
            System.out.println("transform2失败！");
            return null;
        }
    }

    public static String Dec_h(DK dk,TC tc,MPK_h mpk_h,Pairing pairing) throws Exception {
        ABE2OD abe2OD = new ABE2OD();
        return utils.binaryToString(abe2OD.Dec(dk,tc,mpk_h.getPk(),pairing)).replaceAll("[\\p{C}\\p{Z}]", "").trim();
    }

}
