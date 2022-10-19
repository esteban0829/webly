package com.webClipBoard.config

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

enum class S3BucketType {
    WEB_CLIPBOARD
}

@Configuration
class S3Config {
    @Value("\${cloud.aws.credentials.accessKey}")
    private lateinit var awsAccessKey: String

    @Value("\${cloud.aws.credentials.secretKey}")
    private lateinit var awsSecretKey: String

    @Value("\${cloud.aws.region.static}")
    private lateinit var awsRegion: String

    @Value("\${my-app.aws.service-endpoint}")
    private lateinit var awsServiceEndpoint: String

    @Value("\${my-app.aws.bucket}")
    private lateinit var bucket: String

    lateinit var buckets: Map<S3BucketType, String>

    @PostConstruct
    fun init() {
        buckets = mapOf(
            S3BucketType.WEB_CLIPBOARD to bucket,
        )
    }

    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3Client.builder()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(awsServiceEndpoint, awsRegion))
            .withPathStyleAccessEnabled(true)
            .withClientConfiguration(ClientConfiguration().withSignerOverride("AWSS3V4SignerType"))
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(awsAccessKey, awsSecretKey)))
            .build()
    }
}