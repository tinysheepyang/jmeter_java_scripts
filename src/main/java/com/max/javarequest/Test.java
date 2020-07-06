package com.max.javarequest;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public class Test {

    public static void main(String[] args) {
        Arguments params = new Arguments();
        params.addArgument("reqUrl","http://www.emao.com/json/home/gethome");
        params.addArgument("city_id","1");


        JavaSamplerContext context = new JavaSamplerContext(params);

        CsyTestHttpRequest sttest = new CsyTestHttpRequest();

        sttest.setupTest(context);


        sttest.runTest(context);

        sttest.teardownTest(context);
    }

}
