package dev.rsandtner.cloudpractitioner;

import software.amazon.awscdk.Environment;

public class Env {

    public static Environment get() {
        return Environment.builder()
                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                .region(System.getenv("CDK_DEFAULT_REGION"))
                .build();
    }

}
