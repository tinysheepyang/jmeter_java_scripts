package com.max.javarequest;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * Rocket mq send message
 */

public class CsyRocketMQRequest extends AbstractJavaSamplerClient {

    private String NameServer;

    private String Topic;
    private String GroupName;
    private String Tag;
    private String Msg;

    private DefaultMQProducer producer;



    public Arguments getDefaultParameters()
    {
        Arguments params = new Arguments();
//		params.addArgument("AccessKey", "");
//		params.addArgument("SecretKey", "");
        params.addArgument("NameServer", "");
        params.addArgument("Topic", "");
        params.addArgument("GroupName", "");
        params.addArgument("Tag", "");
        params.addArgument("Msg", "");
        return params;
    }

    public void setupTest(JavaSamplerContext context)
    {
//		AccessKey = context.getParameter("AccessKey");
//		SecretKey = context.getParameter("SecretKey");
        NameServer = context.getParameter("NameServer");
        Topic = context.getParameter("Topic");
        GroupName = context.getParameter("GroupName");
        Tag = context.getParameter("Tag");
        Msg = context.getParameter("Msg");

//		Properties properties = new Properties();
//		// AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
//		properties.put(PropertyKeyConst.AccessKey, AccessKey);
//		// SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
//		properties.put(PropertyKeyConst.SecretKey, SecretKey);
//		//设置发送超时时间，单位毫秒
//		properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
//		// 设置 TCP 接入域名，进入控制台的实例管理页面的“获取接入点信息”区域查看
//		properties.put(PropertyKeyConst.NAMESRV_ADDR, NameServer);
//		producer = ONSFactory.createProducer(properties);
//		// 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
//		producer.start();

        try{
            producer = new DefaultMQProducer(GroupName);
            // Specify name server addresses.
            producer.setNamesrvAddr(NameServer);
            //Launch the instance.
            producer.start();
        } catch (Exception e) {
            System.out.println("Start producer failed.");
            e.printStackTrace();
            //throw new Exception("start producer failed with " + e.toString());
        }
        producer.setRetryTimesWhenSendAsyncFailed(0);
    }


    public SampleResult runTest(JavaSamplerContext context)
    {
        SampleResult results = new SampleResult();
        results.sampleStart();
        Message msg = new Message(Topic, Tag, Msg.getBytes());
        // 设置代表消息的业务关键属性，请尽可能全局唯一。
        // 以方便您在无法正常收到消息情况下，可通过阿里云服务器管理控制台查询消息并补发
        // 注意：不设置也不会影响消息正常收发
        // msg.setKey("ORDERID_" + i);
        // async
        try {
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    //				System.out.printf("%-10d OK %s %n", index,
                    //						sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    //				System.out.printf("%-10d Exception %s %n", index, e);
                    //				e.printStackTrace();
                }
            });
            results.setSuccessful(true);
        }catch (Exception e){
            results.setSuccessful(false);
        }
        // sync
//		try {
//			SendResult sendResult = producer.send(msg);
//			results.setResponseData(sendResult.toString(), "utf-8");
//			// 同步发送消息，只要不抛异常就是成功
//			if (sendResult != null) {
//				results.setSuccessful(true);
//				//System.out.println(new Date() + " Send mq message success. Topic is:" + msg.getTopic() + " msgId is: " + sendResult.getMsgId());
//			}
//		}
//		catch (Exception e) {
//			results.setSuccessful(false);
//			// 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
//			//System.out.println(" Send mq message failed. Topic is:" + msg.getTopic());
//			e.printStackTrace();
//		}
        results.sampleEnd();
        return results;
    }


    public void teardownTest(JavaSamplerContext context)
    {
        producer.shutdown();
        super.teardownTest(context);
    }

}
