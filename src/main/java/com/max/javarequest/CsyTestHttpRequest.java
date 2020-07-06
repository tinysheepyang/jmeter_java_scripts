package com.max.javarequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.IOException;

public class CsyTestHttpRequest extends AbstractJavaSamplerClient {

    private String reqUrl;
    private String city_id;


    private CloseableHttpClient httpClient;
    private CloseableHttpResponse response;
    private HttpPost httpPost;
    private HttpGet httpGet;


    /**
     * 定义可用参数及默认值
     * @return
     */
    public Arguments getDefaultParameters()
    {
        Arguments params = new Arguments();
        params.addArgument("reqUrl", "");
        params.addArgument("city_id", "");

        return params;
    }

    /**
     * 可选，测试前执行，做一些初始化工作，在Jmeter执行的时候，一个Thread只执行一次setupTest方法
     * @param context
     */
    public void setupTest(JavaSamplerContext context)
    {
        reqUrl = context.getParameter("reqUrl");
        city_id = context.getParameter("city_id");


        httpClient = HttpClientBuilder.create().build();
        httpGet = new HttpGet(reqUrl + "?city_id=" + city_id);

    }


    /**
     * 必选实现自定义请求
     * @param context
     * @return
     */
    public SampleResult runTest(JavaSamplerContext context)
    {
        SampleResult results = new SampleResult();
        results.sampleStart();
        try
        {
            String res = doGet();

            results.setResponseData(res, "utf-8");
            results.setDataType(SampleResult.TEXT);
            if("true".equals(res)){
                results.setSuccessful(true);
            }else{
                results.setSuccessful(false);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            results.setSuccessful(false);
        }
        results.sampleEnd();
        return results;
    }


    /**
     * 可选测试结束时调用，用于自然释放
     * @param context
     */
    public void teardownTest(JavaSamplerContext context)
    {
        if (httpClient != null){
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        httpGet.releaseConnection();
        super.teardownTest(context);
    }

    /**
     * send http post request
     * @param reqData requests data
     * @return response data
     */
    private String doPost(String reqData){
        String reseContent = "";
        try {
            httpPost.setEntity(new StringEntity(reqData == null ? "" : reqData, "utf-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity)
            {
                reseContent = EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
                EntityUtils.consume(entity);
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
        return reseContent;
    }


    /**
     *  send http get request
     *  return response data
     */
    private String doGet(){
        String reseContent = "";

        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (null != entity)
            {
                reseContent = EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
//                System.out.println("响应内容为:" + reseContent);
                EntityUtils.consume(entity);
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }

        return reseContent;

    }


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
