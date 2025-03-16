package com.luoxi.hrabe.ABE2OD;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.luoxi.hrabe.ABE2OD.param.*;
import com.luoxi.hrabe.ABE2OD.SSH.SSHClient;
import com.luoxi.hrabe.ABE2OD.SSH.send;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class test {
    public static void main(String[] args) throws JsonProcessingException {

        // 从文件导入椭圆曲线参数
        Pairing pairing = PairingFactory.getPairing("a.properties");
        ABE2OD abe2OD=new ABE2OD();
        utils utils=new utils();

        // 初始化LSSS
        // 设置访问策略
        String access_control = "((A, B, C, 2), (D, E, F, 2), (G, H, (I, J, K, L, 3), 2), 2)";
        LSSS lsss = new LSSS(access_control);
        //lsss.showLSSS();

        //加密信息
        String m="罗皙luoxi_0123456789";
        String m_binary=utils.stringToBinary(m);
        //属性集
        String attributes="(A,B,D,E,G,H,I,J,K)";
        //String attributes="(A,B,D)";


        SetupResult setupResult = abe2OD.Setup(pairing);
        PK pk=setupResult.getPk();
        MSK msk=setupResult.getMsk();
        Ciphertext cipher = abe2OD.Enc(m_binary,lsss,pk,pairing);
        KeyTuple tuple=abe2OD.KeyGen(attributes,msk,pk,pairing);
        PTC ptc =abe2OD.transform1(tuple.getTk_1(),tuple.getTk_2(),cipher,pairing);

        //连接服务器进行transform2
        SSHClient sshClient = new SSHClient("lx", "10.242.175.231", 10021, "qymshkm#:\"ZHkcNDV6TQq");
        String url="http://localhost:5000/Transform2";

        HK hk=tuple.getHk();
        //HK hk=new HK();
        String receive_json="";
        TC recive_tc=new TC();
        try {
            if(ptc!=null) {
                //ptc.showPTC();
                String json_PTC = Serl_Deserl.serializePTC2Json(ptc);
                String json_HK = Serl_Deserl.serializeHK2Json(hk);
                String send_json=send.mergeJson(json_PTC,json_HK);
                receive_json=send.sendJson(send_json,sshClient,url);
                System.out.println("发送数据："+send_json);
                System.out.println("接收数据："+receive_json);

                byte[] bytes = send.convertBase64JsonToBytes(receive_json);
                if (bytes != null) {
                    System.out.println("transform2成功！字节流长度: " + bytes.length);
                    recive_tc=Serl_Deserl.deserl_TC(bytes,pairing);
                } else {
                    System.out.println("transform2失败！");
                }
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        //recive_tc.showTC();

        String res = abe2OD.Dec(tuple.getDk(),recive_tc,pk,pairing);

        if(res!=null) {
            System.out.println(utils.binaryToString(res));
        }

    }
}
