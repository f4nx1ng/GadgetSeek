package pascal.taie.util;

import java.util.List;

/**
 * CSRCMethod用于存储方法调用时的参数信息（暂时）
 * paramObjClass,用于存放方法调用时参数的声明类情况，
 * List是因为不止一个参数，
 * Set是因为该参数所指向的污点数据可能不止一个，来源额可能是多个，需要去重
 *
 * */
public class CSRCMethod {
    List<String> paramAssociation;

    String recvAssociation;


    public CSRCMethod(List<String> paramAssociation){
        this.paramAssociation = paramAssociation;
    }

    public List<String> getParamAssociation() {
        return paramAssociation;
    }

    public String getRecvAssociation(){
        return recvAssociation;
    }

    public void setRecvAssociation(String recvAssociation) {
        this.recvAssociation = recvAssociation;
    }
}
