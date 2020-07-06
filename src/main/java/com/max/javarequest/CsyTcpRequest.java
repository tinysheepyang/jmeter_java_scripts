package com.max.javarequest;

import com.max.javarequest.core.StringUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CsyTcpRequest extends AbstractJavaSamplerClient {

    private String host;

    private Integer port;

    private String apiHost;

    private String mac;

    private Socket socket;

    private OutputStream os;

    private InputStream is;

    private ByteArrayOutputStream bos;


    public Arguments getDefaultParameters()
    {
        Arguments params = new Arguments();
        params.addArgument("host", "");
        params.addArgument("port", "");
        params.addArgument("mac", "");
        params.addArgument("apiHost", "");
        return params;
    }

    public void setupTest(JavaSamplerContext context)
    {
        host = context.getParameter("host");
        port = context.getIntParameter("port");
        mac = context.getParameter("mac");
        apiHost = context.getParameter("apiHost");
        try
        {
            //String accessToken = Api.login(apiHost);
            //Api.bind(apiHost, accessToken, mac);
            socket = new Socket(host, port);
            socket.setSoTimeout(60000);
            os = socket.getOutputStream();
            is = socket.getInputStream();

            String hex0402 = "5A0026400100000001000B0104" + mac + "00000000010000000000000004020000000014DE";
            byte[] by0202 = sendMessage(StringUtil.hexStringToByteArray(hex0402));
            System.out.println(StringUtil.byteArrayToHexString(by0202));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public SampleResult runTest(JavaSamplerContext context)
    {
        SampleResult results = new SampleResult();
        results.sampleStart();
        try
        {
            String hex0408 = "5A0032400100000001000B0104" + mac + "0000000001000000000000000408000000000000000000000000000000007FFD";
            byte[] by0208 = sendMessage(StringUtil.hexStringToByteArray(hex0408));
            StringUtil.byteArrayToHexString(by0208);
            results.setSuccessful(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        results.sampleEnd();
        return results;
    }


    public void teardownTest(JavaSamplerContext context)
    {
        try
        {
            if (os != null)
            {
                os.close();
            }
            if (is != null)
            {
                is.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private byte[] sendMessage(byte[] data) throws IOException
    {
        os.write(data);
        //socket.shutdownOutput();
        bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count = 0;
        do
        {
            count = is.read(buffer);
            bos.write(buffer, 0, count);
        }
        while (is.available() != 0);
        return bos.toByteArray();
    }
}
