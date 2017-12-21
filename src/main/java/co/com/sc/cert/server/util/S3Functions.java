package co.com.sc.cert.server.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.waiters.Waiter;
import com.amazonaws.waiters.WaiterParameters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Juan on 05/04/2017.
 */
public class S3Functions
{
    private static final String accessKey = "AKIAI24WVEJYQ4AUC53Q";
    private static final String secretKey = "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";
    private static final String bucketName = "bucket-cert";


    private static AWSStaticCredentialsProvider getCredentials()
    {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(credentials);
    }

    public static void uploadFile(String path, InputStream inputStream, int size, CannedAccessControlList accessControlList)
    {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(getCredentials()).withRegion(Regions.US_EAST_1).build();
        ObjectMetadata data = new ObjectMetadata();
        data.setContentLength(size);
        s3Client.putObject(new PutObjectRequest(bucketName, path, inputStream, data).withCannedAcl(accessControlList));
        Waiter waiter = s3Client.waiters().objectExists();
        waiter.run(new WaiterParameters<>(new GetObjectMetadataRequest(bucketName, path)));
    }

    public static void uploadPrivateFile(String path, InputStream inputStream, int size)
    {
        uploadFile(path, inputStream, size, CannedAccessControlList.Private);
    }

    public static void uploadPrivateFile(String path, byte[] bytes)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        uploadFile(path, bais, bytes.length, CannedAccessControlList.Private);
    }

    public static String uploadFile(String path, InputStream inputStream, int size) throws IOException
    {
        path = StringUtils.stripAccents(path);
        uploadFile(path, inputStream, size, CannedAccessControlList.PublicRead);
        return new URL("https://s3.amazonaws.com/" + bucketName + "/" + path).toString();
    }

    public static String uploadFile(String path, byte[] bytes) throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return uploadFile(path, bais, bytes.length);
    }



}
