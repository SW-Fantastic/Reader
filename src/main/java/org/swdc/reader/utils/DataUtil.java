package org.swdc.reader.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lenovo on 2019/5/22.
 */
public class DataUtil {

    /**
     * 计算文件的sha256摘要
     * @param file
     * @return
     */
    public static String getFileShaCode(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            DataInputStream din = new DataInputStream(new FileInputStream(file));
            byte[] data = new byte[2048];
            ByteArrayOutputStream bot = new ByteArrayOutputStream();
            while (din.read(data) > -1) {
                bot.write(data);
            }
            bot.flush();
            din.close();
            byte[] fileData = bot.toByteArray();
            digest.update(fileData);
            return bytesToHexString(digest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字节数组转换成16进制字符串
     * @param bytes 即将转换的数据
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0;i< bytes.length;i++){
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() <2){
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    public static String getPrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        double value = (double) size;
        if (value < 1024) {
            return String.valueOf(value) + "B";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (value < 1024) {
            return String.valueOf(value) + "KB";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        if (value < 1024) {
            return String.valueOf(value) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return String.valueOf(value) + "GB";
        }
    }

    public static String resolveRelativePath(String base, String target) {
        // 获取base的基目录
        String[] baseParts = base.split("/");
        String baseDir = base;
        if (baseParts[baseParts.length - 1].split("[.]").length > 1  && baseParts.length > 1) {
            baseDir = base.substring(0, base.lastIndexOf("/"));
        } else {
            return target;
        }
        // 拼接
        String targetPath = baseDir + "/" + target;
        List<String> targetParts = Arrays.asList(targetPath.split("/"));
        LinkedList<String> result = new LinkedList<>();
        for (int idx = 0; idx < targetParts.size(); idx ++) {
            String part = targetParts.get(idx);
            if (part.equals("..")) {
                 result.remove(idx - 1);
                continue;
            }
            result.add(part);
        }
        return result.stream().reduce((partA, partB) -> partA + "/" + partB).orElse("");
    }


}
