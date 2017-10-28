package com.github.lit.jdbc.generator;

/**
 * User : liulu
 * Date : 2017-3-5 22:46
 * version $Id: SequenceGenerator.java, v 0.1 Exp $
 */
public class SequenceGenerator implements KeyGenerator {

    @Override
    public boolean isGenerateBySql() {
        return true;
    }

    @Override
    public String generateKey(String dbName) {
        return "";
    }

    public String generateKey(String dbName, String seqName) {
        switch (dbName) {
            case "DB2":
                return "next value for " + seqName;
            case "ORACLE":
                return seqName + ".nextval";
            default:
                return "";
        }
    }

}
