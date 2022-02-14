package com.stonedt.intelligence.util;


import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Created by Yangyang Deng on 17-9-7.
 */

public class SimhashAlgoService {

    public static void main(String[] args) {
        SimhashAlgoService simhashAlgoService = new SimhashAlgoService();
        String s1 = "乐视网因财务造假我亲亲亲亲亲亲亲亲亲亲等 公司及贾跃亭各被罚2.4亿元";
        String s2 = "财务造假十年，乐视网实控人贾跃亭被罚2.412亿元";
        List<String> fingerPrints1 = simhashAlgoService.simHash(s1,64);
        System.out.println("fingerPrints1:"+fingerPrints1);
        SimhashAlgoService simhashAlgoService2 = new SimhashAlgoService();
        List<String> fingerPrints2 = simhashAlgoService2.simHash(s2,64);
        System.out.println("fingerPrints2:"+fingerPrints2);
        int x = simhashAlgoService.hammingDistance(simhashAlgoService2);
        System.out.println("海明距离:" + x);
    }


    private StandardTokenizer hanlpService;

    // 待分词的文本
    private String tokens;

    // 十进制的指纹
    private BigInteger intSimHash;

    // 二进制的指纹
    private String strSimHash;

    // 二进制指纹的4个子指纹
    private String strSimHashA;
    private String strSimHashB;
    private String strSimHashC;
    private String strSimHashD;

    private Map<String,Integer> wordCount;

    private int overCount = 5;

    public BigInteger getIntSimHash(){
        return this.intSimHash;
    }

    public String getStrSimHash() {
        return this.strSimHash;
    }

    private String getStrSimHashA() {
        return this.strSimHashA;
    }

    private String getStrSimHashB() {
        return this.strSimHashB;
    }

    private String getStrSimHashC() {
        return this.strSimHashC;
    }

    private String getStrSimHashD() {
        return this.strSimHashD;
    }

    // 指纹的长度
    private int hashbits = 64;

    // 停用的词性
    private Map<String,String> stopNatures = new HashMap<String, String>();

    // 词性的权重
    private Map<String, Integer> weightOfNature = new HashMap<String, Integer>();


    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public void setHashbits(int hashbits) {
        this.hashbits = hashbits;
    }

    private void setMap() {
        // 停用词性为w:标点
        this.stopNatures.put("w","");
        // 个性化设置词性权重，这里将n：名词设置为2。（默认权重为1）
        this.weightOfNature.put("n",2);
    }

    private String preProcess(String content) {
        // 若输入为HTML,下面会过滤掉所有的HTML的tag
        content = Jsoup.clean(content, Whitelist.none());
        content = StringUtils.lowerCase(content);
        String[] strings = {" ","\n","\\r","\\n","\\t","&nbsp;"};
        for (String s:strings) {
            content = content.replace(s,"");
        }
        return content;
    }

    public List<String> simHash(String tokens, int hashbits) {
        tokens = preProcess(tokens);
        // cleanResume 删除简历固有文字
        this.tokens = cleanResume(tokens);
        this.hashbits = hashbits;
        this.wordCount = new HashMap<String, Integer>();
        setMap();

        // 定义特征向量/数组
        int[] v = new int[this.hashbits];
        // 1、将文本去掉格式后, 分词.
        List<Term> termList = StandardTokenizer.segment(this.tokens);
        for (Term term:termList){
            String word = term.word;
            String nature = term.nature.toString();
//             过滤超频词
            if (this.wordCount.containsKey(word)) {
                int count = this.wordCount.get(word);
                if (count>this.overCount) {continue;}
                this.wordCount.put(word,count+1);
            }
            else {
                this.wordCount.put(word,1);
            }

            // 过滤停用词性
            if (this.stopNatures.containsKey(nature)) {continue;}
            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                int weight = 1;
                if (this.weightOfNature.containsKey(nature)) {
                    weight = this.weightOfNature.get(nature);
                }
                if (t.and(bitmask).signum() != 0) {
                    // 这里是计算整个文档的所有特征的向量和
                    v[i] += weight;
                } else {
                    v[i] -= weight;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            // 4、最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            } else {
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
        this.strSimHashA = simHashBuffer.substring(0,16);
        this.strSimHashB = simHashBuffer.substring(16,32);
        this.strSimHashC = simHashBuffer.substring(32,48);
        this.strSimHashD = simHashBuffer.substring(48,64);

        this.intSimHash = fingerprint;
        List<String> simHashList = new ArrayList<String>();
        simHashList.add(this.getStrSimHashA());
        simHashList.add(this.getStrSimHashB());
        simHashList.add(this.getStrSimHashC());
        simHashList.add(this.getStrSimHashD());
        return simHashList;
    }



    private BigInteger hash(String source) {

        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            /**
             * 当sourece 的长度过短，会导致hash算法失效，因此需要对过短的词补偿
             */
            while (source.length()<3) {
                source = source+source.charAt(0);
            }
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    // 用于计算十进制的hamming距离
    public int hammingDistance(SimhashAlgoService other) {

        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;

        // 统计x中二进制位数为1的个数
        // 我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，对吧，然后，n&(n-1)就相当于把后面的数字清0，
        // 我们看n能做多少次这样的操作就OK了。

        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }


    // 用于计算二进制的hamming距离
    public int getDistance(String str1, String str2) {
        int distance;
        if (str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    public List subByDistance(SimhashAlgoService Simhash, int distance) {
        // 分成几组来检查
        int numEach = this.hashbits / (distance + 1);
        List characters = new ArrayList();

        StringBuffer buffer = new StringBuffer();

        int k = 0;
        for (int i = 0; i < this.intSimHash.bitLength(); i++) {
            // 当且仅当设置了指定的位时，返回 true
            boolean sr = Simhash.intSimHash.testBit(i);

            if (sr) {
                buffer.append("1");
            } else {
                buffer.append("0");
            }

            if ((i + 1) % numEach == 0) {
                // 将二进制转为BigInteger
                BigInteger eachValue = new BigInteger(buffer.toString(), 2);
                System.out.println("----" + eachValue);
                buffer.delete(0, buffer.length());
                characters.add(eachValue);
            }
        }

        return characters;
    }

    // 过滤无关内容
    private String cleanResume(String content) {
        String[] tobeReplace = {
                "\n","\r","\t","\\n","\\r","\\t"
        };

        for (String s:tobeReplace) {
            content = content.replace(s,"");
        }


        return content;
    }



}


