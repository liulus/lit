package com.github.lit.code.context;

import com.github.lit.code.util.NameUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * User : liulu
 * Date : 2018/2/7 17:02
 * version $Id: Task.java, v 0.1 Exp $
 */
@Getter
@Setter
@ToString
public class Task implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Task.class.getName());

    private static final long serialVersionUID = 5454351511354251547L;

    private String name;

    private String tableName;

    private String generateClass;

    private String plugin;

    private String template;

    private String prefixRemove = "";

    private String suffixRemove = "";

    private String delimiter = "_";

    private String prefixAdd = "";

    private String suffixAdd = "";

    private String fileType = ".java";

    private Boolean upperCase = false;

    private Boolean lowerCase = false;

    private Boolean camel = false;

    private String fileSeparator = "";

    private String module = "";

    private String srcDir = "src/main/java";

    private String _package = "";


    public String getShortClassName() {
        String resultName = NameUtils.getFirstUpperName(NameUtils.getCamelName(tableName, delimiter));
        return prefixAdd + resultName + suffixAdd;
    }

    public String getLongClassName() {
        return _package + "." + getShortClassName();
    }

    public String getFirstLowerClassName() {
        if (prefixAdd == null || prefixAdd.isEmpty()) {
            return NameUtils.getCamelName(tableName) + suffixAdd;
        }
        return NameUtils.getFirstLowerName(getShortClassName());
    }

    public String getUnderLineSplitName() {
        return NameUtils.getLowerDelimiterName(getShortClassName(), "_");
    }

    public String getMiddleLineSplitName() {
        return NameUtils.getLowerDelimiterName(getShortClassName(), "-");
    }

    public String getPathSplitName() {
        return NameUtils.getLowerDelimiterName(getShortClassName(), "/");
    }

    public String getUpperName() {
        return getShortClassName().toUpperCase();
    }

    public String getLowerName() {
        return getShortClassName().toLowerCase();
    }


    public String processTableName(String tableName) {
        if (prefixRemove != null && !prefixRemove.isEmpty()) {
            if (tableName.startsWith(prefixRemove)) {
                tableName = tableName.substring(prefixRemove.length());
            } else {
                LOGGER.warning(String.format("table %s 设置移除的前缀 %s 无效!", tableName, prefixRemove));
            }
        }
        if (suffixRemove != null && !suffixRemove.isEmpty()) {
            if (tableName.endsWith(suffixRemove)) {
                tableName = tableName.substring(0, tableName.length() - suffixRemove.length());
            }
        }
        if (tableName.startsWith(delimiter)) {
            tableName = tableName.substring(delimiter.length());
        }
        if (tableName.endsWith(delimiter)) {
            tableName = tableName.substring(0, tableName.length() - delimiter.length());
        }
        return tableName;
    }


    public String getFileName() {

        String resultName = null;

        if (camel) {
            resultName = getShortClassName();
        } else if (upperCase) {
            resultName = prefixAdd + (tableName.replace(delimiter, fileSeparator)).toUpperCase() + suffixAdd;
        } else if (lowerCase) {
            resultName = prefixAdd + (tableName.replace(delimiter, fileSeparator)).toLowerCase() + suffixAdd;
        }

        // 默认设置为首字母大写驼峰
        if (resultName == null) {
            resultName = getShortClassName();
        }

        String longName = (get_package() + "." + resultName).replace(".", File.separator);
        if (!srcDir.startsWith(File.separator)) {
            srcDir = File.separator + srcDir;
        }
        if (!srcDir.endsWith(File.separator)) {
            srcDir = srcDir + File.separator;
        }
        if (!fileType.startsWith(".")) {
            fileType = "." + fileType;
        }
        return srcDir + longName + fileType;
    }

    public String getPackageName() {
        return _package;
    }
}
